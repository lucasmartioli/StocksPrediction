/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

/**
 *
 * @author Lucas
 */
public class GeneticPortfolio {

    private final int portfolioSize;
    private final ArrayList<Ativo> ativos;
    private ArrayList<Portfolio> population;
    private final int populationSize = 5;
    private final int maxInteractions = 500;
    private final int maxAmoutAtivo = 10;
    private final int sizeGeneration = 20;

    public GeneticPortfolio(int portfolioSize, ArrayList<Ativo> ativos) {
        this.portfolioSize = portfolioSize;
        this.ativos = ativos;
    }

    public double objectiveFunction(Portfolio p) {

//        return p.getProfit()/p.getInvestment(); //2.84%
//        return p.getEstimatedProfit() + p.getAccuracy() - p.getVariance();
//        return p.getEstimatedProfit() + p.getAccuracy() - p.getVariance();
        //return p.getEstimatedProfit() - p.getVariance() * p.getEstimatedProfit(); //1.69%
//        return p.getEstimatedProfit() + p.getAccuracy();
//        return p.getAccuracy(); 19.79%
//        return p.getAccuracy() - p.getVariance(); // 3.04%
        return p.getEstimatedProfit() + p.getAccuracy() - p.getVariance() * p.getEstimatedProfit(); // 20.82%

    }

    private void intializePopulation() {

        Random gerador = new Random(Calendar.getInstance().getTimeInMillis());
        population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            Portfolio p = new Portfolio();

            for (int j = 0; j < portfolioSize; j++) {
                Ativo ativo = ativos.get(gerador.nextInt(ativos.size() - 1));
                p.setAmoutForAtivo(ativo, gerador.nextInt(maxAmoutAtivo));
                p.addAtivo(ativo);
            }
            population.add(p);

        }

    }

    public Portfolio generate() {
        intializePopulation();
        Portfolio bestPortfolio = population.get(1);

        for (int i = 0; i < maxInteractions; i++) {

            Portfolio bestSolutionInPopulation = findBestSolutionInPopulation();
            if (objectiveFunction(bestPortfolio) < objectiveFunction(bestSolutionInPopulation)) {
                bestPortfolio = bestSolutionInPopulation;
            }

            generateNewPopulation();

        }

        return bestPortfolio;
    }

    private Portfolio findBestSolutionInPopulation() {
        Portfolio bestSolutionInPopulation;

        bestSolutionInPopulation = population.get(1);
        for (int i = 0; i < population.size(); i++) {
            if (objectiveFunction(population.get(i)) > objectiveFunction(bestSolutionInPopulation)) {
                bestSolutionInPopulation = population.get(i);
            }
        }

        return bestSolutionInPopulation;
    }

    private void generateNewPopulation() {

        Portfolio best = findBestSolutionInPopulation();

        Random gerador = new Random(Calendar.getInstance().getTimeInMillis());

        ArrayList<Portfolio> generation = new ArrayList<>();

        for (int i = 0; i < sizeGeneration; i++) {
            generation.add(crossOver(best, population.get(gerador.nextInt(population.size() - 1))));
        }

        for (Portfolio son : generation) {
            this.addInPopulationElitism(son);
        }

    }

    private Portfolio crossOver(Portfolio p1, Portfolio p2) {

        Portfolio p3 = new Portfolio();

        Random gerador = new Random(Calendar.getInstance().getTimeInMillis());

        int lineCross = gerador.nextInt(portfolioSize);

        int i = 0;
        for (; i < lineCross; i++) {
            p3.setAmoutForAtivo(p1.getAtivo(i), /*p1.getAmount(p1.getAtivo(i))*/gerador.nextInt(maxAmoutAtivo));
            p3.addAtivo(p1.getAtivo(i));
        }

        for (; i < portfolioSize; i++) {
            p3.setAmoutForAtivo(p2.getAtivo(i), gerador.nextInt(maxAmoutAtivo));
            p3.addAtivo(p2.getAtivo(i));
        }

//        gerador = new Random(Calendar.getInstance().getTimeInMillis());
//        if (gerador.nextBoolean()) {
//            int indexMutation = gerador.nextInt(portfolioSize - 1);
//            p3.replaceAtivo(p11.getAtivo(indexMutation), indexMutation);
//            
//        }
        return p3;
    }

    private void addInPopulationElitism(Portfolio son) {
        for (int i = 0; i < population.size(); i++) {
            if (objectiveFunction(son) > objectiveFunction(population.get(i))) {
                population.set(i, son);
                break;
            }
        }
    }

}
