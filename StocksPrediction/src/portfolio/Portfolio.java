/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import java.util.ArrayList;
import java.util.Calendar;

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

    public Portfolio() {
        numberPortfolio++;
        id = numberPortfolio;
        this.ativos = new ArrayList<>();
        this.profit = 0;
    }
    
    public Ativo getAtivo(int index) {
        return ativos.get(index);
    }

    public void addAtivo(Ativo a) {
        ativos.add(a);        
        evaluateProfit();
    }
    
    public void replaceAtivo(Ativo a, int index) {
        ativos.set(index, a); 
        evaluateProfit();
    }

    public Calendar getDate() {
        return ativos.get(0).getValues().getDate();
    }

    private void evaluateProfit() {
        variance = estimatedProfit = investment = profit = 0;

        for (Ativo ativo : ativos) {
            profit += ativo.getValues().getClose().doubleValue() - ativo.getValues().getBeforeClose().doubleValue();
            estimatedProfit += ativo.getValues().getPredictedClose().doubleValue() - ativo.getValues().getBeforeClose().doubleValue();
            investment += ativo.getValues().getBeforeClose().doubleValue();
            variance += ativo.getVariance();
        }
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
        return "Portfolio{" + "investimento=" + investment + ", ativos=" + ativos + ", profit=" + profit + '}';
    }

}
