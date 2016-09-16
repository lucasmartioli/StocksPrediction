/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treinamentoredes;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import model.Company;
import model.ListaEmpresas;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

/**
 *
 * @author Lucas
 */
public class TreinamentoRedes {

    private static Calendar dataInicialTreinamento;
    private static Calendar dataFinalTreinamento;
    private static List<Company> empresas;

    public TreinamentoRedes() throws IOException {
        
        ta-lib
        empresas = new Vector<>();

        dataInicialTreinamento = Calendar.getInstance();
        dataInicialTreinamento.set(14, 1, 1);

        dataFinalTreinamento = Calendar.getInstance();
        dataFinalTreinamento.set(15, 1, 1);

        this.carregaDadosHistoricosParaTreinamento();

    }

    private void carregaDadosHistoricosParaTreinamento() throws IOException {

        //yahoofinance.Utils.
        YahooFinance.logger.setLevel(Level.OFF);
        System.out.println(YahooFinance.CONNECTION_TIMEOUT);
        Map<String, Stock> consulta = YahooFinance.get(ListaEmpresas.getListaDeEmpresas(), dataInicialTreinamento, dataFinalTreinamento, Interval.DAILY);

        for (Map.Entry<String, Stock> entry : consulta.entrySet()) {
            String simbolo = entry.getKey();
            Stock value = entry.getValue();
            
            Company c = new Company(simbolo);
            
            c.setHistorico(value.getHistory());            
            empresas.add(c);
            
        }
        
        ta.
    }
}
