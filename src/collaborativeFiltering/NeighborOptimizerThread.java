package collaborativeFiltering;

import java.util.Set;
import java.util.TreeSet;

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
  
  private static Set<Integer> cloneSet(Set<Integer> set) {
    Set<Integer> ret = new TreeSet<Integer>();
    for (int i : set) {
      ret.add(i);
    }
    return ret;
  }
  
  public void run() {
    try {
      //SubSets subSets = new SubSets(kpr, minSize, kpr);
      for (int baseUserID = from; baseUserID < until; baseUserID ++) {
        boolean run = true;
        while(run) {
          run = false;
          // add neighbor
          Set<Integer> upd = cloneSet(container.getNBS(baseUserID).getBestIndices());
          for (int i = 0; i < kpr; i++) {
            if (upd.contains(i)) {
              continue;
            }
            upd.add(i);
            if (container.update(baseUserID, upd)) {
              run = true;
            } else {
              upd.remove(i);
            }
          }
          
          // remove neighbor
          for (int i = 0; i < kpr; i++) {
            if (upd.contains(i)) {
              upd.remove(i);
              if (container.update(baseUserID, upd)) {
                run = true;
              } else {
                upd.add(i);
              }
            }
          }
          
        }
        /*
        
        for (Set<Integer> subSet : subSets) {
          container.update(baseUserID, subSet);
          print("compute for user " + baseUserID + " value " + subSet);
        }*/
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public synchronized void print(String msg) {
    System.err.println(Thread.currentThread().getName() + " - " + msg);
  }

}
