/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.List;
import technicalindicators.TechnicalIndicators;
import yahoofinance.histquotes.HistoricalQuote;

/**
 *
 * @author Lucas
 */
public class Company {

    private final String simbolo;
    private TechnicalIndicators technicalIndicators;
    private StockValues valoresAutais;

    private List<HistoricalQuote> historico;

    public Company(String simbolo) {
        this.simbolo = simbolo;
    }

    public TechnicalIndicators getTechnicalIndicators() {
        return technicalIndicators;
    }

    public void calculateTechnicalIndicators() {
        this.technicalIndicators = new TechnicalIndicators(historico);
    }

    public List<HistoricalQuote> getHistorico() {
        return historico;
    }

    public void setHistorico(List<HistoricalQuote> historico) {
        this.calculateTechnicalIndicators();
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
