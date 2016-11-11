/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import java.util.ArrayList;

/**
 *
 * @author Lucas
 */
public class Portfolio {

    private ArrayList<Ativo> ativos;
    private double profit;

    public Portfolio() {
        this.ativos = new ArrayList<>();
        this.profit = 0;
    }

    public void addAtivo(Ativo a) {
        ativos.add(a);
        evaluateProfit();
    }

    private void evaluateProfit() {
        profit = 0;
        for (Ativo ativo : ativos) {
            profit += ativo.getValues().getClose().doubleValue() - ativo.getValues().getBeforeClose().doubleValue();
        }
    }

    public double getProfit() {
        return profit;
    }

    @Override
    public String toString() {
        return "Portfolio{" + "ativos=" + ativos + ", profit=" + profit + '}';
    }

}
