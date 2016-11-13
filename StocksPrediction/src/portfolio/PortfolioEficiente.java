/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public class PortfolioEficiente {

    public static void main(String[] args) {
        ArrayList<String> companies = new ArrayList<>();

        companies.add("ENBR3");
        companies.add("CPLE6");
        companies.add("CSNA3");
//        companies.add("CEGR3"); NaN Dados?
        companies.add("ABEV3");
        companies.add("ITUB4");
        companies.add("PETR3");
        companies.add("PETR4");
        companies.add("BBDC4");
        companies.add("CIEL3");
        companies.add("VALE3");
        companies.add("VIVT4");
        companies.add("SANB3");
        companies.add("BBSE3");
        companies.add("BRFS3");
        companies.add("BBAS3");
        companies.add("JBSS3");
        companies.add("UGPA3");
        companies.add("FIBR3");
//        companies.add("KLBN3"); Dados insuficientes
        companies.add("WEGE3");
        companies.add("EMBR3");
        companies.add("CCRO3");
        companies.add("CCRO3");
//        companies.add("EGIE3"); Não tem dados no Yahoo
        companies.add("SUZB5");
        companies.add("LAME4");
        companies.add("BVMF3");
        companies.add("BRKM5");
        companies.add("TIMP3");
        companies.add("KROT3");
        companies.add("CPFE3");
        companies.add("HYPE3");
        companies.add("SBSP3");
        companies.add("RADL3");
        companies.add("PCAR4");
        companies.add("LREN3");
        companies.add("RLOG3");
        companies.add("NATU3");
        companies.add("CTIP3");
        companies.add("PSSA3");
        companies.add("ELET6");
        companies.add("CMIG4");
        companies.add("GGBR4");
        companies.add("MULT3");
        companies.add("EQTL3");
        companies.add("SMLE3");
        companies.add("SMTO3");
        companies.add("SMTO3");
        companies.add("RENT3");
        companies.add("TOTS3");
        companies.add("GRND3");
        companies.add("ODPV3");
        companies.add("CGAS5");
        companies.add("ESTC3");
        companies.add("SMLE3");
        companies.add("CESP6");
        companies.add("MPLU3");
//        companies.add("CPRE3"); Problemas na rede RSI
//        companies.add("TAEE3"); Sem indicadores

//        companies.add("KROT3");
//        companies.add("CSAN3");
//        companies.add("ESTC3");
//        companies.add("ENBR3");
//        companies.add("CMIG3");
//        companies.add("BRAP3");
////        companies.add("VALE5");
//        companies.add("GGBR4");
//        companies.add("CSNA3");
//        companies.add("BRFS3");
//        companies.add("BALM3");
//        companies.add("BALM4");
//        companies.add("CCRO3");
//        companies.add("CEPE3");
//        companies.add("CEPE5");
//        companies.add("CEPE6"); 
//        companies.add("MSPA3"); 
//        companies.add("MSPA4"); 
        Calendar di = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
        di.set(14, 6, 1, 12, 0);
        Calendar df = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
        df.set(16, 10, 1, 12, 0);
        TimeSeriesCollection dataset = new TimeSeriesCollection();

        Period period = new Period(di, df, 0.90, 0.10);

        MakePortfolio makePortfolio = new MakePortfolio(companies, period, 5);

        try {
            makePortfolio.make();
        } catch (IOException ex) {
            Logger.getLogger(PortfolioEficiente.class.getName()).log(Level.SEVERE, null, ex);
        }

        org.jfree.data.time.TimeSeries chartProfit = new org.jfree.data.time.TimeSeries("Profit");
        org.jfree.data.time.TimeSeries chartID = new org.jfree.data.time.TimeSeries("ID");
        org.jfree.data.time.TimeSeries chartEstimatedProfit = new org.jfree.data.time.TimeSeries("Estimated Profit");
        org.jfree.data.time.TimeSeries chartInvestiment = new org.jfree.data.time.TimeSeries("Investiment");
        org.jfree.data.time.TimeSeries chartVariance = new org.jfree.data.time.TimeSeries("Variance");

        double investimento = 0d;
        double investimentoLiq = 0d;
        double lucro = 0d;
        double lucroLiq = 0d;
        for (Portfolio p : makePortfolio.getPortfolios()) {
            if (p.getEstimatedProfit() > 0) {
                lucroLiq += p.getProfit();
                investimento += p.getInvestment();
            }

            lucro += p.getProfit();
            investimentoLiq += p.getInvestment();
            
//            System.out.println(p.toString());
            System.out.println(p.toStringResum());
            chartProfit.addOrUpdate(new Day(p.getDate().getTime()), p.getProfit());
            chartID.addOrUpdate(new Day(p.getDate().getTime()), p.getId());
            chartEstimatedProfit.addOrUpdate(new Day(p.getDate().getTime()), p.getEstimatedProfit());
            chartInvestiment.addOrUpdate(new Day(p.getDate().getTime()), p.getInvestment());
            chartVariance.addOrUpdate(new Day(p.getDate().getTime()), p.getVariance());
        }
        System.out.println("Lucro: " + lucro + "(ou prejuizo) e lucro liquido: " + lucroLiq);
        System.out.println("Lucro %: " + (lucro/investimento) * 100d + "(ou prejuizo) e lucro liquido %: " + (lucroLiq/investimentoLiq) * 100d);

        dataset.addSeries(chartProfit);
        dataset.addSeries(chartEstimatedProfit);
//        dataset.addSeries(chartInvestiment);
        dataset.addSeries(chartVariance);
//        dataset.addSeries(chartID);

        /**
         * Creating the chart
         */
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Portfolios", // title
                "Date", // x-axis label
                "R$", // y-axis label
                dataset, // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));

        displayChart(chart);

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
