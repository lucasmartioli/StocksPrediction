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

    private TimeSeries timeSeries;
    
    private ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);    
    private SMAIndicator sma4days = new SMAIndicator(closePrice, 4);
    private SMAIndicator sma9days = new SMAIndicator(closePrice, 9);
    private SMAIndicator sma18days = new SMAIndicator(closePrice, 18);
    private MACDIndicator macd = new MACDIndicator(closePrice, 12, 26);
    private RSIIndicator rsi14days = new RSIIndicator(closePrice, 14);
    private OnBalanceVolumeIndicator obv = new OnBalanceVolumeIndicator(timeSeries);

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

}
