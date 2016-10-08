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
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.TransferFunctionType;
import technicalindicators.TechnicalIndicators;

/**
 *
 * @author Lucas
 */
public class TrainingNeuralNetwork {

    private static final int tradesForTraining = 5;

    public static void toTrain(Company company) {
        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();
        TimeSeries timeSeries = technicalIndicators.getTimeSeries();

        PredictionNeuralNetwork rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI", 3, 1, TransferFunctionType.SIGMOID);
        rsiNeuralNetwork.setLearningRateForLearning(0.8d);
        rsiNeuralNetwork.setMaxErrorForLearning(0.00001d);
        rsiNeuralNetwork.setMaxIterationsForLearning(10000);
        rsiNeuralNetwork.toTrain(getRSIDataSetTraining(timeSeries, technicalIndicators));

        PredictionNeuralNetwork smaNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "SMA", 3, 1, TransferFunctionType.SIGMOID);
        smaNeuralNetwork.setLearningRateForLearning(0.8d);
        smaNeuralNetwork.setMaxErrorForLearning(0.00001d);
        smaNeuralNetwork.setMaxIterationsForLearning(10000);
        smaNeuralNetwork.toTrain(getSMADataSetTraining(timeSeries, technicalIndicators));

        rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI");
        smaNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "SMA");

        PredictionNeuralNetwork prediction = new PredictionNeuralNetwork(company.getSimbolo(), "PREDICTION", 4, 1, TransferFunctionType.SIGMOID);
        prediction.setLearningRateForLearning(0.5d);
        prediction.setMaxErrorForLearning(0.0001d);
        prediction.setMaxIterationsForLearning(10000);
        prediction.toTrain(getPREDICTIONDataSetTraining(timeSeries, technicalIndicators, rsiNeuralNetwork, smaNeuralNetwork));
    }

    public static double toPredict(Company company) {
        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();
        TimeSeries timeSeries = technicalIndicators.getTimeSeries();

        PredictionNeuralNetwork rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI");
        PredictionNeuralNetwork smaNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "SMA");
        PredictionNeuralNetwork prediction = new PredictionNeuralNetwork(company.getSimbolo(), "PREDICTION");

        double[] result = prediction.toPredict(getInputToPREDICTIONNeuralNetwork(rsiNeuralNetwork, smaNeuralNetwork, technicalIndicators, timeSeries.getTickCount() - 1));

        return result[0];
    }

    private static List<DataSetRow> getPREDICTIONDataSetTraining(TimeSeries timeSeries, TechnicalIndicators technicalIndicators, PredictionNeuralNetwork rsiNeuralNetwork, PredictionNeuralNetwork smaNeuralNetwork) {

        List<DataSetRow> predictionDataSet = new ArrayList<>();
        for (int i = TechnicalIndicators.getMaxDaysIndicators(); i < (timeSeries.getTickCount() - 1) - tradesForTraining; i++) {
            DataSetRow row = new DataSetRow(TrainingNeuralNetwork.getInputToPREDICTIONNeuralNetwork(rsiNeuralNetwork, smaNeuralNetwork, technicalIndicators, i), TrainingNeuralNetwork.getExpectedOutpuToPREDICTIONNeuralNetwork(technicalIndicators, i));
            predictionDataSet.add(row);
        }

        return predictionDataSet;
    }

    private static double[] getInputToPREDICTIONNeuralNetwork(PredictionNeuralNetwork rsiNeuralNetwork, PredictionNeuralNetwork smaNeuralNetwork, TechnicalIndicators technicalIndicators, int index) {
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        double[] rsiResult = rsiNeuralNetwork.toPredict(getInputToRSINeuralNetwork(technicalIndicators, index));
        double[] smaResult = smaNeuralNetwork.toPredict(getInputToSMANeuralNetwork(technicalIndicators, index));

        double trendIndicator = 0.5d;
        System.out.println("Sequencia de precos: " + closePriceIndicator.getValue(index - 2).toDouble() + ", " + closePriceIndicator.getValue(index - 1).toDouble() + ", " + closePriceIndicator.getValue(index).toDouble());
        if (closePriceIndicator.getValue(index).toDouble() > closePriceIndicator.getValue(index - 1).toDouble() && closePriceIndicator.getValue(index - 1).toDouble() > closePriceIndicator.getValue(index - 2).toDouble()) {
            trendIndicator = 1d;
        } else if (closePriceIndicator.getValue(index).toDouble() < closePriceIndicator.getValue(index - 1).toDouble() && closePriceIndicator.getValue(index - 1).toDouble() < closePriceIndicator.getValue(index - 2).toDouble()) {
            trendIndicator = 0;
        }

        System.out.println("Preco Atual: " + closePriceIndicator.getValue(index).toDouble());
//        System.out.println("Preco Futuro: " + closePriceIndicator.getValue(index + tradesForTraining).toDouble());

        double[] input = {
            normalizePriceInput(closePriceIndicator.getValue(index).toDouble()),
            trendIndicator,
            convertSignalInput(rsiResult[0]),
            convertSignalInput(smaResult[0])
        };

        return input;
    }

    private static double[] getExpectedOutpuToPREDICTIONNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {

        double result;
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        double diference = closePriceIndicator.getValue(index + tradesForTraining).toDouble() - closePriceIndicator.getValue(index).toDouble();

        result = 0.5d;
        if (diference > 0) {
            result = 1d;
        } else if (diference < 0) {
            result = 0d;
        }

//         normalizePriceInput(closePriceIndicator.getValue(index + tradesForTraining).toDouble()
        double[] output = {result};

        return output;
    }

    private static List<DataSetRow> getRSIDataSetTraining(TimeSeries timeSeries, TechnicalIndicators technicalIndicators) {

        List<DataSetRow> rsiDataSet = new ArrayList<>();
        for (int i = TechnicalIndicators.getMaxDaysIndicators(); i < (timeSeries.getTickCount() - 1) - tradesForTraining; i++) {
            DataSetRow row = new DataSetRow(TrainingNeuralNetwork.getInputToRSINeuralNetwork(technicalIndicators, i), TrainingNeuralNetwork.getExpectedOutpuToRSINeuralNetwork(technicalIndicators, i));
            rsiDataSet.add(row);
        }

        return rsiDataSet;

    }

    private static double[] getInputToRSINeuralNetwork(TechnicalIndicators technicalIndicators, int index) {
        RSIIndicator rsi14days = technicalIndicators.getRsi14days();

        double[] input = {
            normalizeRSIInput(rsi14days.getValue(index).toDouble()),
            normalizeRSIInput(rsi14days.getValue(index - 1).toDouble()),
            normalizeRSIInput(rsi14days.getValue(index - 2).toDouble())
        };

        return input;
    }

    private static double[] getExpectedOutpuToRSINeuralNetwork(TechnicalIndicators technicalIndicators, int index) {

        double result;
        RSIIndicator rsi14days = technicalIndicators.getRsi14days();

        result = 0.5d;
        if (rsi14days.getValue(index).toDouble() <= 30 || rsi14days.getValue(index - 1).toDouble() <= 30 || rsi14days.getValue(index - 2).toDouble() <= 30) {
            result = 1d;
        } else if (rsi14days.getValue(index).toDouble() >= 70 || rsi14days.getValue(index - 1).toDouble() >= 70 || rsi14days.getValue(index - 2).toDouble() >= 70) {
            result = 0d;
        }

        double[] output = {result};

        return output;
    }

    private static List<DataSetRow> getSMADataSetTraining(TimeSeries timeSeries, TechnicalIndicators technicalIndicators) {

        List<DataSetRow> smaDataSet = new ArrayList<>();
        for (int i = TechnicalIndicators.getMaxDaysIndicators(); i < (timeSeries.getTickCount() - 1) - tradesForTraining; i++) {
            DataSetRow row = new DataSetRow(TrainingNeuralNetwork.getInputToSMANeuralNetwork(technicalIndicators, i), TrainingNeuralNetwork.getExpectedOutpuToSMANeuralNetwork(technicalIndicators, i));
            smaDataSet.add(row);
        }

        return smaDataSet;

    }

    private static double[] getInputToSMANeuralNetwork(TechnicalIndicators technicalIndicators, int index) {

        SMAIndicator sma4days = technicalIndicators.getSma4days();
        SMAIndicator sma9days = technicalIndicators.getSma9days();
        SMAIndicator sma18days = technicalIndicators.getSma18days();

        double[] input = {
            normalizePriceInput(sma4days.getValue(index).toDouble()),
            normalizePriceInput(sma9days.getValue(index).toDouble()),
            normalizePriceInput(sma18days.getValue(index).toDouble())//,
//            normalizePriceInput(sma4days.getValue(index - 1).toDouble()),
//            normalizePriceInput(sma9days.getValue(index - 1).toDouble()),
//            normalizePriceInput(sma18days.getValue(index - 1).toDouble()),
//            normalizePriceInput(sma4days.getValue(index - 2).toDouble()),
//            normalizePriceInput(sma9days.getValue(index - 2).toDouble()),
//            normalizePriceInput(sma18days.getValue(index - 2).toDouble())
        };

        return input;
    }

    private static double[] getExpectedOutpuToSMANeuralNetwork(TechnicalIndicators technicalIndicators, int index) {

        SMAIndicator sma4days = technicalIndicators.getSma4days();
        SMAIndicator sma9days = technicalIndicators.getSma9days();
        SMAIndicator sma18days = technicalIndicators.getSma18days();

//        double[] signals = {
//            getSignalFromSMA(sma4days.getValue(index).toDouble(), sma9days.getValue(index).toDouble(), sma18days.getValue(index).toDouble()),
//            getSignalFromSMA(sma4days.getValue(index - 1).toDouble(), sma9days.getValue(index - 1).toDouble(), sma18days.getValue(index - 1).toDouble()),
//            getSignalFromSMA(sma4days.getValue(index - 2).toDouble(), sma9days.getValue(index - 2).toDouble(), sma18days.getValue(index - 2).toDouble())
//        };       
//        double[] output = {getMostFrequentValue(signals)};
        double[] output = {getSignalFromSMA(sma4days.getValue(index).toDouble(), sma9days.getValue(index).toDouble(), sma18days.getValue(index).toDouble())};

        return output;
    }

    private static double convertSignalInput(double signal) {

        double convertedSignal = 0;

        if (signal > 0.33d && signal < 0.5d) {
            convertedSignal = 0.5d;
        } else if (signal > 0.66d) {
            convertedSignal = 1d;
        }

        System.out.println(Double.compare(signal, 0.66));

        System.out.println("Sinal para converter: " + signal + " convertido para: " + convertedSignal);

        return convertedSignal;

    }

    private static double getSignalFromSMA(double fastLine, double mediumLine, double lazyLine) {
        double signal;

        signal = 0.5d;

        double diferenceFastMediumLine = fastLine - mediumLine;
        double diferenceFastLazyLine = fastLine - lazyLine;

        //double diferenceLazyMediumLine = lazyLine - mediumLine;
        //double diferenceLazyFastLine = lazyLine - fastLine;
        if ((diferenceFastMediumLine < 0 && diferenceFastLazyLine > 0) || (diferenceFastMediumLine < 0 && diferenceFastLazyLine < 0)) {
            signal = 0d;
        } else if ((diferenceFastMediumLine < 0 && diferenceFastLazyLine > 0) || (diferenceFastMediumLine > 0 && diferenceFastLazyLine > 0)) {
            signal = 1d;
        }

        return signal;
    }

    static int countOccurrences(double[] list, double targetValue) {
        int count = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i] == targetValue) {
                count++;
            }
        }
        return count;
    }

    static double getMostFrequentValue(double[] list) {
        int mostFrequentCount = 0;
        double mostFrequentValue = 0;
        for (int i = 0; i < list.length; i++) {
            double value = list[i];
            int count = countOccurrences(list, value);
            if (count > mostFrequentCount) {
                mostFrequentCount = count;
                mostFrequentValue = value;
            }
        }
        return mostFrequentValue;
    }

    private static double normalizePriceInput(double priceValue) {
        return priceValue / 100;
    }

    private static double normalizeRSIInput(double rsiValue) {
        return rsiValue / 100;
    }

