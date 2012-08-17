package collaborativeFiltering;

public class TopK {
  private View<TopKEntry>[] topks;
  
  @SuppressWarnings("unchecked")
  public TopK(int numberOfUsers, int k) {
    topks = new View[numberOfUsers];
    for (int i = 0; i < topks.length; i ++) {
      topks[i] = new View<TopKEntry>(k);
    }
  }
  
  public void add(int i, int j, double s) {
    topks[i].insert(new TopKEntry(j, s));
  }
  
  public View<TopKEntry> get(int i) {
    return topks[i];
  }
  
  public int size() {
    return topks.length;
  }
}



