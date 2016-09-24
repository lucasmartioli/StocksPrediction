/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

/**
 *
 * @author Lucas
 */
public class Company {

    private final String simbolo;
    private StockValues valoresAutais;

    private List<HistoricalQuote> historico;

    public Company(String simbolo) {
        this.simbolo = simbolo;
    }
    
    public List<HistoricalQuote> getHistorico() {
        return historico;
    }

    public void setHistorico(List<HistoricalQuote> historico) {
        this.historico = historico;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public StockValues getValoresAutais() {
        return valoresAutais;
    }

    public void setValoresAutais(StockValues valoresAutais) {
        this.valoresAutais = valoresAutais;
    }
}
