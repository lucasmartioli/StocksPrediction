/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetworks;

import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.volume.OnBalanceVolumeIndicator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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

    public static void toTrain(Company company, int initialIndex, int finalIndex) {
        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();

        PredictionNeuralNetwork rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI", 3, 1, TransferFunctionType.SIGMOID);
        rsiNeuralNetwork.setLearningRateForLearning(0.825d);
        rsiNeuralNetwork.setMaxErrorForLearning(0.0001d);
        rsiNeuralNetwork.setMaxIterationsForLearning(500);
        rsiNeuralNetwork.toTrain(getRSIDataSetTraining(initialIndex, finalIndex, technicalIndicators));

        PredictionNeuralNetwork smaNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "SMA", 2, 1, TransferFunctionType.SIGMOID);
        smaNeuralNetwork.setLearningRateForLearning(0.825d);
        smaNeuralNetwork.setMaxErrorForLearning(0.0001d);
        smaNeuralNetwork.setMaxIterationsForLearning(500);
        smaNeuralNetwork.toTrain(getSMADataSetTraining(initialIndex, finalIndex, technicalIndicators));

        PredictionNeuralNetwork macdNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "MACD", 2, 1, TransferFunctionType.SIGMOID);
        macdNeuralNetwork.setLearningRateForLearning(0.825d);
        macdNeuralNetwork.setMaxErrorForLearning(0.0001d);
        macdNeuralNetwork.setMaxIterationsForLearning(500);
        macdNeuralNetwork.toTrain(getMACDDataSetTraining(initialIndex, finalIndex, technicalIndicators));

        PredictionNeuralNetwork obvNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "OBV", 3, 1, TransferFunctionType.SIGMOID);
