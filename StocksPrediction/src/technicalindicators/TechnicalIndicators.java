/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package technicalindicators;

import java.util.ArrayList;
import java.util.List;
import yahoofinance.histquotes.HistoricalQuote;
import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.simple.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.trackers.EMAIndicator;
import eu.verdelhan.ta4j.indicators.trackers.MACDIndicator;
import eu.verdelhan.ta4j.indicators.trackers.RSIIndicator;
import eu.verdelhan.ta4j.indicators.trackers.SMAIndicator;
import eu.verdelhan.ta4j.indicators.volume.OnBalanceVolumeIndicator;
import org.joda.time.DateTime;

/**
 *
 * @author Lucas
 */
public class TechnicalIndicators {

    static final int maxDaysIndicators = 26;

    public static int getMaxDaysIndicators() {
        return maxDaysIndicators;
    }

    private TimeSeries timeSeries;
    private final ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
    private final SMAIndicator sma4days = new SMAIndicator(closePrice, 4);
    private final SMAIndicator sma9days = new SMAIndicator(closePrice, 9);
    private final SMAIndicator sma18days = new SMAIndicator(closePrice, 18);
    private final MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
    private final RSIIndicator rsi14days = new RSIIndicator(closePrice, 14);
    private final OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(timeSeries);

    public TechnicalIndicators(List<HistoricalQuote> historic) {

        ArrayList<Tick> tickList = new ArrayList<>();
        for (int j = historic.size() - 1; j > 0; j--) {
            HistoricalQuote historicalQuote = historic.get(j);

            DateTime date = new DateTime(historicalQuote.getDate().getTimeInMillis());
            Tick dado = new Tick(date, historicalQuote.getOpen().doubleValue(), historicalQuote.getHigh().doubleValue(), historicalQuote.getLow().doubleValue(), historicalQuote.getClose().doubleValue(), historicalQuote.getVolume());

            tickList.add(dado);
        }

        this.timeSeries = new TimeSeries(tickList);

    }

    public TimeSeries getTimeSeries() {
        return timeSeries;
    }

    public ClosePriceIndicator getClosePrice() {
        return closePrice;
    }

    public SMAIndicator getSma4days() {
        return sma4days;
    }

    public SMAIndicator getSma9days() {
        return sma9days;
    }

    public SMAIndicator getSma18days() {
        return sma18days;
    }

    public MACDIndicator getMacd() {
        return macd;
    }

    public RSIIndicator getRsi14days() {
        return rsi14days;
    }

    public OnBalanceVolumeIndicator getObv() {
        return obv;
    }

}
