/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetworks;

import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private static final double INDICATIVO_TENDENCIA_ALTA = 1d;
    private static final double INDICATIVO_TENDENCIA_BAIXA = 0d;
    private static final double INDICATIVO_TENDENCIA_LATERAL = 0.5d;

    public static void toTrain(Company company, int initialIndex, int finalIndex, double normalizerValue) {

        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();

        PredictionNeuralNetwork rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI", 3, 4, 1, TransferFunctionType.SIGMOID);
        rsiNeuralNetwork.setLearningRateForLearning(0.6d);
        rsiNeuralNetwork.setMaxErrorForLearning(0.0001d);
        rsiNeuralNetwork.setMaxIterationsForLearning(10000);
        rsiNeuralNetwork.toTrain(getRSIDataSetTraining(initialIndex, finalIndex, technicalIndicators));

        PredictionNeuralNetwork smaNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "SMA", 2, 3, 1, TransferFunctionType.SIGMOID);
        smaNeuralNetwork.setLearningRateForLearning(0.6d);
        smaNeuralNetwork.setMaxErrorForLearning(0.0001d);
        smaNeuralNetwork.setMaxIterationsForLearning(10000);
        smaNeuralNetwork.toTrain(getSMADataSetTraining(initialIndex, finalIndex, technicalIndicators));

        PredictionNeuralNetwork macdNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "MACD", 2, 3, 1, TransferFunctionType.SIGMOID);
        macdNeuralNetwork.setLearningRateForLearning(0.6d);
        macdNeuralNetwork.setMaxErrorForLearning(0.0001d);
        macdNeuralNetwork.setMaxIterationsForLearning(10000);
        macdNeuralNetwork.toTrain(getMACDDataSetTraining(initialIndex, finalIndex, technicalIndicators));

        rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI");
        smaNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "SMA");
        macdNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "MACD");

        PredictionNeuralNetwork prediction = new PredictionNeuralNetwork(company.getSimbolo(), "PREDICTION", 8, 17, 1, TransferFunctionType.SIGMOID);
        prediction.setLearningRateForLearning(0.6d);
        prediction.setMaxErrorForLearning(0.0001d);
        prediction.setMaxIterationsForLearning(10000);
        prediction.toTrain(getPREDICTIONDataSetTraining(initialIndex, finalIndex, technicalIndicators, rsiNeuralNetwork, smaNeuralNetwork, macdNeuralNetwork, normalizerValue));
    }

    public static double toPredict(Company company, int finalIndex, double normalizerValue) {
        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();

        PredictionNeuralNetwork macdNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "MACD");
        PredictionNeuralNetwork rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI");
        PredictionNeuralNetwork smaNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "SMA");        
        PredictionNeuralNetwork prediction = new PredictionNeuralNetwork(company.getSimbolo(), "PREDICTION");

        double[] result = prediction.toPredict(getInputToPREDICTIONNeuralNetwork(normalizerValue, macdNeuralNetwork, rsiNeuralNetwork, smaNeuralNetwork, technicalIndicators, finalIndex));

        return result[0] * normalizerValue;
    }

    private static List<DataSetRow> getPREDICTIONDataSetTraining(int initialIndex, int finalIndex, TechnicalIndicators technicalIndicators, PredictionNeuralNetwork rsiNeuralNetwork, PredictionNeuralNetwork smaNeuralNetwork, PredictionNeuralNetwork macdNeuralNetwork, double normalizerValue) {

        List<DataSetRow> predictionDataSet = new ArrayList<>();
        for (int i = initialIndex; i < finalIndex - tradesForTraining; i++) {
            DataSetRow row = new DataSetRow(TrainingNeuralNetwork.getInputToPREDICTIONNeuralNetwork(normalizerValue, macdNeuralNetwork, rsiNeuralNetwork, smaNeuralNetwork, technicalIndicators, i), TrainingNeuralNetwork.getExpectedOutpuToPREDICTIONNeuralNetwork(technicalIndicators, i, normalizerValue));
            predictionDataSet.add(row);
        }

        return predictionDataSet;
    }

    private static double[] getInputToPREDICTIONNeuralNetwork(double normalizerValue, PredictionNeuralNetwork macdNeuralNetwork, PredictionNeuralNetwork rsiNeuralNetwork, PredictionNeuralNetwork smaNeuralNetwork, TechnicalIndicators technicalIndicators, int index) {
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        double[] rsiResult = rsiNeuralNetwork.toPredict(getInputToRSINeuralNetwork(technicalIndicators, index - 2));
        double[] smaResult = smaNeuralNetwork.toPredict(getInputToSMANeuralNetwork(technicalIndicators, index));
        double[] macdResult = macdNeuralNetwork.toPredict(getInputToMACDNeuralNetwork(technicalIndicators, index));

        double[] input = {
            normalizePriceInput(closePriceIndicator.getTimeSeries().getTick(index).getOpenPrice().toDouble(), normalizerValue),
            normalizePriceInput(closePriceIndicator.getTimeSeries().getTick(index).getMinPrice().toDouble(), normalizerValue),
            normalizePriceInput(closePriceIndicator.getTimeSeries().getTick(index).getMaxPrice().toDouble(), normalizerValue),
            normalizePriceInput(closePriceIndicator.getValue(index).toDouble(), normalizerValue),
            closePriceIndicator.getTimeSeries().getTick(index).isBearish() ? 0d : 1d,
            macdResult[0],
            smaResult[0],
            rsiResult[0]
        };

        return input;
    }

    private static double[] getExpectedOutpuToPREDICTIONNeuralNetwork(TechnicalIndicators technicalIndicators, int index, double normalizerValue) {

        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();

        double[] output = {normalizePriceInput(closePriceIndicator.getValue(index + tradesForTraining).toDouble(), normalizerValue)};

        return output;
    }

    private static List<DataSetRow> getRSIDataSetTraining(int initialIndex, int finalIndex, TechnicalIndicators technicalIndicators) {

        List<DataSetRow> rsiDataSet = new ArrayList<>();
        for (int i = initialIndex; i < finalIndex; i++) {
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

        result = INDICATIVO_TENDENCIA_LATERAL;
        if (rsi14days.getValue(index).toDouble() <= 30 || rsi14days.getValue(index - 1).toDouble() <= 30 || rsi14days.getValue(index - 2).toDouble() <= 30) {
            result = INDICATIVO_TENDENCIA_ALTA;
        } else if (rsi14days.getValue(index).toDouble() >= 70 || rsi14days.getValue(index - 1).toDouble() >= 70 || rsi14days.getValue(index - 2).toDouble() >= 70) {
            result = INDICATIVO_TENDENCIA_BAIXA;
        }

        double[] output = {result};

        return output;
    }

    private static List<DataSetRow> getMACDDataSetTraining(int initialIndex, int finalIndex, TechnicalIndicators technicalIndicators) {

        List<DataSetRow> rsiDataSet = new ArrayList<>();
        for (int i = initialIndex; i < finalIndex; i++) {
            DataSetRow row = new DataSetRow(TrainingNeuralNetwork.getInputToMACDNeuralNetwork(technicalIndicators, i), TrainingNeuralNetwork.getExpectedOutpuToMACDNeuralNetwork(technicalIndicators, i));
            rsiDataSet.add(row);
        }

        return rsiDataSet;

    }

    private static double[] getInputToMACDNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {
        ClosePriceIndicator closePrice = technicalIndicators.getClosePrice();
        EMAIndicator ema5 = technicalIndicators.getEma5days();
        EMAIndicator ema35 = technicalIndicators.getEma35days();

        double[] input = {
            normalizeMACDInput(ema5.getValue(index).toDouble() - closePrice.getValue(index).toDouble()),
            normalizeMACDInput(ema5.getValue(index).toDouble() - ema35.getValue(index).toDouble())
        };

        return input;
    }

    private static double[] getExpectedOutpuToMACDNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {

        double result;
        ClosePriceIndicator closePrice = technicalIndicators.getClosePrice();
        EMAIndicator ema5 = technicalIndicators.getEma5days();
        EMAIndicator ema35 = technicalIndicators.getEma35days();

        Double macdLine = ema5.getValue(index).toDouble() - ema35.getValue(index).toDouble();
        Double signalLine = ema5.getValue(index).toDouble() - closePrice.getValue(index).toDouble();
        Double diference = Math.abs(macdLine - signalLine);

        result = INDICATIVO_TENDENCIA_LATERAL;
        if (diference > 0.001) {
            if (macdLine < signalLine) {
                result = INDICATIVO_TENDENCIA_BAIXA;
            } else { //if (macd.getValue(index).toDouble() >= (ema5.getValue(index).toDouble() - closePrice.getValue(index).toDouble())) {
                result = INDICATIVO_TENDENCIA_ALTA;
            }
        }

        double[] output = {result};

        return output;
    }

    private static List<DataSetRow> getSMADataSetTraining(int initialIndex, int finalIndex, TechnicalIndicators technicalIndicators) {

        List<DataSetRow> smaDataSet = new ArrayList<>();
        for (int i = initialIndex; i < finalIndex; i++) {
            DataSetRow row = new DataSetRow(TrainingNeuralNetwork.getInputToSMANeuralNetwork(technicalIndicators, i), TrainingNeuralNetwork.getExpectedOutpuToSMANeuralNetwork(technicalIndicators, i));
            smaDataSet.add(row);
        }

        return smaDataSet;
    }

    private static double[] getInputToSMANeuralNetwork(TechnicalIndicators technicalIndicators, int index) {

        SMAIndicator sma4days = technicalIndicators.getSma4days();
        SMAIndicator sma9days = technicalIndicators.getSma9days();
        SMAIndicator sma18days = technicalIndicators.getSma18days();

        double df1 = sma4days.getValue(index).toDouble() - sma9days.getValue(index).toDouble();
        double df2 = sma4days.getValue(index).toDouble() - sma18days.getValue(index).toDouble();

        double[] input = {
            normalizeMACDInput(df1),
            normalizeMACDInput(df2)
        };

        return input;
    }

    private static double[] getExpectedOutpuToSMANeuralNetwork(TechnicalIndicators technicalIndicators, int index) {

        SMAIndicator sma4days = technicalIndicators.getSma4days();
        SMAIndicator sma9days = technicalIndicators.getSma9days();
        SMAIndicator sma18days = technicalIndicators.getSma18days();

        double df1 = sma4days.getValue(index).toDouble() - sma9days.getValue(index).toDouble();
        double df2 = sma4days.getValue(index).toDouble() - sma18days.getValue(index).toDouble();

        double signal = INDICATIVO_TENDENCIA_LATERAL;
        if (df1 >= 0 && df2 >= 0) {
            signal = INDICATIVO_TENDENCIA_ALTA;
        } else if (df1 <= 0 && df2 <= 0) {
            signal = INDICATIVO_TENDENCIA_BAIXA;
        }

        double[] output = {signal};

        return output;
    }

    private static double normalizePriceInput(double priceValue, double normalizerValue) {
        return (priceValue / normalizerValue);
    }

    private static double normalizeRSIInput(double rsiValue) {
        return rsiValue / 100d;
    }

    private static double normalizeMACDInput(double macdValue) {
        BigDecimal bd = new BigDecimal(macdValue / 10d);
        bd.setScale(4, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }

    public static double getMaxPrice(TechnicalIndicators technicalIndicators) {
        double max = 0;

        for (int i = technicalIndicators.getTimeSeries().getBegin(); i <= technicalIndicators.getTimeSeries().getEnd(); i++) {
            if (technicalIndicators.getClosePrice().getTimeSeries().getTick(i).getMaxPrice().toDouble() > max) {
                max = technicalIndicators.getClosePrice().getTimeSeries().getTick(i).getMaxPrice().toDouble();
            }
        }

        return max;
    }
}
