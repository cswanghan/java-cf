package collaborativeFiltering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Vector;

public class OptimizedLearner {

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {
    if (args.length == 5) {
      final int k = Integer.parseInt(args[2]);
      final int r = Integer.parseInt(args[3]);
      final int numberOfThreads = Integer.parseInt(args[4]);
      
      // read the database
      Vector<Map<Integer,Double>> db = Train.readDB(new File(args[0]));
      
      // compute similarities
      SimilarityMatrix similarities = Train.computeSimilarities(new CosineSimilarity(), db, numberOfThreads);
      
      // compute top K similar nodes in O(k^2*n)
      TopK topk = Train.computeTopK(similarities, k, numberOfThreads);
      
      // 
      
      // write model
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(new File(args[1]))));
      for (int i = 0; i < topk.size(); i ++) {
        out.println(i + "\t" + topk.get(i));
      }
      out.close();
      
    } else {
      System.err.println("Usage: " + Train.class.getCanonicalName() + " training model k r threads");
    }

  }

}
