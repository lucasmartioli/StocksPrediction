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
    private double covariance;
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
        if (!amountsAtivos.containsKey(a.getSymbol())) {
            setAmoutForAtivo(a, 1);
        }
        evaluateProfit();
    }

    public void setAmoutForAtivo(Ativo a, Integer amount) {
        amountsAtivos.put(a.getSymbol(), amount);
    }

//    public void replaceAtivo(Ativo a, int index) {
//        setAmoutForAtivo(ativos.get(index), 0);
//        setAmoutForAtivo(a, 1);
//        ativos.set(index, a);
//        evaluateProfit();
//    }
    public Calendar getDate() {
        return ativos.get(0).getValues().getDate();
    }

    public Integer getAmount(Ativo a) {
        return amountsAtivos.get(a.getSymbol());
    }

    public double getAccuracy() {
        return accuracy;
    }

    private void evaluateProfit() {
        covariance = accuracy = variance = estimatedProfit = investment = profit = 0;

        int t = 0;

        for (Ativo ativo : ativos) {
            profit += ((ativo.getValues().getClose().doubleValue() - ativo.getValues().getBeforeRealClose().doubleValue())) * amountsAtivos.get(ativo.getSymbol());
            estimatedProfit += ((ativo.getValues().getPredictedClose().doubleValue() - ativo.getValues().getBeforeClose().doubleValue()) / ativo.getValues().getBeforeClose().doubleValue()) * amountsAtivos.get(ativo.getSymbol());
            investment += ativo.getValues().getBeforeRealClose().doubleValue() * amountsAtivos.get(ativo.getSymbol());            
            variance += ativo.getVariance() * amountsAtivos.get(ativo.getSymbol());
            accuracy += ativo.getAccuracy() * amountsAtivos.get(ativo.getSymbol());
            if (amountsAtivos.get(ativo.getSymbol()) > 0) {
                t++;
            }
        }

        accuracy /= t;
        variance /= t;
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

    public static String cabecExcel() {
        return "Teste" + "\t" + "Data" + "\t" + "Investimento" + "\t" + "Lucro" + "\t" + "Estimativa retorno" + "\t" + "Estimativa de Lucro" + "\t" + "% de Lucro" + "\t" + "Ativos";
    }

    public String toExcel() {
        return "\t" + ativos.get(1).getValues().getDate().getTime() + "\t" + investment + "\t" + profit + "\t" + estimatedProfit + "\t" + "\t" + "\t" + ativosToString();
    }
    
    public String ativosToString() {
        String r = "";
         for (Map.Entry<String, Integer> entry : amountsAtivos.entrySet()) {
            r += entry.getKey() + " X " + entry.getValue() + "---" + searchAtivo(entry.getKey()).getValues().getBeforeRealClose()+ "  " + searchAtivo(entry.getKey()).getValues().getClose();

        }
         return r;
    }
    
    private Ativo searchAtivo(String k) {
        for (Ativo ativo : ativos) {
            if (ativo.getSymbol() == k)
                return ativo;
            
        }
        return null;
        
    }

    public String toStringResum() {
        String r = "Portfolio id: " + id + "\n"
                + "Data: " + ativos.get(1).getValues().getDate().getTime() + "\n"
                + "Percentual lucro: " + (getProfit() / getInvestment()) * 100 + "%\n"
                + "Ativos:\n";

        for (Map.Entry<String, Integer> entry : amountsAtivos.entrySet()) {
            r += entry.getKey() + " X " + entry.getValue() + "\n";

        }
        return r;
    }

}
