package collaborativeFiltering;


public class TopKComputatorThread implements Runnable {
  private final int from;
  private final int until;
  private final SimilarityMatrix matrix;
  private TopK container;
  
  public TopKComputatorThread(int f, int u, SimilarityMatrix m, TopK c) {
    from = f;
    until = u;
    matrix = m;
    container = c;
  }
  
  public void run() {
    try {
      for (int baseUserID = from; baseUserID < until; baseUserID ++) {
        for (int userID = 0; userID < matrix.size(); userID ++) {
          if (baseUserID != userID) {
            container.add(baseUserID, userID, matrix.get(baseUserID, userID));
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
