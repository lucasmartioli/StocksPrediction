/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loadingcompany;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import model.Company;
import model.StockValues;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

/**
 *
 * @author Lucas
 */
public class LoadingCompany {

    private static final String commonSuffix = ".SA";

    public static ArrayList<Company> loading(String[] listaEmpresas, Calendar dataInicial, Calendar dataFinal) throws IOException {

        ArrayList<Company> empresas = new ArrayList<>();
        YahooFinance.logger.setLevel(Level.OFF);

        Map<String, Stock> consulta = YahooFinance.get(listaEmpresas, dataInicial, dataFinal, Interval.DAILY);

        for (Map.Entry<String, Stock> entry : consulta.entrySet()) {
            String simbolo = entry.getKey();
            Stock value = entry.getValue();

            Company company = new Company(simbolo);

            company.setHistoricValues(value.getHistory());
            empresas.add(company);
        }

        return empresas;
    }

    public static Company loading(String symbol, Calendar dataInicial, Calendar dataFinal) throws IOException {

        YahooFinance.logger.setLevel(Level.OFF);
        Stock consulta = YahooFinance.get(symbol + commonSuffix, dataInicial, dataFinal, Interval.DAILY);

        Company company = new Company(symbol);
        company.setHistoricValues(consulta.getHistory());

        return company;
    }
    
     public static Company loadingCoin(String symbol, Calendar dataInicial, Calendar dataFinal) throws IOException {

        YahooFinance.logger.setLevel(Level.OFF);
        Stock consulta = YahooFinance.get(symbol, dataInicial, dataFinal, Interval.DAILY);

        Company company = new Company(symbol);
        company.setHistoricValues(consulta.getHistory());

        return company;
    }

    public static Company loading(String empresa) throws IOException {

        YahooFinance.logger.setLevel(Level.OFF);
        Stock consulta = YahooFinance.get(empresa + commonSuffix);

        StockValues sv = new StockValues(consulta);

        Company company = new Company(empresa);
        company.setCurrentValues(sv);

        return company;
    }

    public static Company loadingCoin(String empresa) throws IOException {

        YahooFinance.logger.setLevel(Level.OFF);
        Stock consulta = YahooFinance.get(empresa);

        StockValues sv = new StockValues(consulta);

        Company company = new Company(empresa);
        company.setCurrentValues(sv);

        return company;
    }
}
