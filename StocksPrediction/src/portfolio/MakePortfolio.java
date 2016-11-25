/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import loadingcompany.LoadingCompany;
import model.Company;
import model.StockValues;
import neuralnetworks.TrainingNeuralNetwork;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;
import org.ojalgo.finance.portfolio.MarkowitzModel;
import org.ojalgo.finance.portfolio.PortfolioContext;
import stocksprediction.StocksPrediction;
import technicalindicators.TechnicalIndicators;

/**
 *
 * @author Lucas
 */
public class MakePortfolio {

    private final ArrayList<String> companySymbols;
    private ArrayList<Portfolio> portfolios;
    private ArrayList<Company> companies;
    private final Period period;
    private final int portfolioSize;
    private final int tradesToPredict = 5;

    public MakePortfolio(ArrayList<String> companySymbols, Period period, int portfolioSize) {
        this.companySymbols = companySymbols;
        this.period = period;
        this.portfolioSize = portfolioSize;
    }

    public ArrayList<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void make() throws IOException {

        this.testsNNs();

        int indexFutureValues = 0;
        boolean finalizou = false;
        portfolios = new ArrayList<>();
        while (true) {
            ArrayList<Ativo> ativosDoDia = new ArrayList<>();

            for (Company company : companies) {
                finalizou = indexFutureValues >= company.getFutureValues().size();
                if (finalizou) {

                    break;
                }

                Ativo a = new Ativo(company.getSimbolo(), company.getFutureValues().get(indexFutureValues), company.getVariance(), company.getAccuracy());
                ativosDoDia.add(a);
            }

            if (finalizou) {
                break;
            }

//            System.out.println("Ativos do dia: ");
//            for (Ativo ativo : ativosDoDia) {
//                System.out.println(ativo);
//                
//            }
            GeneticPortfolio geneticPortfolio = new GeneticPortfolio(portfolioSize, ativosDoDia);
            portfolios.add(geneticPortfolio.generate());
            indexFutureValues++;
        }
    }

    private void testsNNs() throws IOException {

        companies = new ArrayList<>();

        for (String companySymbol : companySymbols) {
            System.out.println("Testes para " + companySymbol + " iniciados.");

            Company company = LoadingCompany.loading(companySymbol, period.getDateInitial(), period.getDateFinal());

            TimeSeries timeSeries = company.getTechnicalIndicators().getTimeSeries();
            int indicadorInicial = timeSeries.getBegin();
            int indicadorFinal = timeSeries.getEnd();
            company.setNormalizerValue(TrainingNeuralNetwork.getMaxPrice(company.getTechnicalIndicators()));

            indicadorInicial += 36; // desconsiderando valores iniciais onde os indicadores não fazem sentido, pois ainda não tem informações suficientes

            int inicioTreinamento = indicadorInicial;

            Calendar di = Calendar.getInstance();
            di.set(2016, 5, 30, 0, 0, 0);

            int finalTreinamento = 0;

            for (int i = indicadorInicial; i < indicadorFinal; i++) {
                if (di.getTime().toString().equals(timeSeries.getTick(i).getEndTime().toCalendar(Locale.ROOT).getTime().toString())) {
                    finalTreinamento = i;
                    break;
                }
            }

//            int finalTreinamento = Math.round(((float) (indicadorFinal * period.getPercentageToTrainning())));
            int inicioTestes = finalTreinamento + 1;
            int finalTestes = indicadorFinal;//Math.round(((float) (indicadorFinal * 1 - period.getPercentageToTest())));
            TrainingNeuralNetwork.toTrain(company, inicioTreinamento, finalTreinamento, company.getNormalizerValue());
            double anteriorCalculado = TrainingNeuralNetwork.toPredict(company, inicioTestes - tradesToPredict, company.getNormalizerValue());
            double anteriorReal = timeSeries.getTick(inicioTestes - tradesToPredict).getClosePrice().toDouble();
            double totalTests = 0d;
            double acerto = 0d;
            int indexEndAccuracy = inicioTestes + Math.round(((float) ((finalTestes - inicioTestes) * 0.5)));
            ArrayList<StockValues> futureValues = new ArrayList<>();

            for (int i = inicioTestes; i < finalTestes;) {
//                System.out.println("Lu" + timeSeries.getTick(i).getEndTime().toCalendar(Locale.ROOT).getTime());

                double saida = TrainingNeuralNetwork.toPredict(company, i, company.getNormalizerValue());

                Tick tick = timeSeries.getTick(i);

                i += tradesToPredict;
                if (i >= finalTestes) {
                    break;
                }

                if (indexEndAccuracy > i) {

                    StockValues futureStockValue;
//                    if (anteriorCalculado == 0d) {
//                        futureStockValue = new StockValues(timeSeries.getTick(i).getEndTime().toCalendar(Locale.ROOT), timeSeries.getTick(i).getClosePrice().toDouble(), saida, tick.getClosePrice().toDouble());
//                    } else {
                    futureStockValue = new StockValues(timeSeries.getTick(i).getEndTime().toCalendar(Locale.ROOT), timeSeries.getTick(i).getClosePrice().toDouble(), saida, anteriorCalculado, tick.getClosePrice().toDouble());
//                    }
                    double increase = ((saida - timeSeries.getTick(i).getClosePrice().toDouble()) / timeSeries.getTick(i).getClosePrice().toDouble()) * 100d;
                    futureStockValue.setIncrease(increase);
                    futureValues.add(futureStockValue);
                } else {
                    if ((anteriorCalculado < saida) && (anteriorReal < timeSeries.getTick(i).getClosePrice().toDouble())
                            || (anteriorCalculado > saida) && (anteriorReal > timeSeries.getTick(i).getClosePrice().toDouble())) {
                        acerto += 1;
                    }
                    totalTests++;
                }

                anteriorCalculado = saida;
                anteriorReal = timeSeries.getTick(i).getClosePrice().toDouble();

            }

            double accuracy = acerto / totalTests;
            company.setAccuracy(accuracy);
            System.out.println(accuracy);

            company.setFutureValues(futureValues);
            companies.add(company);
            System.out.println("Finalizado.");
        }

    }

}
