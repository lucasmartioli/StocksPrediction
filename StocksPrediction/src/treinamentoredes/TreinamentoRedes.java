/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package treinamentoredes;

import java.util.Calendar;
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

    public TreinamentoRedes() {
        Company[] empresas = new Company[ListaEmpresas.getNumeroTotalDeEmpresas()];

        for (int i = 0; i < empresas.length; i++) {
            empresas[i] = new Company(ListaEmpresas.getSimboloEmpresa(i));
            

//            for (HistoricalQuote arg : YahooFinance.get(empresas[i].getSimbolo(), Calendar.getInstance().Interval.DAILY).getHistory()) {
//                System.out.println(arg.toString());
//            }

        }
    }
}
