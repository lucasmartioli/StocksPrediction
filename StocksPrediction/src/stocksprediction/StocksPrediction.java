/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stocksprediction;

import java.io.IOException;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

/**
 *
 * @author Lucas
 */

public class StocksPrediction {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        
        for ( HistoricalQuote arg : YahooFinance.get("ABEV3.SA",Interval.WEEKLY).getHistory()) {
            System.out.println(arg.toString());           
        }

        
    }
    
}
