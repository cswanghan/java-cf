package collaborativeFiltering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Predict {
  
  public static Vector<HashMap<Integer,Double>> readModel(File file, int k) throws IOException {
    Vector<HashMap<Integer,Double>> model = new Vector<HashMap<Integer,Double>>(100000);
    
    // open model file
    BufferedReader in = new BufferedReader(new FileReader(file));
    
    String line = in.readLine();
    int l = 0;
    while (line != null) {
      l ++;
      // parse line
      String[] splitedLine = line.split("\\s|,|:");
      
      // check input
      if (2*k+1 > splitedLine.length) {
        throw new IOException("Bad format of model at line " + l + "!");
      }
      
      // parse (uid,sim) pairs and put it into corresponding rating vector of the model
      HashMap<Integer,Double> rates = new HashMap<Integer,Double>();
      model.add(rates);
      for (int idx = 1; idx < splitedLine.length - 1 && idx < 2*k; idx += 2) {
        int uid = Integer.parseInt(splitedLine[idx]);
        double sim = Double.parseDouble(splitedLine[idx + 1]);
        rates.put(uid, sim);
      }
      
      line = in.readLine();
    }
    
    // close model file
    in.close();
    
    return model;
  }
  
  public static void main(String[] args) throws IOException {
    if (args.length == 4) {
      final int k = Integer.parseInt(args[3]);
      
      // read the training and evaluation databases
      Vector<Map<Integer,Double>> training = Train.readDB(new File(args[0]));
      Vector<Map<Integer,Double>> evaluation = Train.readDB(new File(args[2]));
      
      // read model (in for of baseUserID -> topk(userID, sim))
      Vector<HashMap<Integer,Double>> model = Predict.readModel(new File(args[1]), k);
      
      
      // compute user average ratings
      double[] avgs = new double[training.size()];
      for (int i = 0; i < training.size(); i ++) {
        avgs[i] = 0.0;
        for (int iid : training.get(i).keySet()) {
          avgs[i] += training.get(i).get(iid);
        }
        avgs[i] = (training.get(i).size() > 0) ? avgs[i] / ((double)training.get(i).size()) : 0.0;
      }
      
      // perform evaluation
      for (int uid = 0; uid < evaluation.size(); uid ++) {
        // get model of uid
        Map<Integer,Double> modelOfUid = model.get(uid);
        double avgOfUid = avgs[uid];
        
        for (int iid : evaluation.get(uid).keySet()) {
          double expectedRate = evaluation.get(uid).get(iid);
          
          double predictedRate = 0.0;
          double sumOfSimilarities = 0.0;
          
          // compute prediction
          for (int neighborId : modelOfUid.keySet()) {
            Map<Integer,Double> neighborRates = training.get(neighborId);
            Double neighborRateD = neighborRates.get(iid);
            if (neighborRateD != null) {
              double neighborSim = modelOfUid.get(neighborId);
              double neighborRate = neighborRateD.doubleValue();
              double avgOfNeighbor = avgs[neighborId];
              
              // coffee method
              predictedRate += neighborSim * (neighborRate - avgOfNeighbor);
              sumOfSimilarities += Math.abs(neighborSim);
            }
          }
          
          // predicted rate is computed
          predictedRate = (Math.abs(sumOfSimilarities) < 10.0E-8) ? 0.0 : predictedRate / sumOfSimilarities;
          predictedRate += avgOfUid;
          
          System.out.println(uid + "\t" + iid + "\t" + predictedRate + "\t" + expectedRate);
        }
      }
    } else {
      System.err.println("Usage: " + Predict.class.getCanonicalName() + " training model evaluation k");
    }
  }
}
