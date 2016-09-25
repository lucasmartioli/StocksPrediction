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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import model.Company;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.random.WeightsRandomizer;
import technicalindicators.TechnicalIndicators;

/**
 *
 * @author Lucas
 */
public class NeuralNetworks {

    private final MultiLayerPerceptron neuralNetwork;
    private final int inputLength = 14;
    private final int interlayerLength = 29;
    private final int outputLength = 1;
    private final int maxIterationsForLearning = 4000;
    private final double maxErrorForLearning = 0.0001;
    private final double learningRateForLearning = 0.5;
    private double[] minValueInSet;
    private double[] maxValueInSet;
    private final int daysForPredict = 5;
    private final String fileNameNeuralNetwork;
    private final Company company;
    private double denorma;

    public NeuralNetworks(Company company) {
        this.company = company;
        fileNameNeuralNetwork = company.getSimbolo() + ".neuralnet";
        neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputLength, interlayerLength,interlayerLength,  outputLength);

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

        System.out.println(closePriceIndicator.getTimeSeries().getTick(index).getDateName() + "  "
                + closePriceIndicator.getValue(index).toDouble() + ", "
                + sma4days.getValue(index).toDouble() + ", "
                + sma9days.getValue(index).toDouble() + ", "
                + sma18days.getValue(index).toDouble() + ", "
                + macd.getValue(index).toDouble() + ", "
                + rsi14days.getValue(index).toDouble() + ", "
                + obv.getValue(index).toDouble());
        double[] input
                //
                //                
                //                normalizeValue(closePriceIndicator.getValue(index - 2).toDouble(), IndexMinAndMaxInSet.PRICE),
                //                    normalizeValue(closePriceIndicator.getValue(index - 1).toDouble(), IndexMinAndMaxInSet.PRICE),
                //                    normalizeValue(closePriceIndicator.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
                = {normalizeValue(sma4days.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma4days.getValue(index - 1).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma4days.getValue(index - 2).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma9days.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma9days.getValue(index - 1).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma9days.getValue(index - 2).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma18days.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma18days.getValue(index - 1).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizeValue(sma18days.getValue(index - 2).toDouble(), IndexMinAndMaxInSet.PRICE),
                    normalizePercentage(rsi14days.getValue(index).toDouble()),                    
                    normalizeValue(macd.getValue(index).toDouble(), IndexMinAndMaxInSet.MACD),
                    normalizeValue(macd.getValue(index - 1).toDouble(), IndexMinAndMaxInSet.MACD),
                    normalizeValue(macd.getValue(index - 2).toDouble(), IndexMinAndMaxInSet.MACD),
                    normalizeValue(obv.getValue(index).toDouble(), IndexMinAndMaxInSet.VOLUME)};
        
        return input;

    }

