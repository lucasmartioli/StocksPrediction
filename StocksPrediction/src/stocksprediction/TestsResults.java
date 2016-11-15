/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stocksprediction;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import loadingcompany.LoadingCompany;
import model.Company;
import neuralnetworks.TrainingNeuralNetwork;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import technicalindicators.TechnicalIndicators;
import yahoofinance.histquotes.HistoricalQuote;

/**
 *
 * @author Lucas
 */
public class TestsResults {

    public static void main(String[] args) {
        int tradesToPredict = 1;

        try {
            Calendar di = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            di.set(14, 6, 1, 12, 0);
            Calendar df = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            df.set(16, 9, 1, 12, 0);

            Company company = LoadingCompany.loading("CPLE6", di, df);

            TimeSeries timeSeries = company.getTechnicalIndicators().getTimeSeries();
            int indicadorInicial = timeSeries.getBegin();
            int indicadorFinal = timeSeries.getEnd();
            double normalizerValue = getMaxPrice(company.getTechnicalIndicators());

            indicadorInicial += 36; // desconsiderando valores iniciais onde os indicadores não fazem tanto sentido.

            TimeSeriesCollection dataset = new TimeSeriesCollection();

            int inicioTreinamento = indicadorInicial;
            int finalTreinamento = Math.round(((float) (indicadorFinal * 0.88d)));
            int inicioTestes = finalTreinamento + 1;
            int finalTestes = indicadorFinal;

            TrainingNeuralNetwork.toTrain(company, inicioTreinamento, finalTreinamento, normalizerValue);

            org.jfree.data.time.TimeSeries chartResultados = new org.jfree.data.time.TimeSeries("Calculado");
            org.jfree.data.time.TimeSeries chartDiferencas = new org.jfree.data.time.TimeSeries("Diferenca");
            org.jfree.data.time.TimeSeries chartCandleC = new org.jfree.data.time.TimeSeries("Candle Calculado");
            org.jfree.data.time.TimeSeries chartCandleR = new org.jfree.data.time.TimeSeries("Candle Real");
            double anteriorCalculado = 0d;
            double anteriorReal = 0d;
            double totalTests = 0d;
            double acerto = 0d;
            double diferencaTotal = 0d;
            for (int i = inicioTestes; i <= finalTestes; i++) {

                double saida = TrainingNeuralNetwork.toPredict(company, i, normalizerValue);
                System.out.println("Saida do algoritmo: " + saida);
                System.out.println("");
                System.out.println("Dia sendo analisado: " + timeSeries.getTick(i));

                if (i + tradesToPredict <= finalTestes) {
                    System.out.println("Diferença: " + (saida - timeSeries.getTick(i + tradesToPredict).getClosePrice().toDouble()) + " Dia no futuro: " + timeSeries.getTick(i + tradesToPredict));
                    Tick tick = timeSeries.getTick(i + tradesToPredict);
                    chartResultados.add(new Day(tick.getEndTime().toDate()), saida);
                    chartDiferencas.add(new Day(tick.getEndTime().toDate()), saida - timeSeries.getTick(i + tradesToPredict).getClosePrice().toDouble());
                    diferencaTotal += Math.abs(saida - timeSeries.getTick(i + tradesToPredict).getClosePrice().toDouble());
                    if ((anteriorCalculado < saida) && (anteriorReal < timeSeries.getTick(i + tradesToPredict).getClosePrice().toDouble())
                            || (anteriorCalculado > saida) && (anteriorReal > timeSeries.getTick(i + tradesToPredict).getClosePrice().toDouble())) {
                        acerto += 1;
                    }

                    totalTests++;
                    chartCandleC.add(new Day(tick.getEndTime().toDate()), (anteriorCalculado < saida) ? 1 : 0);
                    chartCandleR.add(new Day(tick.getEndTime().toDate()), (anteriorReal < timeSeries.getTick(i + tradesToPredict).getClosePrice().toDouble()) ? 1 : 0);
                    anteriorCalculado = saida;
                    anteriorReal = timeSeries.getTick(i + tradesToPredict).getClosePrice().toDouble();
                } else {
                    System.out.println("Não tem como saber o futuro ainda!!");
                }
                System.out.println("");
            }

            System.out.println("Total de testes: " + totalTests + " Total de acertos: " + acerto + " Diferença total: " + diferencaTotal);
            System.out.println("Percentual de acertos: " + (acerto / totalTests) * 100d + "%");
            System.out.println("Média de diferença: " + (diferencaTotal / totalTests));

            dataset.addSeries(buildChartTimeSeries(inicioTestes, timeSeries, company.getTechnicalIndicators().getClosePrice(), "Close Price"));
//            dataset.addSeries(buildChartTimeSeries(inicioTestes, timeSeries, vivo.getTechnicalIndicators().getSma18days(), "SMA 18 dias"));
//            dataset.addSeries(buildChartTimeSeries(inicioTestes, timeSeries, vivo.getTechnicalIndicators().getSma4days(), "SMA 4 dias"));
//            dataset.addSeries(buildChartTimeSeries(inicioTestes, timeSeries, vivo.getTechnicalIndicators().getSma9days(), "SMA 9 dias"));
            dataset.addSeries(chartResultados);
            dataset.addSeries(chartDiferencas);
            dataset.addSeries(chartCandleC);
            dataset.addSeries(chartCandleR);

            /**
             * Creating the chart
             */
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    company.getSimbolo() + " - " + tradesToPredict + " dias", // title
                    "Date", // x-axis label
                    "Price Per Unit", // y-axis label
                    dataset, // data
                    true, // create legend?
                    true, // generate tooltips?
                    false // generate URLs?
            );
            XYPlot plot = (XYPlot) chart.getPlot();
            DateAxis axis = (DateAxis) plot.getDomainAxis();
            axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));

            displayChart(chart);

        } catch (IOException ex) {
            Logger.getLogger(TrainingNetworks.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static org.jfree.data.time.TimeSeries buildChartTimeSeries(int initial, TimeSeries tickSeries, Indicator<Decimal> indicator, String name) {
        org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
        for (int i = initial; i < tickSeries.getTickCount(); i++) {
            Tick tick = tickSeries.getTick(i);
            chartTimeSeries.add(new Day(tick.getEndTime().toDate()), indicator.getValue(i).toDouble());
        }
        return chartTimeSeries;
    }

    private static void displayChart(JFreeChart chart) {

        //http://www.jfree.org/jfreechart/api/javadoc/org/jfree/chart/plot/CombinedDomainCategoryPlot.html
        //http://www.java2s.com/Code/Java/Chart/JFreeChartCombinedCategoryPlotDemo1.htm
        // Chart panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        // Application frame
        ApplicationFrame frame = new ApplicationFrame("Testes");

        frame.setContentPane(panel);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }

    private static double getMaxPrice(TechnicalIndicators technicalIndicators) {
        double max = 0;

        for (int i = technicalIndicators.getTimeSeries().getBegin(); i <= technicalIndicators.getTimeSeries().getEnd(); i++) {
            if (technicalIndicators.getClosePrice().getTimeSeries().getTick(i).getMaxPrice().toDouble() > max) {
                max = technicalIndicators.getClosePrice().getTimeSeries().getTick(i).getMaxPrice().toDouble();
            }
        }

        return max;
    }

}
