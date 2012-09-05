package collaborativeFiltering;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;


public class SubSets implements Iterable<Set<Integer>>, Iterator<Set<Integer>>{
  
  private final int n;
  private final int min;
  private final int max;
  private TreeSet<Integer> s;
  
  public SubSets(int n) {
    this(n, 0, n);
  }
  
  public SubSets(int n, int min, int max) {
    this.n = n;
    this.min = min;
    this.max = max;
    s = null;
  }

  @Override
  public Iterator<Set<Integer>> iterator() {
    return new SubSets(n, min, max);
  }
  
  private boolean step() {
    boolean ret = false;
    int i;
    for (i = 0; i < n && s.contains(i); i++) {
      s.pollFirst();
    }
    if (i < n) {
      s.add(i);
      ret = true;
    }
    return ret;
  }
  
  @Override
  public boolean hasNext() {
    boolean ret = false;
    if (s == null) {
      s = new TreeSet<Integer>();
      ret = true;
    } else {
      ret = step();
    }
    while (ret && (s.size() < min || s.size() > max)) {
      ret = step();
    }
    return ret;
  }

  @Override
  public Set<Integer> next() {
    return s;
  }

  @Override
  public void remove() {
    throw new RuntimeException("Not Supported!");
  }
  
  public static void main(String[] args) {
    SubSets sets = new SubSets(4, 2, 2);
    int i = 1;
    for (Set<Integer> set : sets) {
      System.out.println(i + "\t" + set);
      i++;
    }
  }

}
