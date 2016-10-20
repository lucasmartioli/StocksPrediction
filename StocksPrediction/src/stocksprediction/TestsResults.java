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

/**
 *
 * @author Lucas
 */
public class TestsResults {

    public static void main(String[] args) {
        int tradesToPredict = 5;

        try {
            Calendar di = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            di.set(15, 6, 1, 12, 0);
            Calendar df = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
            df.set(19, 9, 15, 12, 0);

            Company vivo = LoadingCompany.loading("ITUB4", di, df);

//            TechnicalIndicators technicalIndicators = vivo.getTechnicalIndicators();
            TimeSeries timeSeries = vivo.getTechnicalIndicators().getTimeSeries();
            int indicadorInicial = timeSeries.getBegin();
            int indicadorFinal = timeSeries.getEnd();

            indicadorInicial += 30; // desconsiderando valores iniciais onde os indicadores não fazem tanto sentido.

            TimeSeriesCollection dataset = new TimeSeriesCollection();
            //for (double percent = 0.1; percent < 1d; percent += 0.1d) {

            int inicioTreinamento = indicadorInicial;
            int finalTreinamento = Math.round(((float) (indicadorFinal * 0.95d)));
            int inicioTestes = finalTreinamento + 1;
            int finalTestes = indicadorFinal;

            TrainingNeuralNetwork.toTrain(vivo, inicioTreinamento, finalTreinamento);

            org.jfree.data.time.TimeSeries chartResultados = new org.jfree.data.time.TimeSeries("Calculado");
            org.jfree.data.time.TimeSeries chartDiferencas = new org.jfree.data.time.TimeSeries("Diferenca");
            for (int i = inicioTestes; i <= finalTestes; i++) {

                double saida = TrainingNeuralNetwork.toPredict(vivo, i);
                System.out.println("Saida do algoritmo: " + saida * 100);
                System.out.println("");
                System.out.println("Dia sendo analisado: " + timeSeries.getTick(i));
                if (i + tradesToPredict <= finalTestes) {
                    System.out.println("Diferença: " + ((saida * 100) - timeSeries.getTick(i + tradesToPredict).getClosePrice().toDouble()) + " Dia no futuro: " + timeSeries.getTick(i + tradesToPredict));
                    Tick tick = timeSeries.getTick(i + tradesToPredict);
                    chartResultados.add(new Day(tick.getEndTime().toDate()), (saida * 100d));
                    chartDiferencas.add(new Day(tick.getEndTime().toDate()), 10d * Math.abs((saida * 100d) - timeSeries.getTick(i + tradesToPredict).getClosePrice().toDouble()));
                } else {
                    System.out.println("Não tem como saber o futuro ainda!!");
                }
                System.out.println("");
            }

            dataset.addSeries(buildChartTimeSeries(inicioTestes, timeSeries, vivo.getTechnicalIndicators().getClosePrice(), vivo.getSimbolo() + " - Close Price"));
            dataset.addSeries(chartResultados);
            dataset.addSeries(chartDiferencas);
            //}

//        dataset.addSeries(buildChartTimeSeries(timeSeries, sma, "Média móvel Simples - 4 dias"));
//        dataset.addSeries(buildChartTimeSeries(timeSeries, sma9, "Média móvel Simples - 9 dias"));
//        dataset.addSeries(buildChartTimeSeries(serie, sma18, "Média móvel Simples - 18 dias"));
            //dataset.addSeries(buildChartTimeSeries(serie, macd, "MACD - 12 e 26 dias"));
            //dataset.addSeries(buildChartTimeSeries(serie, rsi, "RSI - 14 dias"));
            //dataset.addSeries(buildChartTimeSeries(serie, obv, "OBV"));
            /**
             * Creating the chart
             */
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    vivo.getSimbolo(), // title
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

}
