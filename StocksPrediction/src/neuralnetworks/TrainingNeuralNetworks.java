/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetworks;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import model.Company;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

/**
 *
 * @author Lucas
 */
public class TrainingNeuralNetworks {

    private final MultiLayerPerceptron neuralNetwork;
    private final int inputLength = 12;
    private final int interlayerLength = 25;
    private final int outputLength = 7;
    private final int maxIterationsForLearning = 1000;
    private final double maxErrorForLearning = 0.0001;
    private final double learningRateForLearning = 0.8;
    private final String fileNameNeuralNetwork;
    private static Calendar dataInicialTreinamento;
    private static Calendar dataFinalTreinamento;
    private static List<Company> empresas;

    public TrainingNeuralNetworks() throws IOException {

        empresas = new Vector<>();
        
        fileNameNeuralNetwork = "tenhoquemudaressenome";

        dataInicialTreinamento = Calendar.getInstance();
        dataInicialTreinamento.set(16, 2, 6);

        dataFinalTreinamento = Calendar.getInstance();
        dataFinalTreinamento.set(16, 8, 20);

        neuralNetwork = new MultiLayerPerceptron(inputLength, interlayerLength, outputLength);

        this.carregaDadosHistoricosParaTreinamento();

    }

    private void carregaDadosHistoricosParaTreinamento() throws IOException {

    }

    public void toTrain(List<DataSetRow> dataSet) {
        neuralNetwork.randomizeWeights();
        BackPropagation learningRules = new BackPropagation();
        learningRules.setMaxIterations(maxIterationsForLearning);
        learningRules.setMaxError(maxErrorForLearning);
        learningRules.setLearningRate(learningRateForLearning);
        
        DataSet trainingSet;
        trainingSet = new DataSet(inputLength, outputLength);
        for (DataSetRow row : dataSet) {
            trainingSet.addRow(row);
        }

        neuralNetwork.learn(trainingSet, learningRules);
        neuralNetwork.save(fileNameNeuralNetwork);

    }
}
    
/*
public final class RedeNeural {

    
    final String caminhoDaRede = "company.rede";    

    public RedeNeural() {
        neuralNetwork = new MultiLayerPerceptron(12, 25, 7);
    }
    
    public void training(List<DataSetRow> l) {
        neuralNetwork.randomizeWeights();
        BackPropagation lr = new BackPropagation();
        lr.setMaxIterations(1000);
        lr.setMaxError(0.0001);
        lr.setLearningRate(0.8);
        DataSet trainingSet;
        trainingSet = new DataSet(12, 7);        
        for (DataSetRow l1 : l) {
            trainingSet.addRow(l1);            
        }

        neuralNetwork.learn(trainingSet, lr);
        neuralNetwork.save(caminhoDaRede);
        System.out.print("Salvou");
    }

    @SuppressWarnings("rawtypes")
	public double[] consult(double[] input) {  
    	
    	System.out.print("En: ");
    	for (double d : input) {
			System.out.print(d + ", ");
		}
    	System.out.println();
    	
        @SuppressWarnings({ "rawtypes" })
		NeuralNetwork neuralNetworkLoad;
        neuralNetworkLoad = NeuralNetwork.createFromFile(caminhoDaRede);
        neuralNetworkLoad.setInput(input);
        neuralNetworkLoad.calculate();

        double[] saida = neuralNetworkLoad.getOutput();
        System.out.print("S: ");
        for (double d : saida) {
			System.out.print(d + ", ");
		}
        System.out.println();
        return saida;
    }
   

}

}
*/
