package collaborativeFiltering;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class OptimizedNeighborSets {
  private OptimizedNeighborSet[] neighbors;
  
  public OptimizedNeighborSets(int numberOfUsers, int numberOfNeighbors /*k+r*/, Vector<Map<Integer,Double>> db, SimilarityMatrix similarities, TopK topk, Random rand) {
    neighbors = new OptimizedNeighborSet[numberOfUsers];
    for (int i = 0; i < neighbors.length; i ++) {
      neighbors[i] = new OptimizedNeighborSet(i, numberOfUsers, numberOfNeighbors, db, similarities, topk.get(i), rand);
    }
  }
  
  public boolean update(int i, Set<Integer> indices) {
    return neighbors[i].update(indices);
  }
  
  public TopKEntry[] get(int i) {
    return neighbors[i].getBestNeighbors();
  }
  
  public int size() {
    return neighbors.length;
  }
  
  public OptimizedNeighborSet getNBS(int i) {
    return neighbors[i];
  }

}
