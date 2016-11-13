/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import model.StockValues;

/**
 *
 * @author Lucas
 */
public class Ativo implements Comparable<Ativo> {

    private final String symbol;
    private final StockValues values;
    private final double variance;
    private final double accuracy;

    public Ativo(String symbol, StockValues values, double variance, double accuracy) {
        this.symbol = symbol;
        this.values = values;
        this.variance = variance;
        this.accuracy = accuracy;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public String getSymbol() {
        return symbol;
    }

    public StockValues getValues() {
        return values;
    }

    public double getVariance() {
        return variance;
    }

    @Override
    public int compareTo(Ativo t) {
        return Double.compare(values.getIncrease() + variance, t.getValues().getIncrease() + t.getVariance());
    }

    @Override
    public String toString() {
        return "Ativo{\n"
                + "symbol=" + symbol + "\n"
                + ", values=" + values + "\n"
                + ", variance=" + variance + '}';
    }

}
