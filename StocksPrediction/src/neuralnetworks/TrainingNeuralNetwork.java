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

        System.out.println("Treinando esse: ");
        PredictionNeuralNetwork rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI", 3, 1, TransferFunctionType.SGN);
        rsiNeuralNetwork.toTrain(getRSIDataSetTraining(timeSeries, technicalIndicators));
        
        rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI");
        
        System.out.println("Treinando o outro: ");
        PredictionNeuralNetwork prediction = new PredictionNeuralNetwork(company.getSimbolo(), "PREDICTION", 4, 1, TransferFunctionType.SGN);
        prediction.toTrain(getPREDICTIONDataSetTraining(timeSeries, technicalIndicators, rsiNeuralNetwork));
    }

    public static double toPredict(Company company) {
        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();
        TimeSeries timeSeries = technicalIndicators.getTimeSeries();

        PredictionNeuralNetwork rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI");
        PredictionNeuralNetwork prediction = new PredictionNeuralNetwork(company.getSimbolo(), "PREDICTION");

        double[] result = prediction.toPredict(getInputToPREDICTIONNeuralNetwork(rsiNeuralNetwork, technicalIndicators, timeSeries.getTickCount() - 1));

        return result[0];
    }

    private static List<DataSetRow> getPREDICTIONDataSetTraining(TimeSeries timeSeries, TechnicalIndicators technicalIndicators, PredictionNeuralNetwork rsiNeuralNetwork) {

        List<DataSetRow> predictionDataSet = new ArrayList<>();
        for (int i = TechnicalIndicators.getMaxDaysIndicators(); i < (timeSeries.getTickCount() - 1) - tradesForTraining; i++) {
            DataSetRow row = new DataSetRow(TrainingNeuralNetwork.getInputToPREDICTIONNeuralNetwork(rsiNeuralNetwork, technicalIndicators, i), TrainingNeuralNetwork.getExpectedOutpuToPREDICTIONNeuralNetwork(technicalIndicators, i));
            predictionDataSet.add(row);
        }
        
        for (DataSetRow dataSetRow : predictionDataSet) {
            System.out.println(dataSetRow.toString());
            
        }

        return predictionDataSet;

    }

    private static double[] getInputToPREDICTIONNeuralNetwork(PredictionNeuralNetwork rsiNeuralNetwork, TechnicalIndicators technicalIndicators, int index) {
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        double[] rsiResult = rsiNeuralNetwork.toPredict(getInputToRSINeuralNetwork(technicalIndicators, index));
        double[] input = {
            normalizePriceInput(closePriceIndicator.getValue(index).toDouble()),
            normalizePriceInput(closePriceIndicator.getValue(index - 1).toDouble()),
            normalizePriceInput(closePriceIndicator.getValue(index - 2).toDouble()),
            rsiResult[0]
        };

        return input;
    }

    private static double[] getExpectedOutpuToPREDICTIONNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {

        int result;
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        double diference = closePriceIndicator.getValue(index + tradesForTraining).toDouble() - closePriceIndicator.getValue(index).toDouble();

        result = 0;
        if (diference > 0) {
            result = 1;
        } else if (diference < 0) {
            result = -1;
        }

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

        int result;
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        double diference = closePriceIndicator.getValue(index + tradesForTraining).toDouble() - closePriceIndicator.getValue(index).toDouble();

        result = 0;
        if (diference > 0) {
            result = 1;
        } else if (diference < 0) {
            result = -1;
        }

        double[] output = {result};

        return output;
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
