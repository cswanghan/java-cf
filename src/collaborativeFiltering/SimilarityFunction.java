package collaborativeFiltering;

import java.util.Map;

public interface SimilarityFunction {
  public double computeSimilarity(Map<Integer, Double> a, Map<Integer, Double> b) throws Exception;

}
