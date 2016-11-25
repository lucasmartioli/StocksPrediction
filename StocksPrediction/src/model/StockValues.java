/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.math.BigDecimal;
import java.util.Calendar;
import yahoofinance.Stock;
import yahoofinance.histquotes.HistoricalQuote;

/**
 *
 * @author Lucas
 */
public class StockValues implements Comparable<StockValues>{

    private Calendar date;

    private BigDecimal open;
    private BigDecimal low;
    private BigDecimal high;
    private BigDecimal close;
    private BigDecimal predictedClose;
    private BigDecimal beforeClose;
    private BigDecimal beforeRealClose;
    private Double increase;

    //private BigDecimal adjClose;
    private Long volume;

    public StockValues(HistoricalQuote h) {
        this.date = h.getDate();
        this.open = h.getOpen();
        this.low = h.getLow();
        this.volume = h.getVolume();
        this.high = h.getHigh();

    }

    public StockValues(Calendar date, double close, double predictedClose, double beforeClose, double beforeRealClose) {
        this.date = date;
        this.close = new BigDecimal(close);
        this.predictedClose = new BigDecimal(predictedClose);
        this.beforeClose = new BigDecimal(beforeClose);
        this.beforeRealClose = new BigDecimal(beforeRealClose);
    }

    public StockValues(Stock consulta) {
        this.date = Calendar.getInstance();
        this.open = consulta.getQuote().getOpen();
        this.volume = consulta.getQuote().getVolume();
        this.low = consulta.getQuote().getDayLow();
        this.high = consulta.getQuote().getDayHigh();
    }

    public BigDecimal getBeforeRealClose() {
        return beforeRealClose;
    }
    

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public Long getVolume() {
        return volume;
    }

    public void setVolume(Long volume) {
        this.volume = volume;
    }

    public Double getIncrease() {
        return increase;
    }

    public void setIncrease(Double increase) {
        this.increase = increase;
    }

    public BigDecimal getPredictedClose() {
        return predictedClose;
    }

    public BigDecimal getBeforeClose() {
        return beforeClose;
    }

    @Override
    public String toString() {
        return "StockValues{" + "date=" + date.getTime() + ", open=" + open + ", low=" + low + ", high=" + high + ", close=" + close + ", predictedClose=" + predictedClose + ", beforeClose=" + beforeClose + ", increase=" + increase + '}';
    }

    @Override
    public int compareTo(StockValues t) {
        return date.compareTo(t.getDate());
        
    }

}
