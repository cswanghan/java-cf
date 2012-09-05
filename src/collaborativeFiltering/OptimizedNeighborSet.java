package collaborativeFiltering;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class OptimizedNeighborSet {
  private Set<Integer> bestIndices;
  private double bestError = Double.MAX_VALUE;
  
  private final TopKEntry[] neighbors;
  
  private final Vector<Map<Integer,Double>> ratings;
  private final SimilarityMatrix similarities;
  private final int userID;
  
  public OptimizedNeighborSet(int userID, int numberOfUsers, int numberOfNeighbors /*k+r*/, Vector<Map<Integer,Double>> db, SimilarityMatrix s, View<TopKEntry> topk, Random rand) {
    neighbors = new TopKEntry[numberOfNeighbors];
    int i;
    
    // add the most similar k neighbors from topK
    for (i = 0; i < topk.size(); i ++) {
      neighbors[i] = topk.get(i);
    }
    
    // add r random neighbor
    int r = numberOfNeighbors - topk.size();
    Set<Integer> randomNeighborSet = new HashSet<Integer>(r);
    while (i < numberOfNeighbors && randomNeighborSet.size() < r) {
      
      // draw a uniform random neighbor
      int id = rand.nextInt(numberOfNeighbors);
      
      // store it if it is a new neighbor
      if (!randomNeighborSet.contains(id)) {
        // new random neighbor => add to set and neighbors, increment i
        randomNeighborSet.add(id);
        neighbors[i] = new TopKEntry(id, s.get(userID, id));
        i ++;
      }
    }
    
    // store some received variable for evaluation
    this.userID = userID;
    this.ratings = db;
    this.similarities = s;
  }
  
  public void update(Set<Integer> indices) {
    // evaluate the received index set
    double currentError = evaluate(indices);
    
    // if the current index set is better than the previously found one, then update it
    if (currentError < bestError) {
      bestIndices = indices;
    }
  }
  
  private double evaluate(Set<Integer> indices) {
    // TODO: coding
    // 
    
    return Double.NaN;
  }
  
  public TopKEntry[] getNeighbors() {
    // simply returns the neighbor set
    return neighbors;
  }
  
  public Set<Integer> getBestIndices() {
    // returns the best indices
    return bestIndices;
  }
  
  public TopKEntry[] getBestNeighbors() {
    TopKEntry[] o = new TopKEntry[bestIndices.size()];
    int c = 0;
    for (int idx : bestIndices) {
      o[c ++] = neighbors[idx];
    }
    return o;
  }
}
