/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treinamentoredes;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import model.Company;
import model.ListaEmpresas;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

/**
 *
 * @author Lucas
 */
public class TreinamentoRedes {

    public TreinamentoRedes() throws IOException {
        Company[] empresas = new Company[ListaEmpresas.getNumeroTotalDeEmpresas()];

        for (int i = 0; i < empresas.length; i++) {
            empresas[i] = new Company(ListaEmpresas.getSimboloEmpresa(i));
            

            Calendar dataInicialTreinamento = Calendar.getInstance();
            dataInicialTreinamento.set(1, 1, 13);
            
            Calendar dataFinalTreinamento = Calendar.getInstance();
            dataFinalTreinamento.set(1, 1, 14);
            
            List<HistoricalQuote> history = YahooFinance.get(empresas[i].getSimbolo(), dataInicialTreinamento, dataFinalTreinamento, Interval.WEEKLY).getHistory();
            
            history.stream().forEach((arg) -> {
                System.out.println(arg.toString());
            });

        }
    }
}
