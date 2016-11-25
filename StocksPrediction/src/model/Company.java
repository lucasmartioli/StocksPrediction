/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import technicalindicators.TechnicalIndicators;
import yahoofinance.histquotes.HistoricalQuote;

/**
 *
 * @author Lucas
 */
public class Company {

    private final String simbolo;
    private TechnicalIndicators technicalIndicators;
    private StockValues currentValues;
    private ArrayList<StockValues> futureValues;
    private double accuracy;

    private List<HistoricalQuote> historicValues;
    private double normalizerValue;
    private double variance;

    public Company(String simbolo) {
        this.simbolo = simbolo;
    }

    public TechnicalIndicators getTechnicalIndicators() {
        return technicalIndicators;
    }

    public void calculateTechnicalIndicators() {
        this.technicalIndicators = new TechnicalIndicators(historicValues);
    }

    public List<HistoricalQuote> getHistoricValues() {
        return historicValues;
    }

    public void setHistoricValues(List<HistoricalQuote> historicValues) {
        this.historicValues = historicValues;
        this.calculateTechnicalIndicators();
        this.evaluateVariance();
    }

    public String getSimbolo() {
        return simbolo;
    }

    public StockValues getCurrentValues() {
        return currentValues;
    }

    public void setCurrentValues(StockValues currentValues) {
        this.currentValues = currentValues;
    }

    public double getNormalizerValue() {
        return normalizerValue;
    }

    public void setNormalizerValue(double normalizerValue) {
        this.normalizerValue = normalizerValue;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public ArrayList<StockValues> getFutureValues() {
        return futureValues;
    }

    public void setFutureValues(ArrayList<StockValues> futureValues) {
        Collections.sort(futureValues);

//        for (StockValues futureValue : futureValues) {
//            System.out.println(futureValue.getDate().getTime());
//        }

        this.futureValues = futureValues;
    }

    public double getVariance() {
        return variance;
    }

    private void evaluateVariance() {

        int limit = this.getTechnicalIndicators().getTimeSeries().getEnd();

        double mean = 0d;

        for (int i = 0; i < limit; i++) {
            mean += this.getTechnicalIndicators().getClosePrice().getValue(i).toDouble();
        }

        mean /= limit;

        double total = 0d;
        for (int i = 0; i < limit; i++) {
            total += Math.pow(getTechnicalIndicators().getClosePrice().getValue(i).toDouble() - mean, 2);
        }

        variance = total / limit - 1;

    }

    @Override
    public String toString() {
        return "Company{" + "simbolo=" + simbolo + ", futureValues=" + futureValues + ", normalizerValue=" + normalizerValue + ", variance=" + variance + '}';
    }

}
