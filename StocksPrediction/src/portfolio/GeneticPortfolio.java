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
    private final int maxInteractions = 100;
    private final int sizeGeneration = 15;

    public GeneticPortfolio(int portfolioSize, ArrayList<Ativo> ativos) {
        this.portfolioSize = portfolioSize;
        this.ativos = ativos;
    }

    public double objectiveFunction(Portfolio p) {
        return p.getEstimatedProfit() - p.getVariance();
    }

    private void intializePopulation() {

        Random gerador = new Random(Calendar.getInstance().getTimeInMillis());
        population = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            Portfolio p = new Portfolio();

            for (int j = 0; j < portfolioSize; j++) {
                p.addAtivo(ativos.get(gerador.nextInt(ativos.size() - 1)));

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
        for (int i = 0; i < 10; i++) {
            if (objectiveFunction(bestSolutionInPopulation) < objectiveFunction(population.get(i))) {
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
            generation.add(this.crossOver(best, population.get(gerador.nextInt(population.size() - 1))));
        }

        for (Portfolio son : generation) {
            this.addInPopulationElitism(son);
        }

    }

    private Portfolio crossOver(Portfolio p1, Portfolio p2) {

        Portfolio p3 = new Portfolio();

        Random gerador = new Random(Calendar.getInstance().getTimeInMillis());

        int lineCross = gerador.nextInt(portfolioSize - 2);

        for (int i = 0; i < lineCross; i++) {
            p3.addAtivo(p1.getAtivo(i));
        }

        for (int i = lineCross + 1; i < portfolioSize - 1; i++) {
            p3.addAtivo(p2.getAtivo(i));
        }

        return p3;
    }

    private void addInPopulationElitism(Portfolio son) {
        for (int i = 0; i < population.size() - 1; i++) {
            if (objectiveFunction(son) > objectiveFunction(population.get(i))) {
                population.set(i, son);
                break;
            }
        }
    }

}