    private double[] getExpectedOutpuToNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        System.out.println(closePriceIndicator.getTimeSeries().getTick(index + daysForPredict).getDateName() + " " + daysForPredict + " DIAS " + closePriceIndicator.getValue(index + daysForPredict).toDouble());
        double[] output
                = {normalizeValue(closePriceIndicator.getValue(index + daysForPredict).toDouble(), IndexMinAndMaxInSet.PRICE)};
        return output;
    }

    private List<DataSetRow> generateDataSetToTraining() {
        List<DataSetRow> dataSet = new ArrayList<>();

        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();
        TimeSeries timeSeries = technicalIndicators.getTimeSeries();

        for (int i = TechnicalIndicators.getMaxDaysIndicators(); i < (timeSeries.getTickCount() - 1) - daysForPredict; i++) {
            DataSetRow row = new DataSetRow(getInputToNeuralNetwork(technicalIndicators, i), getExpectedOutpuToNeuralNetwork(technicalIndicators, i));
            dataSet.add(row);
        }

        return dataSet;
    }

    double normalizeValue(double input, IndexMinAndMaxInSet index) {
        System.out.println("VALOR MÁXIMO " + maxValueInSet[index.getIndex()]);

        return input / maxValueInSet[index.getIndex()];
//        return value/*maxValueInSet[index.getIndex()]*/;
        //return (inputRounded - minValueInSet[index.getIndex()]) / (maxValueInSet[index.getIndex()] - minValueInSet[index.getIndex()]) * 0.8d + 0.1d;
    }

    double deNormalizeValue(double input, IndexMinAndMaxInSet index) {
//        System.out.println("dsdwsd" + maxValueInSet[index.getIndex()]);
        return input * denorma/*maxValueInSet[index.getIndex()]*/;
    }

    public void toTrain() {
        List<DataSetRow> dataSet = generateDataSetToTraining();
        WeightsRandomizer randomizer = new WeightsRandomizer();
        Random random = new Random(Calendar.getInstance().getTimeInMillis());
        randomizer.setRandomGenerator(random);
        neuralNetwork.randomizeWeights(randomizer);
//        neuralNetwork.randomizeWeights();
        BackPropagation learningRules = new BackPropagation();
        learningRules.setMaxIterations(maxIterationsForLearning);
        learningRules.setMaxError(maxErrorForLearning);
        learningRules.setLearningRate(learningRateForLearning);
        //neuralNetwork.connectInputsToOutputs();
        //learningRules.setBatchMode(true);

        DataSet trainingSet;
        trainingSet = new DataSet(inputLength, outputLength);
        for (DataSetRow row : dataSet) {
            trainingSet.addRow(row);
        }

        neuralNetwork.learn(trainingSet, learningRules);
        neuralNetwork.save(fileNameNeuralNetwork);

    }

    public double toPredict(double v) {
        denorma = v;

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
        maxValueInSet = new double[3];
        minValueInSet = new double[3];
        minValueInSet[IndexMinAndMaxInSet.PRICE.getIndex()] = Double.MAX_VALUE;
        minValueInSet[IndexMinAndMaxInSet.VOLUME.getIndex()] = Double.MAX_VALUE;
        minValueInSet[IndexMinAndMaxInSet.MACD.getIndex()] = Double.MAX_VALUE;
        maxValueInSet[IndexMinAndMaxInSet.PRICE.getIndex()] = 0d;
        maxValueInSet[IndexMinAndMaxInSet.VOLUME.getIndex()] = 0d;
        maxValueInSet[IndexMinAndMaxInSet.MACD.getIndex()] = 0d;

        //System.out.println("COMEÇOU A ROTINA QUE PODE ESTAR ERRADA!!!");
        for (int i = 0; i < timeSeries.getTickCount() - 1; i++) {

            ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
            OnBalanceVolumeIndicator obv = technicalIndicators.getObv();
            MACDIndicator macd = technicalIndicators.getMacd();

            double priceValue = closePriceIndicator.getValue(i).toDouble();
            double volumeValue = obv.getValue(i).toDouble();
            double macdValue = macd.getValue(i).toDouble();

            //System.out.println("DATA " + closePriceIndicator.getTimeSeries().getTick(i).getDateName() + " PRICE: " + priceValue + " VOLUME: " + volumeValue + " MACD: " + macdValue);
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

            if (macdValue < minValueInSet[IndexMinAndMaxInSet.MACD.getIndex()]) {
                minValueInSet[IndexMinAndMaxInSet.MACD.getIndex()] = macdValue;
            }

            if (macdValue > maxValueInSet[IndexMinAndMaxInSet.MACD.getIndex()]) {
                maxValueInSet[IndexMinAndMaxInSet.MACD.getIndex()] = macdValue;
            }

        }
        /*

        System.out.println("MINIMO FINAL: ");
        for (double d : minValueInSet) {
            System.out.print(d + " , ");

        }
        System.out.println("");

        System.out.println("MAXIMO FINAL: ");
        for (double d : maxValueInSet) {
            System.out.print(d + " , ");

        }
        System.out.println("");
         */
    }

    private double normalizePercentage(double input) {
        return input / 100d;
    }

    public enum IndexMinAndMaxInSet {

        PRICE(0), VOLUME(1), MACD(2);

        public int index;

        IndexMinAndMaxInSet(int index) {
            this.index = index;
        }

        public int getIndex() {
            return this.index;
        }
    }
}
