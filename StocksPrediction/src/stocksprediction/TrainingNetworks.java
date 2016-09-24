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
public class TrainingNetworks {

    public static void main(String[] args) {

        try {
            Calendar di = Calendar.getInstance();
            di.set(14, 0, 1);
            Calendar df = Calendar.getInstance();
            df.set(16, 0, 1);
            
            Company petrobras = LoadingCompany.loading("PETR4",di,df);
            NeuralNetworks petrobrasNN = new NeuralNetworks(petrobras);
            petrobrasNN.toTrain();
        } catch (IOException ex) {
            Logger.getLogger(TrainingNetworks.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
