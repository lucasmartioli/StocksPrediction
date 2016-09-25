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
import neuralnetworks.NeuralNetworks;

/**
 *
 * @author Lucas
 */
public class TrainingNetworks {

    public static void main(String[] args) {

        try {
            Calendar di = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            di.set(14, 5, 1, 12, 0);
            Calendar df = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            df.set(16, 6, 1, 12, 0);

            Company petrobras = LoadingCompany.loading("PETR4", di, df);
            NeuralNetworks petrobrasNN = new NeuralNetworks(petrobras);
            petrobrasNN.toTrain();
        } catch (IOException ex) {
            Logger.getLogger(TrainingNetworks.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
