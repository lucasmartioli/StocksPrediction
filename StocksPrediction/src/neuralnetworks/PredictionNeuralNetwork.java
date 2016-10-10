/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetworks;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.NeuralNetworkType;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.WeightsRandomizer;

/**
 *
 * @author Lucas
 */
public class PredictionNeuralNetwork {

    private MultiLayerPerceptron neuralNetwork;
    private int inputLength;
    private int outputLength;
    private int maxIterationsForLearning = 1000;
    private double maxErrorForLearning = 0.00001;
    private double learningRateForLearning = 0.5;
    private final String fileNameNeuralNetwork;

    public PredictionNeuralNetwork(String companySymbol, String neuralNetworkName, int inputLength, int outputLength, TransferFunctionType transferFunctionType) {
        this.inputLength = inputLength;
        this.outputLength = outputLength;
        fileNameNeuralNetwork = companySymbol + neuralNetworkName + ".neuralnet";
        neuralNetwork = new MultiLayerPerceptron(transferFunctionType, inputLength, 2 * inputLength + 1, outputLength);
        neuralNetwork.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);

    }
    
    public PredictionNeuralNetwork(String companySymbol, String neuralNetworkName, int inputLength, int outputLength) {
        this.inputLength = inputLength;
        this.outputLength = outputLength;
        fileNameNeuralNetwork = companySymbol + neuralNetworkName + ".neuralnet";
        neuralNetwork = new MultiLayerPerceptron(inputLength, 2 * inputLength + 1, outputLength);
        neuralNetwork.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);

    }

    public PredictionNeuralNetwork(String companySymbol, String neuralNetworkName) {
        fileNameNeuralNetwork = companySymbol + neuralNetworkName + ".neuralnet";
    }

    public int getInputLength() {
        return inputLength;
    }

    public void setInputLength(int inputLength) {
        this.inputLength = inputLength;
    }

    public int getOutputLength() {
        return outputLength;
    }

    public void setOutputLength(int outputLength) {
        this.outputLength = outputLength;
    }

    public int getMaxIterationsForLearning() {
        return maxIterationsForLearning;
    }

    public void setMaxIterationsForLearning(int maxIterationsForLearning) {
        this.maxIterationsForLearning = maxIterationsForLearning;
    }

    public double getMaxErrorForLearning() {
        return maxErrorForLearning;
    }

    public void setMaxErrorForLearning(double maxErrorForLearning) {
        this.maxErrorForLearning = maxErrorForLearning;
    }

    public double getLearningRateForLearning() {
        return learningRateForLearning;
    }

    public void setLearningRateForLearning(double learningRateForLearning) {
        this.learningRateForLearning = learningRateForLearning;
    }

    public void toTrain(List<DataSetRow> dataSet) {
        System.out.println("Iniciando treinamento da rede " + fileNameNeuralNetwork);
//        WeightsRandomizer randomizer = new WeightsRandomizer();
//        Random random = new Random(Calendar.getInstance().getTimeInMillis());
//        randomizer.setRandomGenerator(random);
//        neuralNetwork.randomizeWeights(randomizer);
        neuralNetwork.randomizeWeights();
        BackPropagation learningRules = new BackPropagation();        
        learningRules.setMaxIterations(maxIterationsForLearning);
        learningRules.setMaxError(maxErrorForLearning);
        learningRules.setLearningRate(learningRateForLearning);
        
        //learningRules.setBatchMode(true);

        DataSet trainingSet;
        trainingSet = new DataSet(inputLength, outputLength);
        for (DataSetRow row : dataSet) {
            System.out.println(row.toString());
            trainingSet.addRow(row);
        }

        neuralNetwork.learn(trainingSet, learningRules);        
        neuralNetwork.save(fileNameNeuralNetwork);
        System.out.println("Rede " + fileNameNeuralNetwork + " salva em arquivo.");
    }

    public double[] toPredict(double[] input) {
        System.out.println("Iniciando uso da rede " + fileNameNeuralNetwork + "." );
        NeuralNetwork neuralNetworkLoad;
        neuralNetworkLoad = NeuralNetwork.createFromFile(fileNameNeuralNetwork);
        neuralNetworkLoad.setInput(input);
        neuralNetworkLoad.calculate();

        
        System.out.print("entrada: ");

        for (double d : input) {
            System.out.print(d + ", ");

        }
        
        System.out.println("");
        System.out.print("saida: ");

        for (double d : neuralNetworkLoad.getOutput()) {
            System.out.print(d + ", ");

        }
        
        System.out.println("");
        System.out.println("Finalizado o uso da rede " + fileNameNeuralNetwork + "." );
        
        return neuralNetworkLoad.getOutput();
        
    }
}
