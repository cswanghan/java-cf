package collaborativeFiltering;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class OptimizedNeighborSet {
  private final View<TopKEntry> topk;
  private final View<TopKEntry> topr;
  
  private final Vector<Map<Integer,Double>> ratings;
  private final SimilarityMatrix similarities;
  private final int userID;
  
  private double bestError = Double.MAX_VALUE;
  private int n;
  
  private final Vector<Double> avgs;
  
  public OptimizedNeighborSet(int userID, int numberOfUsers, int numberOfNeighbors /*k+r*/, Vector<Map<Integer,Double>> db, SimilarityMatrix s, View<TopKEntry> topk, Random rand) {
    // store some received variable for evaluation
    this.userID = userID;
    this.ratings = db;
    this.similarities = s;
    final int r = numberOfNeighbors - topk.size();
    this.topk = topk;
    this.topr = new View<TopKEntry>(r);
    
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
  
  private Set<Integer> bestNbrIndices = new TreeSet<Integer>();
  
  public void upd() {
    bestNbrIndices.clear();
    for (int i = 0; i < topk.size(); i++) {
      bestNbrIndices.add(i);
    }
    
    boolean run = true;
    while(run) {
      run = false;
      // add neighbor
      //print(baseUserID + " begining: " + upd);
      for (int i = 0; i < topk.size() + topr.size(); i++) {
        if (bestNbrIndices.contains(i)) {
          continue;
        }
        bestNbrIndices.add(i);
        double err = error(bestNbrIndices);
        //print(baseUserID + " i added: " + upd);
        if (err < bestError) {
          bestError = err;
          run = true;
        } else {
          //print("remove i="+i);
          bestNbrIndices.remove(i);
        }
      }
      //print(baseUserID + " after add: " + upd);
      
      // remove neighbor
      for (int i = 0; i < topk.size() + topr.size(); i++) {
        if (bestNbrIndices.contains(i)) {
          bestNbrIndices.remove(i);
          double err = error(bestNbrIndices);
          if (err < bestError) {
            bestError = err;
            run = true;
          } else {
            bestNbrIndices.add(i);
          }
        }
      }
    }
  }
  
  public void update() {
    for (int currN = 0; currN < topk.size(); currN ++) {
      double e = error(currN);
      if (e < bestError) {
        bestError = e;
        n = currN;
        bestNbrIndices.clear();
        for (int i = 0; i < topk.size(); i++) {
          int index = (i < topk.size() - n) ? i : i + n;
          bestNbrIndices.add(index);
        }
      }
    }
  }
  
  private Set<Integer> getEvalSet() {
    Set<Integer> set = new TreeSet<Integer>();
    set.add(userID);
    int topkCounter = 10;
    for (int i = 0; i < topk.size() && i < topkCounter; i++) {
      set.add(topk.get(i).getUserID());
    }
    return set;
  }
  
  private double error(Set<Integer> nIndices) {
    double err = 0.0;
    double c = 0.0;
    
    for (int uid : getEvalSet()) {
      for (int itemID : ratings.get(uid).keySet()) {
        double exp = ratings.get(uid).get(itemID);
        double pred = 0.0;
        double sumSim = 0.0;
        
        for (int index : nIndices) {
          int nuid = (index < topk.size()) ? topk.get(index).getUserID() : topr.get(index - topk.size()).getUserID();
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
  
  /**
   * Computes the absolute error for the current user based on its neighbors and the 
   * specified cut point.
   * @param currN the cut point
   * @return absolute error
   */
  private double error(int currN) {
    Set<Integer> nIndices = new TreeSet<Integer>();
    for (int i = 0; i < topk.size(); i++) {
      int index = (i < topk.size() - currN) ? i : currN + i;
      nIndices.add(index);
    }
    
    return error(nIndices);
  }
  
  public TopKEntry[] getFinalEntries() {
    TopKEntry[] model = new TopKEntry[topk.size()];
    int c = 0;
    for (int i : bestNbrIndices) {
      model[c] = (i < topk.size()) ? topk.get(i) : topr.get(i - topk.size());
      c++;
    }
    /*
    // get part from topk
    for (int i = 0; i < topk.size() - n; i++) {
      model[i] = topk.get(i);
    }
    // get part from topr
    for (int i = 0; i < n; i++) {
      model[i + topk.size() - n] = topr.get(i);
    }*/
    return model;
  }
  
}
