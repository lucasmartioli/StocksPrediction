/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stocksprediction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadingcompany.LoadingCompany;
import model.Company;
import model.CompanyList;
import neuralnetworks.PredictionNeuralNetwork;
import neuralnetworks.TrainingNeuralNetwork;
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
    public static void main(String[] args) throws InterruptedException {
        SimpleDateFormat formatter=new SimpleDateFormat("DD-MMM-yyyy");  

        try {
            Calendar di = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            Calendar df = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            Calendar dfuturo = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));

            Company vivo;
            Company vivo2;
            PredictionNeuralNetwork copelNet;

            for (int i = 0; i < 10; i++) {

                df.set(16, 2, 1 + i, 12, 0, 0);
                di.setTimeInMillis(df.getTimeInMillis());
                dfuturo.setTimeInMillis(df.getTimeInMillis());
                di.add(Calendar.DAY_OF_MONTH, -200);
                dfuturo.add(Calendar.DAY_OF_MONTH, 1);

                DateTime d2 = new DateTime(di.getTimeInMillis());
                DateTime d3 = new DateTime(dfuturo.getTimeInMillis());
                DateTime d = new DateTime(df.getTimeInMillis());
                System.out.println(d2.toString());
                System.out.println(d.toString());
                System.out.println(d3.toString());

//                for (int j = 0; j < CompanyList.getNumeroTotalDeEmpresas(); j++) {
//                    
//                }
                vivo = LoadingCompany.loading("PETR4", di, df);

                double[] result = TrainingNeuralNetwork.toPredict(vivo, vivo.getTechnicalIndicators().getTimeSeries().getEnd());

                vivo2 = LoadingCompany.loading("PETR4", df, dfuturo);               
//                for (int j = 1; j < ; j++) {
//                    
//                }
//                vivo2.getTechnicalIndicators().getClosePrice();
                if (vivo2.getTechnicalIndicators().getTimeSeries().getTickCount() > 0)
                    System.err.println("Resultado: " 
                            + 100 * result[0] + 
                            " Real futuro: " + 
                            vivo2.getTechnicalIndicators().getClosePrice().getValue(vivo2.getTechnicalIndicators().getTimeSeries().getTickCount() - 1)
                            + "DIFERENCA: " + 
                            ((100d * result[0]) - vivo2.getTechnicalIndicators().getClosePrice().getValue(vivo2.getTechnicalIndicators().getTimeSeries().getTickCount() - 1).toDouble()));
                
                else 
                    System.err.println("Nem DEU");

            }

        } catch (IOException ex) {
            Logger.getLogger(TrainingNetworks.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