//    getInputToNeuralNetwork(technicalIndicators, timeSeries.getTickCount() - 1)
//    
//    private double[] getExpectedOutpuToNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {
//        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
//        System.out.println(closePriceIndicator.getTimeSeries().getTick(index + tradesForPredict).getDateName() + " " + tradesForPredict + " DIAS " + closePriceIndicator.getValue(index + tradesForPredict).toDouble());
//        double[] output
//                = {normalizeValue(closePriceIndicator.getValue(index + tradesForPredict).toDouble(), IndexMinAndMaxInSet.PRICE)};
//        return output;
//    }
//
//    private List<DataSetRow> generateDataSetToTraining() {
//        List<DataSetRow> dataSet = new ArrayList<>();
//
//        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();
//        TimeSeries timeSeries = technicalIndicators.getTimeSeries();
//
//        for (int i = TechnicalIndicators.getMaxDaysIndicators(); i < (timeSeries.getTickCount() - 1) - tradesForPredict; i++) {
//            DataSetRow row = new DataSetRow(getInputToNeuralNetwork(technicalIndicators, i), getExpectedOutpuToNeuralNetwork(technicalIndicators, i));
//            dataSet.add(row);
//        }
//
//        return dataSet;
//    }
//
//    private double[] getInputToNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {
//
//        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
//        MACDIndicator macd = technicalIndicators.getMacd();
//        RSIIndicator rsi14days = technicalIndicators.getRsi14days();
//        SMAIndicator sma4days = technicalIndicators.getSma4days();
//        SMAIndicator sma9days = technicalIndicators.getSma9days();
//        SMAIndicator sma18days = technicalIndicators.getSma18days();
//        OnBalanceVolumeIndicator obv = technicalIndicators.getObv();
//
//        System.out.println(closePriceIndicator.getTimeSeries().getTick(index).getDateName() + "  "
//                + closePriceIndicator.getValue(index).toDouble() + ", "
//                + sma4days.getValue(index).toDouble() + ", "
//                + sma9days.getValue(index).toDouble() + ", "
//                + sma18days.getValue(index).toDouble() + ", "
//                + macd.getValue(index).toDouble() + ", "
//                + rsi14days.getValue(index).toDouble() + ", "
//                + obv.getValue(index).toDouble());
//
//        double[] input
//                //
//                //                
//                //                normalizeValue(closePriceIndicator.getValue(index - 2).toDouble(), IndexMinAndMaxInSet.PRICE),
//                //                    normalizeValue(closePriceIndicator.getValue(index - 1).toDouble(), IndexMinAndMaxInSet.PRICE),
//                //                    normalizeValue(closePriceIndicator.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
//                = {normalizeValue(closePriceIndicator.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    normalizeValue(closePriceIndicator.getValue(index - tradesForPredict).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    //normalizeValue(closePriceIndicator.getValue(index - 2 * tradesForPredict ).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    normalizeValue(sma4days.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    normalizeValue(sma4days.getValue(index - tradesForPredict).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    //normalizeValue(sma4days.getValue(index - 2 * tradesForPredict).toDouble(), IndexMinAndMaxInSet.PRICE),                    
//                    normalizeValue(sma9days.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    normalizeValue(sma9days.getValue(index - tradesForPredict).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    //normalizeValue(sma9days.getValue(index - 2 * tradesForPredict).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    normalizeValue(sma18days.getValue(index).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    normalizeValue(sma18days.getValue(index - tradesForPredict).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    //normalizeValue(sma18days.getValue(index - 2 * tradesForPredict).toDouble(), IndexMinAndMaxInSet.PRICE),
//                    normalizePercentage(rsi14days.getValue(index).toDouble()),
//                    normalizePercentage(rsi14days.getValue(index - tradesForPredict).toDouble()),
//                    normalizeValue(macd.getValue(index).toDouble(), IndexMinAndMaxInSet.MACD),
//                    normalizeValue(macd.getValue(index - tradesForPredict).toDouble(), IndexMinAndMaxInSet.MACD),
//                    normalizeValue(obv.getValue(index).toDouble(), IndexMinAndMaxInSet.VOLUME),
//                    normalizeValue(obv.getValue(index - tradesForPredict).toDouble(), IndexMinAndMaxInSet.VOLUME)};
//        //normalizePercentage(rsi14days.getValue(index - 2 * tradesForPredict).toDouble())};
////                    
////                    
////                    normalizeValue(macd.getValue(index - 2).toDouble(), IndexMinAndMaxInSet.MACD),
////                    normalizeValue(macd.getValue(index - 5).toDouble(), IndexMinAndMaxInSet.MACD),
////                    };
//
//        return input;
//
//    }
}
