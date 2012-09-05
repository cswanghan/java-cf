package collaborativeFiltering;

import java.util.Set;

public class NeighborOptimizerThread implements Runnable {
  private final int from;
  private final int until;
  
  private OptimizedNeighborSets container;
  
  private final int minSize;
  private final int kpr;
  
  public NeighborOptimizerThread(int f, int u, OptimizedNeighborSets c, int m, int kplusr) {
    from = f;
    until = u;
    container = c;
    minSize = m;
    kpr = kplusr;
  }
  
  public void run() {
    try {
      SubSets subSets = new SubSets(kpr, minSize, kpr);
      for (int baseUserID = from; baseUserID < until; baseUserID ++) {
        for (Set<Integer> subSet : subSets) {
          container.update(baseUserID, subSet);
          print("compute for user " + baseUserID + " value " + subSet);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public synchronized void print(String msg) {
    System.err.println(Thread.currentThread().getName() + " - " + msg);
  }

}
