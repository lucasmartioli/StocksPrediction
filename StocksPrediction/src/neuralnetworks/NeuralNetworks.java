/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetworks;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.volume.OnBalanceVolumeIndicator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import model.Company;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import technicalindicators.TechnicalIndicators;

/**
 *
 * @author Lucas
 */
public class NeuralNetworks {

    private final MultiLayerPerceptron neuralNetwork;
    private final int inputLength = 12;
    private final int interlayerLength = 25;
    private final int outputLength = 7;
    private final int maxIterationsForLearning = 1000;
    private final double maxErrorForLearning = 0.00001;
    private final double learningRateForLearning = 0.8;
    private final double minValueInSet = 0d;
    private final double maxValueInSet = 1000d;
    private final int daysForPredict = 7;
    private final String fileNameNeuralNetwork;
    private final Company company;

    public NeuralNetworks(Company company) {
        this.company = company;
        fileNameNeuralNetwork = company.getSimbolo() + ".neuralnet";
        neuralNetwork = new MultiLayerPerceptron(inputLength, interlayerLength, outputLength);
    }

    private List<DataSetRow> generateDataSetToTraining() {
        List<DataSetRow> dataSet = new ArrayList<>();

        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();
        TimeSeries timeSeries = technicalIndicators.getTimeSeries();

        for (int i = TechnicalIndicators.getMaxDaysIndicators(); i < timeSeries.getTickCount() - daysForPredict; i++) {

            ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
            MACDIndicator macd = technicalIndicators.getMacd();
            RSIIndicator rsi14days = technicalIndicators.getRsi14days();
            SMAIndicator sma4days = technicalIndicators.getSma4days();
            SMAIndicator sma9days = technicalIndicators.getSma9days();
            SMAIndicator sma18days = technicalIndicators.getSma18days();
            OnBalanceVolumeIndicator obv = technicalIndicators.getObv();
            double[] input
                    = { normalizeValue(closePriceIndicator.getValue(i).toDouble()),
                        normalizeValue(macd.getValue(i).toDouble()),
                        normalizeValue(rsi14days.getValue(i).toDouble()),
                        normalizeValue(sma4days.getValue(i).toDouble()),
                        normalizeValue(sma9days.getValue(i).toDouble()),
                        normalizeValue(sma18days.getValue(i).toDouble()),
                        normalizeValue(obv.getValue(i).toDouble())};
            double[] output = {normalizeValue(closePriceIndicator.getValue(i + daysForPredict).toDouble())};
            DataSetRow row = new DataSetRow(input, output);
            dataSet.add(row);
        }

        return dataSet;
    }

    double normalizeValue(double input) {
        return (input - minValueInSet) / (maxValueInSet - minValueInSet) * 0.8d + 0.1d;
    }

    double deNormalizeValue(double input) {
        return minValueInSet + (input - 0.1d) * (maxValueInSet - minValueInSet) / 0.8d;
    }

    public void toTrain() {
        List<DataSetRow> dataSet = generateDataSetToTraining();
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
