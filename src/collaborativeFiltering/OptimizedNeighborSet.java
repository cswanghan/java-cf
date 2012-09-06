package collaborativeFiltering;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

public class OptimizedNeighborSet {
  private Set<Integer> bestIndices = new HashSet<Integer>();
  private double bestError = Double.MAX_VALUE;
  
  private final View<TopKEntry> topk;
  private final View<TopKEntry> topr;
  
  private final Vector<Map<Integer,Double>> ratings;
  private final SimilarityMatrix similarities;
  private final int userID;
  
  private final Vector<Double> avgs;
  
  public OptimizedNeighborSet(int userID, int numberOfUsers, int numberOfNeighbors /*k+r*/, Vector<Map<Integer,Double>> db, SimilarityMatrix s, View<TopKEntry> topk, Random rand) {
    // store some received variable for evaluation
    this.userID = userID;
    this.ratings = db;
    this.similarities = s;
    
    // compute the avg. preds. for users
    avgs = new Vector<Double>();
    for (int i = 0; i < ratings.size(); i++) {
      Map<Integer, Double> row = ratings.get(i);
      double avg = 0.0;
      for (int iid : row.keySet()) {
        avg += row.get(iid);
      }
      avgs.add(row.size() == 0.0 ? 0.0 : avg/row.size());
    }
    
    int r = numberOfNeighbors - topk.size();
    this.topk = topk;
    this.topr = new View<TopKEntry>(r);
    
    // add r random neighbor
    Set<Integer> randomNeighborSet = new HashSet<Integer>(r);
    while (randomNeighborSet.size() < r) {
      
      // draw a uniform random neighbor
      int id = rand.nextInt(numberOfNeighbors);
      
      // store it if it is a new neighbor
      if (!randomNeighborSet.contains(id)) {
        // new random neighbor => add to set and neighbors, increment i
        randomNeighborSet.add(id);
        topr.insert(new TopKEntry(id, similarities.get(userID, id)));
      }
    }
  }
  
  public void update() {
  }
  
  private Set<Integer> getEvalSet() {
    Set<Integer> set = new TreeSet<Integer>();
    set.add(userID);
    return set;
  }
  
  /**
   * Computes the absolute error for the current user based on its neighbors and the 
   * specified cut point.
   * @param currN the cut point
   * @return absolute error
   */
  private double error(int currN) {
    double err = 0.0;
    double c = 0.0;
    
    for (int uid : getEvalSet()) {
      for (int itemID : ratings.get(uid).keySet()) {
        double exp = ratings.get(uid).get(itemID);
        double pred = 0.0;
        double sumSim = 0.0;
        
        for (int i = 0; i < topk.size(); i++) {
          int nuid = (i < topk.size() - currN) ? topk.get(i).getUserID() : topr.get(i + currN - topk.size()).getUserID();
          Double nDRate = ratings.get(nuid).get(itemID);
          double nRate = nDRate == null ? 0.0 : nDRate;
          double uAvg = avgs.get(nuid);
          double sim = similarities.get(uid, nuid);
          
          pred += sim * (nRate - uAvg);
          sumSim += Math.abs(sim);
        }
        
        pred = sumSim == 0.0 ? 0.0 : pred / sumSim + avgs.get(uid);
        c++;
        err += Math.abs(exp - pred);
      }
    }
    
    return err / c;
  }
  
  public TopKEntry[] getNeighbors() {
    // simply returns the neighbor set
    return neighbors;
  }
  
  public Set<Integer> getBestIndices() {
    // returns the best indices
    return bestIndices;
  }
  
  public TopKEntry[] getFinal() {
    TopKEntry[] o = new TopKEntry[bestIndices.size()];
    int c = 0;
    for (int idx : bestIndices) {
      o[c ++] = neighbors[idx];
    }
    return o;
  }
  
}
