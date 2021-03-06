package collaborativeFiltering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class OptimizedLearner {

  public static OptimizedNeighborSets optimizeNeighbors(Vector<Map<Integer,Double>> ratings, SimilarityMatrix similarities, TopK topk, int k, int r, Random rand, int minSize, int numberOfThreads) throws InterruptedException {
    if (ratings == null) {
      System.err.println("Ratings is null in static call");
    }
    OptimizedNeighborSets container = new OptimizedNeighborSets(ratings.size(), k+r, ratings, similarities, topk, rand);
    Thread[] threads = new Thread[numberOfThreads];
    
    int from = 0;
    int size = ratings.size();
    int step = size / numberOfThreads;
    
    // create threads
    for (int i = 0; i < threads.length - 1; i ++) {
      threads[i] = new Thread(new NeighborOptimizerThread(from, from + step, container));
      from += step;
    }
    threads[threads.length - 1] = new Thread(new NeighborOptimizerThread(from, size, container));
    
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
  
  public static String asString(TopKEntry[] n) {
    StringBuffer b = new StringBuffer();
    for (int i = 0; i < n.length; i++) {
      b.append(n[i].getUserID()).append(':').append(n[i].getSimilarity());
      if (i < n.length - 1) {
        b.append(',');
      }
    }
    return b.toString();
  }
  
  public static void main(String[] args) throws Exception {
    if (args.length == 6) {
      final int k = Integer.parseInt(args[2]);
      final int r = Integer.parseInt(args[3]);
      final long seed = Long.parseLong(args[4]);
      final Random rand = new Random(seed);
      final int numberOfThreads = Integer.parseInt(args[5]);
      
      System.err.print("Reading database...\t");
      // read the database
      Vector<Map<Integer,Double>> db = Train.readDB(new File(args[0]));
      
      System.err.print("OK\nComputing similarities...\t");
      // compute similarities
      SimilarityMatrix similarities = Train.computeSimilarities(new CosineSimilarity(), db, numberOfThreads);
      
      System.err.print("OK\nComputing topK neighbors...\t");
      // compute top K similar nodes in O(k^2*n)
      TopK topk = Train.computeTopK(similarities, k, numberOfThreads);
      
      System.err.println("OK\nOptimizing neghbor sets...");
      // optimize neighbor set
      int minSize = k;
      OptimizedNeighborSets neighbors = OptimizedLearner.optimizeNeighbors(db, similarities, topk, k, r, rand, minSize, numberOfThreads);
      
      System.err.print("Writing the output to file...\t");
      // write model
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(args[1]))));
      for (int i = 0; i < neighbors.size(); i ++) {
        out.println(i + "\t" + asString(neighbors.get(i)));
      }
      out.close();
      System.err.println("OK");
      
    } else {
      System.err.println("Usage: " + Train.class.getCanonicalName() + " training model k r seed threads");
    }

  }

}
