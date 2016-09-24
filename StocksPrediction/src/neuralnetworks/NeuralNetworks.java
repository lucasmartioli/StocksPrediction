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
import java.util.ArrayList;
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
    private double[] minValueInSet;
    private double[] maxValueInSet;
    private final int daysForPredict = 7;
    private final String fileNameNeuralNetwork;
    private final Company company;
    
    public NeuralNetworks(Company company) {
        this.company = company;
        fileNameNeuralNetwork = company.getSimbolo() + ".neuralnet";
        neuralNetwork = new MultiLayerPerceptron(inputLength, interlayerLength, outputLength);
        
        foundMinAndMaxInSet();
    }
    
    private double[] getInputToNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {
        
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        MACDIndicator macd = technicalIndicators.getMacd();
        RSIIndicator rsi14days = technicalIndicators.getRsi14days();
        SMAIndicator sma4days = technicalIndicators.getSma4days();
        SMAIndicator sma9days = technicalIndicators.getSma9days();
        SMAIndicator sma18days = technicalIndicators.getSma18days();
        OnBalanceVolumeIndicator obv = technicalIndicators.getObv();
        double[] input
                = {normalizeValue(closePriceIndicator.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(macd.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(rsi14days.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma4days.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma9days.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma18days.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(obv.getValue(index).toDouble(), IndexMinAndMaxInSet.VOLUME)};
        
        return input;
        
    }
    
    private double[] getExpectedOutpuToNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        double[] output = {normalizeValue(closePriceIndicator.getValue(index + daysForPredict).toDouble(), IndexMinAndMaxInSet.PRICE)};
        
        return output;
    }
    
    private List<DataSetRow> generateDataSetToTraining() {
        List<DataSetRow> dataSet = new ArrayList<>();
        
        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();
        TimeSeries timeSeries = technicalIndicators.getTimeSeries();
        
        for (int i = TechnicalIndicators.getMaxDaysIndicators(); i < timeSeries.getTickCount() - daysForPredict; i++) {
            DataSetRow row = new DataSetRow(getInputToNeuralNetwork(technicalIndicators, i), getExpectedOutpuToNeuralNetwork(technicalIndicators, i));
            dataSet.add(row);
        }
        
        return dataSet;
    }
    
    double normalizeValue(double input, IndexMinAndMaxInSet index) {
        return (input - minValueInSet[index.getIndex()]) / (maxValueInSet[index.getIndex()] - minValueInSet[index.getIndex()]) * 0.8d + 0.1d;
    }
    
    double deNormalizeValue(double input, IndexMinAndMaxInSet index) {
        return minValueInSet[index.getIndex()] + (input - 0.1d) * (maxValueInSet[index.getIndex()] - minValueInSet[index.getIndex()]) / 0.8d;
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
    
    public double toPredict() {
        
        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();
        TimeSeries timeSeries = technicalIndicators.getTimeSeries();
        
        NeuralNetwork neuralNetworkLoad;
        neuralNetworkLoad = NeuralNetwork.createFromFile(fileNameNeuralNetwork);
        neuralNetworkLoad.setInput(getInputToNeuralNetwork(technicalIndicators, timeSeries.getTickCount() - 1));
        neuralNetworkLoad.calculate();      
        
        double[] output = neuralNetworkLoad.getOutput();
        
        /* RETIRAR ESSA PARTE QUE PRINTA AQUI DEPOIS */
        System.out.print("Output: ");
        for (double d : output) {
            System.out.print(deNormalizeValue(d, IndexMinAndMaxInSet.PRICE) + ", ");
        }
        System.out.println();
        
        return deNormalizeValue(output[0], IndexMinAndMaxInSet.PRICE);
    }
    
    private void foundMinAndMaxInSet() {
        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();
        TimeSeries timeSeries = technicalIndicators.getTimeSeries();
        maxValueInSet = new double[2];
        minValueInSet = new double[2];
        minValueInSet[IndexMinAndMaxInSet.PRICE.getIndex()] = Double.MAX_VALUE;
        minValueInSet[IndexMinAndMaxInSet.VOLUME.getIndex()] = Double.MAX_VALUE;
        
        for (int i = 0; i < timeSeries.getTickCount() - 1; i++) {
            
            ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
            OnBalanceVolumeIndicator obv = technicalIndicators.getObv();
            
            double priceValue = closePriceIndicator.getValue(i).toDouble();
            double volumeValue = obv.getValue(i).toDouble();
            
            if (priceValue < minValueInSet[IndexMinAndMaxInSet.PRICE.getIndex()]) {
                minValueInSet[IndexMinAndMaxInSet.PRICE.getIndex()] = priceValue;
            }
            
            if (priceValue > maxValueInSet[IndexMinAndMaxInSet.PRICE.getIndex()]) {
                maxValueInSet[IndexMinAndMaxInSet.PRICE.getIndex()] = priceValue;
            }
            
            if (volumeValue < minValueInSet[IndexMinAndMaxInSet.VOLUME.getIndex()]) {
                minValueInSet[IndexMinAndMaxInSet.VOLUME.getIndex()] = volumeValue;
            }
            
            if (volumeValue > maxValueInSet[IndexMinAndMaxInSet.VOLUME.getIndex()]) {
                maxValueInSet[IndexMinAndMaxInSet.VOLUME.getIndex()] = volumeValue;
            }
            
        }
        
    }
    
    public enum IndexMinAndMaxInSet {
        
        PRICE(0), VOLUME(1);
        
        public int index;
        
        IndexMinAndMaxInSet(int index) {
            this.index = index;
        }
        
        public int getIndex() {
            return this.index;
        }
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
