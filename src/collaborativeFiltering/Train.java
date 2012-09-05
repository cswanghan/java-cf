package collaborativeFiltering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;


public class Train {

  public static SimilarityMatrix computeSimilarities(SimilarityFunction sf, Vector<Map<Integer, Double>> matrix, int numberOfThreads) throws Exception {
    SimilarityMatrix container = new SimilarityMatrix(matrix.size());
    Thread[] threads = new Thread[numberOfThreads];
    
    long from = 0;
    long size = matrix.size();
    long step = ((size * (size - 1L)) / 2) / numberOfThreads;
    
    // create threads
    for (int i = 0; i < threads.length - 1; i ++) {
      //System.err.println(i + ". thread: " + from + "\t" + (from + step));
      threads[i] = new Thread(new SimilarityComputatorThread(from, from + step, matrix, sf, container));
      from += step;
    }
    //System.err.println((threads.length - 1) + ". thread: " + from + "\t" + ((size * (size - 1L)) / 2));
    threads[threads.length - 1] = new Thread(new SimilarityComputatorThread(from, ((size * (size - 1L)) / 2), matrix, sf, container));
    
    // start threads
    for (int i = 0; i < threads.length; i++) {
      threads[i].start();
    }
    // wait for finishing threads
    for (int i = 0; i < threads.length; i++) {
      threads[i].join();
    }
    
    return container;
  }
  
  public static TopK computeTopK(SimilarityMatrix matrix, int k, int numberOfThreads) throws Exception {
    TopK container = new TopK(matrix.size(), k);
    Thread[] threads = new Thread[numberOfThreads];
    
    int from = 0;
    int size = matrix.size();
    int step = size / numberOfThreads;
    
    // create threads
    for (int i = 0; i < threads.length - 1; i ++) {
      //System.err.println(i + ". thread: " + from + "\t" + (from + step));
      threads[i] = new Thread(new TopKComputatorThread(from, from + step, matrix, container));
      from += step;
    }
    //System.err.println((threads.length - 1) + ". thread: " + from + "\t" + ((size * (size - 1L)) / 2));
    threads[threads.length - 1] = new Thread(new TopKComputatorThread(from, size, matrix, container));
    
    // start threads
    for (int i = 0; i < threads.length; i++) {
      threads[i].start();
    }
    // wait for finishing threads
    for (int i = 0; i < threads.length; i++) {
      threads[i].join();
    }
    
    return container;
  }
  
  public static Vector<Map<Integer,Double>> readDB(File file) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(file));
    Map<Integer,Integer> uid2raw = new HashMap<Integer,Integer>();
    Vector<Map<Integer,Double>> rawData = new Vector<Map<Integer,Double>>();
    int prevUid = -1;
    int maxUid = Integer.MIN_VALUE;
    String line = in.readLine();
    while (line != null) {
      String[] s = line.split("\\s+");
      
      int uid = Integer.parseInt(s[0]) - 1;
      int iid = Integer.parseInt(s[1]) - 1;
      double rate = Double.parseDouble(s[2]);
      
      if (prevUid != uid) {
        rawData.add(new TreeMap<Integer,Double>());
        uid2raw.put(uid, rawData.size() - 1);
        if (uid > maxUid) {
          maxUid = uid;
        }
      }
      rawData.get(rawData.size() - 1).put(iid, rate);
      
      prevUid = uid;;
      line = in.readLine();
    }
    in.close();
    
    // create final raw data
    Vector<Map<Integer,Double>> db = new Vector<Map<Integer,Double>>(maxUid + 1);
    for (int i = 0; i <= maxUid; i ++) {
      Integer rId = uid2raw.get(i);
      if (rId != null) {
        db.add(rawData.get(rId));
      } else {
        db.add(new TreeMap<Integer,Double>());
      }
    }
    
    return db;
  }
  
  
  public static void main(String[] args) throws Exception {
    if (args.length == 4) {
      final int k = Integer.parseInt(args[2]);
      final int numberOfThreads = Integer.parseInt(args[3]);
      
      // read the database
      Vector<Map<Integer,Double>> db = readDB(new File(args[0]));
      
      // compute similarities
      SimilarityMatrix similarities = Train.computeSimilarities(new CosineSimilarity(), db, numberOfThreads);
      
      // compute top K similar nodes in O(k^2*n)
      TopK topk = Train.computeTopK(similarities, k, numberOfThreads);
      
      // write model
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(args[1]))));
      for (int i = 0; i < topk.size(); i ++) {
        out.println(i + "\t" + topk.get(i));
      }
      out.close();
      
    } else {
      System.err.println("Usage: " + Train.class.getCanonicalName() + " training model k threads");
    }
    
  }
}
