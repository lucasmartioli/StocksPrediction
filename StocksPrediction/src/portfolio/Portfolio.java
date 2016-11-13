/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Lucas
 */
public class Portfolio {

    private static int numberPortfolio = 0;
    private final int id;
    private ArrayList<Ativo> ativos;
    private double investment;
    private double profit;
    private double estimatedProfit;
    private double variance;
    private double accuracy;
    private HashMap<String, Integer> amountsAtivos;

    public Portfolio() {
        numberPortfolio++;
        id = numberPortfolio;
        this.ativos = new ArrayList<>();
        amountsAtivos = new HashMap<>();
        this.profit = 0;
    }

    public Ativo getAtivo(int index) {
        return ativos.get(index);
    }

    public void addAtivo(Ativo a) {
        ativos.add(a);
        amountsAtivos.put(a.getSymbol(), 1);
        evaluateProfit();
    }

    public void setAmoutForAtivo(Ativo a, Integer amount) {
        amountsAtivos.put(a.getSymbol(), amount);
    }

    public void replaceAtivo(Ativo a, int index) {
        ativos.set(index, a);
        evaluateProfit();
    }

    public Calendar getDate() {
        return ativos.get(0).getValues().getDate();
    }

    public double getAccuracy() {
        return accuracy;
    }

    private void evaluateProfit() {
        accuracy = variance = estimatedProfit = investment = profit = 0;

        for (Ativo ativo : ativos) {
            profit += (ativo.getValues().getClose().doubleValue() - ativo.getValues().getBeforeClose().doubleValue()) * amountsAtivos.get(ativo.getSymbol());
            estimatedProfit += (ativo.getValues().getPredictedClose().doubleValue() - ativo.getValues().getBeforeClose().doubleValue()) * amountsAtivos.get(ativo.getSymbol());
            investment += ativo.getValues().getBeforeClose().doubleValue() * amountsAtivos.get(ativo.getSymbol());
            variance += ativo.getVariance() * amountsAtivos.get(ativo.getSymbol());
            accuracy += ativo.getAccuracy() * amountsAtivos.get(ativo.getSymbol());
        }
        accuracy /= ativos.size();
        variance /= ativos.size();
    }

    public double getProfit() {
        return profit;
    }

    public double getInvestment() {
        return investment;
    }

    public double getEstimatedProfit() {
        return estimatedProfit;
    }

    public int getId() {
        return id;
    }

    public double getVariance() {
        return variance;
    }

    @Override
    public String toString() {
        return "Portfolio{ "
                + "\n"
                + "id=" + id + "\n"
                + ", ativos=\n" + ativos + "\n"
                + ", investment=" + investment + "\n"
                + ", profit=" + profit + "\n"
                + ", estimatedProfit=" + estimatedProfit + "\n"
                + ", variance=" + variance + '}';
    }

    public String toStringResum() {
        String r = "Portfolio id: " + id + "\n"
                + "Data: " + ativos.get(1).getValues().getDate().getTime() + "\n"
                + "Percentual lucro: " + (getProfit()/getInvestment()) * 100 + "%\n"
                + "Ativos:\n";

        for (Map.Entry<String, Integer> entry : amountsAtivos.entrySet()) {
            r += entry.getKey() + " X " + entry.getValue() + "\n";

        }
        return r;
    }

}
