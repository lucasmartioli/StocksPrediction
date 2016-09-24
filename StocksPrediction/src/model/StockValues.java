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
public class StockValues {

    private Calendar date;

    private BigDecimal open;
    private BigDecimal low;
    private BigDecimal high;
    private BigDecimal close;

    //private BigDecimal adjClose;
    private Long volume;

    public StockValues(HistoricalQuote h) {
        this.date = h.getDate();
        this.open = h.getOpen();
        this.low = h.getLow();
        this.volume = h.getVolume();
        this.high = h.getHigh();

    }

    public StockValues(Stock consulta) {
        this.date = Calendar.getInstance();
        this.open = consulta.getQuote().getOpen();
        this.volume = consulta.getQuote().getVolume();
        this.low = consulta.getQuote().getDayLow();
        this.high = consulta.getQuote().getDayHigh();
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
}
