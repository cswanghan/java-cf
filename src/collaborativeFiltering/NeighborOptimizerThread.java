package collaborativeFiltering;


public class NeighborOptimizerThread implements Runnable {
  private final int from;
  private final int until;
  
  private OptimizedNeighborSets container;
  
  public NeighborOptimizerThread(int f, int u, OptimizedNeighborSets c) {
    from = f;
    until = u;
    container = c;
  }
  
  public void run() {
    try {
      for (int baseUserID = from; baseUserID < until; baseUserID ++) {
        container.update(baseUserID);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public synchronized void print(String msg) {
    System.err.println(Thread.currentThread().getName() + " - " + msg);
  }

}
