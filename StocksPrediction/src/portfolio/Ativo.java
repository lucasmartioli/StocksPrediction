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

    public Ativo(String symbol, StockValues values, double variance) {
        this.symbol = symbol;
        this.values = values;
        this.variance = variance;
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

        if (variance > t.getVariance() || values.getIncrease() > t.getValues().getIncrease()) {
            return 1;
        } else if (variance < t.getVariance() || values.getIncrease() < t.getValues().getIncrease()) {
            return -1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Ativo{" + "symbol=" + symbol + ", values=" + values + ", variance=" + variance + '}';
    }

}
