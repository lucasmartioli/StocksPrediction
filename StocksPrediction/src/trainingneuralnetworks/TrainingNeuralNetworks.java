/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainingneuralnetworks;

import com.sun.media.jfxmedia.logging.Logger;
import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import model.Company;
import model.CompanyList;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;
import java.math.BigDecimal;
import java.util.Vector;
import yahoofinance.histquotes.HistoricalQuote;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.volume.OnBalanceVolumeIndicator;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import org.joda.time.DateTime;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author Lucas
 */
public class TrainingNeuralNetworks {

    private static Calendar dataInicialTreinamento;
    private static Calendar dataFinalTreinamento;
    private static List<Company> empresas;

    public TrainingNeuralNetworks() throws IOException {

//c.macd(0, 0, inReal, 0, 0, 0, outBegIdx, outNBElement, outMACD, outMACDSignal, outMACDHist)
//c.rsi(0, 0, inReal, 0, outBegIdx, outNBElement, outReal)
//c.obv(0, 0, inReal, inVolume, outBegIdx, outNBElement, outReal)
        empresas = new Vector<>();

        dataInicialTreinamento = Calendar.getInstance();
        dataInicialTreinamento.set(16, 2, 6);

        dataFinalTreinamento = Calendar.getInstance();        
        dataFinalTreinamento.set(16, 8, 20);

        this.carregaDadosHistoricosParaTreinamento();

    }

    private void carregaDadosHistoricosParaTreinamento() throws IOException {

        //yahoofinance.Utils.
        YahooFinance.logger.setLevel(Level.OFF);
        Stock consulta = YahooFinance.get("PETR4.SA", dataInicialTreinamento, dataFinalTreinamento, Interval.DAILY);
//        Map<String, Stock> consulta = YahooFinance.get("PETR4.SA", dataInicialTreinamento, dataFinalTreinamento, Interval.WEEKLY);

        //for (Map.Entry<String, Stock> entry : consulta.entrySet()) {
//            String simbolo = entry.getKey();
//            Stock value = entry.getValue();
        Company c = new Company("PETR4");

        //System.out.println(consulta.getHistory().t());
        c.setHistorico(consulta.getHistory());
        empresas.add(c);

        ArrayList<Tick> serieTemporal = new ArrayList<>();
        for (int j = c.getHistorico().size() - 1; j > 0; j--) {
            HistoricalQuote hq = c.getHistorico().get(j);

            DateTime d = new DateTime(hq.getDate().getTimeInMillis());            
//            Period periodo = new org.joda.time.Period
            Tick dado = new Tick(d, hq.getOpen().doubleValue(), hq.getHigh().doubleValue(), hq.getLow().doubleValue(), hq.getClose().doubleValue(), hq.getVolume());
            //dado.
            serieTemporal.add(dado);
            System.out.println(hq.toString());
        }

        TimeSeries serie = new TimeSeries(serieTemporal);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(serie);
        EMAIndicator ema = new EMAIndicator(closePrice, 4);
        SMAIndicator sma = new SMAIndicator(closePrice, 4);
        SMAIndicator sma9 = new SMAIndicator(closePrice, 9);
        SMAIndicator sma18 = new SMAIndicator(closePrice, 18);
        MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
        RSIIndicator rsi = new RSIIndicator(closePrice, 14);
        OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(serie);
        //sma.c;

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(buildChartTimeSeries(serie, closePrice, "Petrobras (PETR4) - Close Price"));
        dataset.addSeries(buildChartTimeSeries(serie, sma, "Média móvel Simples - 4 dias"));
        dataset.addSeries(buildChartTimeSeries(serie, sma9, "Média móvel Simples - 9 dias"));
        dataset.addSeries(buildChartTimeSeries(serie, sma18, "Média móvel Simples - 18 dias"));
        //dataset.addSeries(buildChartTimeSeries(serie, macd, "MACD - 12 e 26 dias"));
        //dataset.addSeries(buildChartTimeSeries(serie, rsi, "RSI - 14 dias"));
        //dataset.addSeries(buildChartTimeSeries(serie, obv, "OBV"));

        /**
         * Creating the chart
         */
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Petrobras (PETR4) - Close Price", // title
                "Date", // x-axis label
                "Price Per Unit", // y-axis label
                dataset, // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

        
        displayChart(chart);

        System.out.println(sma.getValue(serie.getTickCount() - 1));
        System.out.println(sma9.getValue(serie.getTickCount() - 1));
        System.out.println(sma18.getValue(serie.getTickCount() - 1));
        System.out.println(ema.getValue(serie.getTickCount() - 1));

//        }
    }

    private static org.jfree.data.time.TimeSeries buildChartTimeSeries(TimeSeries tickSeries, Indicator<Decimal> indicator, String name) {
        org.jfree.data.time.TimeSeries chartTimeSeries = new org.jfree.data.time.TimeSeries(name);
        for (int i = 0; i < tickSeries.getTickCount(); i++) {
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
        ApplicationFrame frame = new ApplicationFrame("Exemplo de Indicadores - PETR4");
        
        frame.setContentPane(panel);
        frame.pack();
        RefineryUtilities.centerFrameOnScreen(frame);
        //frame.setVisible(true);
    }
}
