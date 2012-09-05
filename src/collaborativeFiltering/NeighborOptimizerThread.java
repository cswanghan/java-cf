package collaborativeFiltering;

import java.util.Set;

public class NeighborOptimizerThread implements Runnable {
  private final int from;
  private final int until;
  
  private OptimizedNeighborSets container;
  
  private final int minSize;
  
  public NeighborOptimizerThread(int f, int u, OptimizedNeighborSets c, int m) {
    from = f;
    until = u;
    container = c;
    minSize = m;
  }
  
  public void run() {
    try {
      SubSets subSets = new SubSets(container.size(), minSize, container.size());
      for (int baseUserID = from; baseUserID < until; baseUserID ++) {
        for (Set<Integer> subSet : subSets) {
          container.update(baseUserID, subSet);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
