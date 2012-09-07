package collaborativeFiltering;

import java.io.Serializable;

public class TopKEntry implements Serializable, Comparable<TopKEntry> {
  private static final long serialVersionUID = 1118924373305622093L;
  private final int userID;
  private final double similarity;
  
  public TopKEntry(int u, double s) {
    userID = u;
    similarity = s;
  }
  
  public int getUserID() {
    return userID;
  }
  
  public double getSimilarity() {
    return similarity;
  }
  
  public String toString() {
    return userID + ":" + similarity;
  }
  
  public int compareTo(TopKEntry o) {
    if (similarity < o.similarity) {
      return +1;
    } else if (similarity > o.similarity) {
      return -1;
    }
    return 0;
  }
  
  public boolean equals(Object o) {
    if (o instanceof TopKEntry && ((TopKEntry)o).userID == userID) {
      return true;
    }
    return false;
  }
}