/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stocksprediction;

import java.io.IOException;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadingcompany.LoadingCompany;
import model.Company;
import neuralnetworks.PredictionNeuralNetwork;
import neuralnetworks.TrainingNeuralNetwork;

/**
 *
 * @author Lucas
 */
public class TrainingNetworks {

    public static void main(String[] args) {

        try {
            Calendar di = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            di.set(14, 0, 1, 12, 0);
            Calendar df = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            df.set(16, 8, 1, 12, 0);

            Company copel = LoadingCompany.loading("CPLE6", di, df);
            TrainingNeuralNetwork.toTrain(copel);
            
        } catch (IOException ex) {
            Logger.getLogger(TrainingNetworks.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
