/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import java.util.Calendar;

/**
 *
 * @author Lucas
 */
public class Period {
    
    private final Calendar dateInitial;
    private final Calendar dateFinal;
    private final double percentageToTrainning;
    private final double percentageToTest;

    public Period(Calendar dateInitial, Calendar dateFinal, double percentageToTrainning, double percentageToTest) {
        this.dateInitial = dateInitial;
        this.dateFinal = dateFinal;
        this.percentageToTrainning = percentageToTrainning;
        this.percentageToTest = percentageToTest;
    }

    public Calendar getDateInitial() {
        return dateInitial;
    }

    public Calendar getDateFinal() {
        return dateFinal;
    }

    public double getPercentageToTrainning() {
        return percentageToTrainning;
    }

    public double getPercentageToTest() {
        return percentageToTest;
    }
    
}
