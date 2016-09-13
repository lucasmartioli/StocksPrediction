/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Lucas
 */
public class Company {

    private final String simbolo;
    private StockValues valoresHoje;
    private StockValues[] historico;
    
    public Company( String simbolo ) {
        this.simbolo = simbolo;
    }

    public StockValues getValoresHoje() {
        return valoresHoje;
    }

    public void setValoresHoje(StockValues valoresHoje) {
        this.valoresHoje = valoresHoje;
    }

    public StockValues[] getHistorico() {
        return historico;
    }

    public void setHistorico(StockValues[] historico) {
        this.historico = historico;
    }
    
    public String getSimbolo() {
        return simbolo;
    }
}
