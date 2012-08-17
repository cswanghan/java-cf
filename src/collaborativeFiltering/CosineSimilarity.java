package collaborativeFiltering;

import java.util.Map;

public class CosineSimilarity implements SimilarityFunction {

  public double computeSimilarity(Map<Integer, Double> a, Map<Integer, Double> b) throws Exception {
    double result = 0.0;
    
    // compute inner product of a and b
    double sum = 0.0;
    for (int keya : a.keySet()){
      if (b.containsKey(keya)){
        sum += a.get(keya) * b.get(keya);
      }
    }
    if (sum != 0.0){
      // compute norm of vector a
      double diva = 0.0;
      for (int key : a.keySet()) diva += a.get(key) * a.get(key);
      diva = Math.sqrt(diva);
      
      // compute norm of vector b
      double divb = 0.0;
      for (int key : b.keySet()) divb += b.get(key) * b.get(key);
      divb = Math.sqrt(divb);
      
      result = sum / (diva * divb);
    }
    
    return result;
  }

}
