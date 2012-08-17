package collaborativeFiltering;

import java.util.Map;
import java.util.Vector;

public class SimilarityComputatorThread implements Runnable {
  private final long from;
  private final long until;
  private final Vector<Map<Integer, Double>> matrix;
  private final SimilarityFunction similarity;
  private SimilarityMatrix container;
  
  public SimilarityComputatorThread(long f, long u, Vector<Map<Integer, Double>> m, SimilarityFunction s, SimilarityMatrix c) {
    from = f;
    until = u;
    matrix = m;
    similarity = s;
    container = c;
  }
  

  public void run() {
    try {
      for (long k = from; k < until; k ++) {
        long iL = (long) ((1.0 + Math.sqrt(1.0 + 8.0 * k)) / 2.0);
        int j = (int) (k - ((iL*(iL-1))/2));
        int i = (int) iL;
          
        double s = similarity.computeSimilarity(matrix.get(i), matrix.get(j));
        
        container.set(i, j, s);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}