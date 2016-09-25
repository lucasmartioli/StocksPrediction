/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stocksprediction;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadingcompany.LoadingCompany;
import model.Company;
import neuralnetworks.NeuralNetworks;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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
            Calendar di = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            Calendar df = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            Calendar dfuturo = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            

            Company petrobras;
            NeuralNetworks petrobrasNN;

            for (int i = 1; i < 10; i++) {
                
                df.set(16, 8, 10 + i, 0, 0, 0);                      
                di.setTimeInMillis(df.getTimeInMillis());
                dfuturo.setTimeInMillis(df.getTimeInMillis());
                di.add(Calendar.DAY_OF_MONTH, -2000);
                dfuturo.add(Calendar.DAY_OF_MONTH, 7);
                        
                DateTime d2 = new DateTime(di.getTimeInMillis(), DateTimeZone.UTC);                
                DateTime d3 = new DateTime(dfuturo.getTimeInMillis(), DateTimeZone.UTC);                
                DateTime d = new DateTime(df.getTimeInMillis(), DateTimeZone.UTC);                
                System.out.println(d2.toString());
                System.out.println(d.toString());
                System.out.println(d3.toString());
                petrobras = LoadingCompany.loading("PETR4", di, df);

                petrobrasNN = new NeuralNetworks(petrobras);
                petrobrasNN.toPredict();

            }

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
