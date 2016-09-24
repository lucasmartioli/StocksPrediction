/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stocksprediction;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadingcompany.LoadingCompany;
import model.Company;
import neuralnetworks.NeuralNetworks;

/**
 *
 * @author Lucas
 */
public class StocksPrediction {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        try {
            Calendar di = Calendar.getInstance();
            Calendar df = Calendar.getInstance();
            di.set(16, 6, 1);
            di.set(16, 8, 1);
            
            Company petrobras = LoadingCompany.loading("PETR4", di, df);
            NeuralNetworks petrobrasNN = new NeuralNetworks(petrobras);
            petrobrasNN.toPredict();
        } catch (IOException ex) {
            Logger.getLogger(TrainingNetworks.class.getName()).log(Level.SEVERE, null, ex);
        }

        //NeuralNetworks t = new NeuralNetworks();
//        com.tictactec.ta.lib.Core c = new com.tictactec.ta.lib.Core();
//        double[] ddd = {3, 10, 10, 10, 1, 10, 10, 10};
//        MInteger outBagIdx = new MInteger();
//        MInteger outNBElement = new MInteger();                
//        double[] outReal = new double[60];
//        
//        c.movingAverage(0, (ddd.length - 1), ddd, 2, MAType.Tema, outBagIdx, outNBElement, outReal);
//        System.out.println(ddd.length);
//        System.out.println(outReal.length);
//        System.out.println("BAG " + outBagIdx.value);
//        System.out.println(outNBElement.value);
//        int n = 0;
//        for (double d : outReal) {
//            n++;
//            System.out.print("id: " + n + " ");
//            System.out.println(d);
//            
//        }
    }

}
