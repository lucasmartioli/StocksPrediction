/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stocksprediction;

import eu.verdelhan.ta4j.TimeSeries;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadingcompany.LoadingCompany;
import model.Company;
import neuralnetworks.TrainingNeuralNetwork;
import technicalindicators.TechnicalIndicators;

/**
 *
 * @author Lucas
 */
public class TestsResults {

    public static void main(String[] args) {

        try {
            Calendar di = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            di.set(13, 5, 1, 12, 0);
            Calendar df = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            df.set(16, 9, 15, 12, 0);

            Company vivo = LoadingCompany.loading("VIVT4", di, df);
            
//            TechnicalIndicators technicalIndicators = vivo.getTechnicalIndicators();
            TimeSeries timeSeries = vivo.getTechnicalIndicators().getTimeSeries();
            int indicadorInicial = timeSeries.getBegin();
            int indicadorFinal = timeSeries.getEnd();
            
            
            
            indicadorInicial += 30; // desconsiderando valores iniciais onde os indicadores não fazem tanto sentido.
            
            int inicioTreinamento = indicadorInicial;
            int finalTreinamento = Math.round(((float)(indicadorFinal * 0.66d)));
            int inicioTestes = finalTreinamento + 1;
            int finalTestes = indicadorFinal;
            
            //TrainingNeuralNetwork.toTrain(vivo, inicioTreinamento, finalTreinamento);
            
            for (int i = inicioTestes; i <= finalTestes; i++) {
                
                double saida = TrainingNeuralNetwork.toPredict(vivo, i);
                System.out.println("Saida do algoritmo: " +  saida * 100);                
                System.out.println("");                                
                System.out.println("Dia sendo analisado: " + timeSeries.getTick(i));
                if (i + 5 <= finalTestes)
                    System.out.println("Diferença: " + ((saida * 100) - timeSeries.getTick(i + 5).getClosePrice().toDouble()) + " Dia no futuro: " + timeSeries.getTick(i + 5));
                else
                    System.out.println("Não tem como saber o futuro ainda!!");
                System.out.println("");
            }               
            
        } catch (IOException ex) {
            Logger.getLogger(TrainingNetworks.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