//        obvNeuralNetwork.setLearningRateForLearning(0.5d);
//        obvNeuralNetwork.setMaxErrorForLearning(0.001d);
//        obvNeuralNetwork.setMaxIterationsForLearning(500);
//        obvNeuralNetwork.toTrain(getOBVDataSetTraining(initialIndex, finalIndex, technicalIndicators));

        rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI");
        smaNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "SMA");
        macdNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "MACD");
        obvNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "OBV");

        PredictionNeuralNetwork prediction = new PredictionNeuralNetwork(company.getSimbolo(), "PREDICTION", 14, 2, TransferFunctionType.SIGMOID);
        prediction.setLearningRateForLearning(0.525d);
        prediction.setMaxErrorForLearning(0d);
        prediction.setMaxIterationsForLearning(5000);
        prediction.toTrain(getPREDICTIONDataSetTraining(initialIndex, finalIndex, technicalIndicators, rsiNeuralNetwork, smaNeuralNetwork, macdNeuralNetwork, obvNeuralNetwork));
    }

    public static double[] toPredict(Company company, int finalIndex) {
        TechnicalIndicators technicalIndicators = company.getTechnicalIndicators();

        PredictionNeuralNetwork macdNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "MACD");
        PredictionNeuralNetwork rsiNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "RSI");
        PredictionNeuralNetwork smaNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "SMA");
        PredictionNeuralNetwork obvNeuralNetwork = new PredictionNeuralNetwork(company.getSimbolo(), "OBV");
        PredictionNeuralNetwork prediction = new PredictionNeuralNetwork(company.getSimbolo(), "PREDICTION");

        double[] result = prediction.toPredict(getInputToPREDICTIONNeuralNetwork(obvNeuralNetwork, macdNeuralNetwork, rsiNeuralNetwork, smaNeuralNetwork, technicalIndicators, finalIndex));

        return result;
    }

    private static List<DataSetRow> getPREDICTIONDataSetTraining(int initialIndex, int finalIndex, TechnicalIndicators technicalIndicators, PredictionNeuralNetwork rsiNeuralNetwork, PredictionNeuralNetwork smaNeuralNetwork, PredictionNeuralNetwork macdNeuralNetwork, PredictionNeuralNetwork obvNeuralNetwork) {

        List<DataSetRow> predictionDataSet = new ArrayList<>();
        for (int i = initialIndex; i < finalIndex - tradesForTraining; i++) {
            DataSetRow row = new DataSetRow(TrainingNeuralNetwork.getInputToPREDICTIONNeuralNetwork(obvNeuralNetwork, macdNeuralNetwork, rsiNeuralNetwork, smaNeuralNetwork, technicalIndicators, i), TrainingNeuralNetwork.getExpectedOutpuToPREDICTIONNeuralNetwork(technicalIndicators, i));
            predictionDataSet.add(row);
        }

        return predictionDataSet;
    }

    private static double[] getInputToPREDICTIONNeuralNetwork(PredictionNeuralNetwork obvNeuralNetwork, PredictionNeuralNetwork macdNeuralNetwork, PredictionNeuralNetwork rsiNeuralNetwork, PredictionNeuralNetwork smaNeuralNetwork, TechnicalIndicators technicalIndicators, int index) {
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        OnBalanceVolumeIndicator obv = technicalIndicators.getObv();
        double[] rsiResult = rsiNeuralNetwork.toPredict(getInputToRSINeuralNetwork(technicalIndicators, index));
        double[] rsiResult2 = rsiNeuralNetwork.toPredict(getInputToRSINeuralNetwork(technicalIndicators, index - 1));
        double[] rsiResult3 = rsiNeuralNetwork.toPredict(getInputToRSINeuralNetwork(technicalIndicators, index - 2));
        double[] smaResult = smaNeuralNetwork.toPredict(getInputToSMANeuralNetwork(technicalIndicators, index));
        double[] smaResult2 = smaNeuralNetwork.toPredict(getInputToSMANeuralNetwork(technicalIndicators, index - 1));
        double[] smaResult3 = smaNeuralNetwork.toPredict(getInputToSMANeuralNetwork(technicalIndicators, index - 2));
        double[] macdResult = macdNeuralNetwork.toPredict(getInputToMACDNeuralNetwork(technicalIndicators, index));
        double[] macdAnteriorResult = macdNeuralNetwork.toPredict(getInputToMACDNeuralNetwork(technicalIndicators, index - 1));
        double[] macdAnteriorResult2 = macdNeuralNetwork.toPredict(getInputToMACDNeuralNetwork(technicalIndicators, index - 2));
//        double[] obvResult = obvNeuralNetwork.toPredict(getInputToOBVNeuralNetwork(technicalIndicators, index));

        double obvResult = INDICATIVO_TENDENCIA_LATERAL;
        if (obv.getValue(index).toDouble() >= obv.getValue(index - 1).toDouble() && obv.getValue(index - 1).toDouble() >= obv.getValue(index - 1).toDouble()) {
            obvResult = INDICATIVO_TENDENCIA_ALTA;
        } else if (obv.getValue(index).toDouble() < obv.getValue(index - 1).toDouble() && obv.getValue(index - 1).toDouble() < obv.getValue(index - 1).toDouble()) {
            obvResult = INDICATIVO_TENDENCIA_BAIXA;
        }

        double trendIndicator = INDICATIVO_TENDENCIA_LATERAL;
        System.out.println("Sequencia de precos: " + closePriceIndicator.getValue(index - 2).toDouble() + ", " + closePriceIndicator.getValue(index - 1).toDouble() + ", " + closePriceIndicator.getValue(index).toDouble());
        if (closePriceIndicator.getValue(index).toDouble() >= closePriceIndicator.getValue(index - 1).toDouble() && closePriceIndicator.getValue(index - 1).toDouble() >= closePriceIndicator.getValue(index - 2).toDouble()) {
            trendIndicator = INDICATIVO_TENDENCIA_ALTA;
        } else if (closePriceIndicator.getValue(index).toDouble() < closePriceIndicator.getValue(index - 1).toDouble() && closePriceIndicator.getValue(index - 1).toDouble() < closePriceIndicator.getValue(index - 2).toDouble()) {
            trendIndicator = INDICATIVO_TENDENCIA_BAIXA;
        }

        System.err.println("Preco Atual: " + closePriceIndicator.getValue(index).toDouble());
//        System.out.println("Preco Futuro: " + closePriceIndicator.getValue(index + tradesForTraining).toDouble());

        double[] input = {
            normalizePriceInput(closePriceIndicator.getValue(index).toDouble()),
            normalizePriceInput(closePriceIndicator.getValue(index - 1).toDouble()),
            normalizePriceInput(closePriceIndicator.getValue(index - 2).toDouble()),
            trendIndicator,
            macdAnteriorResult2[0],
            macdAnteriorResult[0],
            macdResult[0],
            rsiResult[0],
            rsiResult2[0],
            rsiResult3[0],
            smaResult[0],
            smaResult2[0],            
            smaResult3[0],            
            obvResult
            //obvResult[0]
//            convertSignalInput(macdAnteriorResult2[0]),
//            convertSignalInput(macdAnteriorResult[0]),
//            convertSignalInput(macdResult[0]),
//            convertSignalInput(rsiResult[0]),
//            convertSignalInput(smaResult[0]),
//            convertSignalInput(smaResult2[0]),
//            convertSignalInput(obvResult[0])
        };

        return input;
    }

    private static double[] getExpectedOutpuToPREDICTIONNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {

        double result;
        ClosePriceIndicator closePriceIndicator = technicalIndicators.getClosePrice();
        double diference = closePriceIndicator.getValue(index + tradesForTraining).toDouble() - closePriceIndicator.getValue(index).toDouble();

        if (diference > 0) {
            result = INDICATIVO_TENDENCIA_ALTA;
        } else {
            result = INDICATIVO_TENDENCIA_BAIXA;
        }

//         normalizePriceInput(closePriceIndicator.getValue(index + tradesForTraining).toDouble()
        double[] output = {Math.abs(diference)/10d, result};
//        double[] output = {result};

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

    private static List<DataSetRow> getOBVDataSetTraining(int initialIndex, int finalIndex, TechnicalIndicators technicalIndicators) {

        List<DataSetRow> rsiDataSet = new ArrayList<>();
        for (int i = initialIndex; i < finalIndex; i++) {
            DataSetRow row = new DataSetRow(TrainingNeuralNetwork.getInputToOBVNeuralNetwork(technicalIndicators, i), TrainingNeuralNetwork.getExpectedOutpuToOBVNeuralNetwork(technicalIndicators, i));
            rsiDataSet.add(row);
        }

        return rsiDataSet;

    }

    private static double[] getInputToOBVNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {
        OnBalanceVolumeIndicator obv = technicalIndicators.getObv();

        double[] input = {
            normalizeOBVInput(obv.getValue(index).toDouble()),
            normalizeOBVInput(obv.getValue(index - 1).toDouble()),
            normalizeOBVInput(obv.getValue(index - 2).toDouble())
        };

        return input;
    }

    private static double[] getExpectedOutpuToOBVNeuralNetwork(TechnicalIndicators technicalIndicators, int index) {

        double result;
        OnBalanceVolumeIndicator obv = technicalIndicators.getObv();

        result = INDICATIVO_TENDENCIA_LATERAL;
        if (obv.getValue(index).toDouble() >= obv.getValue(index - 1).toDouble() && obv.getValue(index - 1).toDouble() >= obv.getValue(index - 1).toDouble()) {
            result = INDICATIVO_TENDENCIA_ALTA;
        } else if (obv.getValue(index).toDouble() < obv.getValue(index - 1).toDouble() && obv.getValue(index - 1).toDouble() < obv.getValue(index - 1).toDouble()) {
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


    private static double convertSignalInput(double signal) {

        double convertedSignal = INDICATIVO_TENDENCIA_BAIXA;

        if (signal > 0.33d && signal < 0.66d) {
            convertedSignal = INDICATIVO_TENDENCIA_LATERAL;
        } else if (signal > 0.66d) {
            convertedSignal = INDICATIVO_TENDENCIA_ALTA;
        }

        System.out.println("Sinal para converter: " + signal + " convertido para: " + convertedSignal);

        return convertedSignal;

    }

    private static double normalizePriceInput(double priceValue) {
        return priceValue / 100d;
    }

    private static double normalizeRSIInput(double rsiValue) {
        return rsiValue / 100d;
    }

    private static double normalizeOBVInput(double obvValue) {
        double v1 = obvValue / 1000000000d;
        v1 = v1 / 10d;
        return v1;
    }

    private static double normalizeMACDInput(double macdValue) {
        BigDecimal bd = new BigDecimal(macdValue/10d);
        bd.setScale(4, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
}
