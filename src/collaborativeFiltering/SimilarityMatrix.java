package collaborativeFiltering;

import java.io.Serializable;

public class SimilarityMatrix implements Serializable {
  private static final long serialVersionUID = 2933792525859741713L;
  
  private double[][] s = null;
  
  public SimilarityMatrix(int size) {
    s = new double[size][];
    
    for (int i = 0; i < size; i++){
      s[i] = new double[i];
    }
  }
  
  public int size() {
    return s.length;
  }
  
  public double get(int i, int j) {
    if (0 <= i && 0 <= j && i < s.length && j < s.length) {
      if (i == j) {
        return 1.0;
      } else if (i > j) {
        return s[i][j];
      } else if (i < j) {
        return s[j][i];
      }
    }
    return Double.NaN;
  }
  
  public void set(int i, int j, double value) {
    if (0 <= i && 0 <= j && i < s.length && j < s.length) {
      if (i > j) {
        s[i][j] = value;
      } else if (i < j) {
        s[j][i] = value;
      }
    }
  }
  
  public double[][] getSimilarityMatrix() {
    return s;
  }
}
