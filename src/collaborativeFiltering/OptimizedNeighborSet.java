package collaborativeFiltering;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class OptimizedNeighborSet {
  private Set<Integer> bestIndices = new HashSet<Integer>();
  private double bestError = Double.MAX_VALUE;
  
  private final TopKEntry[] neighbors;
  private final Vector<Map<Integer,Double>> ratings;
  private final Vector<Double> avgs;
  private final SimilarityMatrix similarities;
  private final int userID;
  private final int k;
  
  public OptimizedNeighborSet(int userID, int numberOfUsers, int numberOfNeighbors /*k+r*/, Vector<Map<Integer,Double>> db, SimilarityMatrix s, View<TopKEntry> topk, Random rand) {
    // store some received variable for evaluation
    this.userID = userID;
    this.ratings = db;
    this.similarities = s;
    int i;
    
    // compute the avg. preds. for users
    avgs = new Vector<Double>();
    for (i = 0; i < ratings.size(); i++) {
      Map<Integer, Double> row = ratings.get(i);
      double avg = 0.0;
      for (int iid : row.keySet()) {
        avg += row.get(iid);
      }
      avgs.add(row.size() == 0.0 ? 0.0 : avg/row.size());
    }
    
    
    neighbors = new TopKEntry[numberOfNeighbors];
    
    // add the most similar k neighbors from topK
    for (i = 0; i < topk.size(); i ++) {
      neighbors[i] = topk.get(i);
      bestIndices.add(i);
    }
    update(bestIndices);
    k = topk.size();
    
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
  }
  
  public boolean update(Set<Integer> indices) {
    // evaluate the received index set
    double currentError = evaluate(indices);
    
    // if the current index set is better than the previously found one, then update it
    if (currentError < bestError) {
      bestIndices = indices;
      return true;
    }
    return false;
  }
  
  private double error(int uid, Set<Integer> indices) {
    double MAE = 0.0;
    if (ratings == null) {
      System.err.println("Ratings is null!!!");
    }
    if (ratings.get(uid) == null) {
      return 0.0;
    }
    double size = ratings.get(uid).size();
    for (int iid : ratings.get(uid).keySet()) {
      double exp = ratings.get(uid).get(iid);
      double pred = 0.0;
      double sumSim = 0.0;
      for (int index : indices) {
        int u = neighbors[index].getUserID();
        if (uid != u) {
          Double nDRate = ratings.get(u).get(iid);
          double nRate = nDRate == null ? 0.0 : nDRate;
          double uAvg = avgs.get(u);
          double sim = similarities.get(uid, u);
          
          pred += sim * (nRate - uAvg);
          sumSim += Math.abs(sim);
        }
      }
      pred = sumSim == 0.0 ? 0.0 : pred / sumSim + avgs.get(uid);
      MAE += Math.abs(exp - pred);
    }
    return size == 0.0 ? 0.0 : MAE / size;
  }
  
  private double evaluate(Set<Integer> indices) {
    // Compute prediction error for topK and current users
    TreeMap<Integer, Double> MAEs = new TreeMap<Integer, Double>();
    MAEs.put(userID, error(userID, indices));
    for (int i = 0; i < k; i++) {
      int uid = neighbors[i].getUserID();
      double value = error(uid, indices);
      MAEs.put(uid, value);
    }
    
    // linearly weighting and summing MAEs
    double ret = 0.0;
    double sumSim = 0.0;
    for (int uid : MAEs.keySet()) {
      ret += similarities.get(uid, userID) * MAEs.get(uid);
      sumSim += Math.abs(similarities.get(uid, userID));
    }
    
    return sumSim == 0.0 ? 0.0 : ret / sumSim;
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
