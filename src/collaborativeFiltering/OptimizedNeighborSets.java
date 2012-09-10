package collaborativeFiltering;

import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class OptimizedNeighborSets {
  private OptimizedNeighborSet[] neighbors;
  
  public OptimizedNeighborSets(int numberOfUsers, int numberOfNeighbors /*k+r*/, Vector<Map<Integer,Double>> db, SimilarityMatrix similarities, TopK topk, Random rand) {
    neighbors = new OptimizedNeighborSet[numberOfUsers];
    for (int i = 0; i < neighbors.length; i ++) {
      neighbors[i] = new OptimizedNeighborSet(i, numberOfUsers, numberOfNeighbors, db, similarities, topk.get(i), rand);
    }
  }
  
  public void update(int i) {
    neighbors[i].update();
  }
  
  public TopKEntry[] get(int i) {
    return neighbors[i].getFinalEntries();
  }
  
  public int size() {
    return neighbors.length;
  }
  
  public OptimizedNeighborSet getNBS(int i) {
    return neighbors[i];
  }
  
  public synchronized void printStatus(String str) {
    System.err.println("\t" + str);
  }

}
