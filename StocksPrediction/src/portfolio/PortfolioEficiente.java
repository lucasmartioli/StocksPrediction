/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portfolio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lucas
 */
public class PortfolioEficiente {

    public static void main(String[] args) {
        ArrayList<String> companies = new ArrayList<>();

        companies.add("ABEV3");
        companies.add("ITUB4");
        companies.add("PETR3");
        companies.add("PETR4");
        companies.add("BBDC4");
        companies.add("CIEL3");
        companies.add("VALE3");
        companies.add("VIVT4");
        companies.add("SANB3");
        companies.add("BBSE3");
        companies.add("BRFS3");
        companies.add("BBAS3");
        companies.add("JBSS3");
        companies.add("UGPA3");
        companies.add("FIBR3");
        companies.add("KLBN3");
        companies.add("WEGE3");
        companies.add("EMBR3");
        companies.add("CCRO3");
        companies.add("CCRO3");
        companies.add("EGIE3");
        companies.add("SUZB5");
        companies.add("LAME4");
        companies.add("BVMF3");
        companies.add("BRKM5");
        companies.add("TIMP3");
        companies.add("KROT3");
        companies.add("CPFE3");
        companies.add("HYPE3");
        companies.add("SBSP3");
        companies.add("RADL3");
        companies.add("PCAR4");
        companies.add("LREN3");
        companies.add("RLOG3");
        companies.add("NATU3");
        companies.add("CTIP3");
        companies.add("PSSA3");
        companies.add("ELET6");
        companies.add("CMIG4");
        companies.add("GGBR4");
        companies.add("MULT3");
        companies.add("EQTL3");
        companies.add("SMLE3");
        companies.add("MPLU3");
        companies.add("CPRE3");
        companies.add("TAEE3");
        companies.add("ENBR3");
        companies.add("CPLE6");
        companies.add("CSNA3");
        companies.add("CEGR3");

//        companies.add("KROT3");
//        companies.add("CSAN3");
//        companies.add("ESTC3");
//        companies.add("ENBR3");
//        companies.add("CMIG3");
//        companies.add("BRAP3");
////        companies.add("VALE5");
//        companies.add("GGBR4");
//        companies.add("CSNA3");
//        companies.add("BRFS3");
//        companies.add("BALM3");
//        companies.add("BALM4");
//        companies.add("CCRO3");
//        companies.add("CEPE3");
//        companies.add("CEPE5");
//        companies.add("CEPE6"); 
//        companies.add("MSPA3"); 
//        companies.add("MSPA4"); 
        Calendar di = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
        di.set(14, 6, 1, 12, 0);
        Calendar df = Calendar.getInstance(TimeZone.getTimeZone("America/Sao Paulo"));
        df.set(16, 10, 1, 12, 0);

        Period period = new Period(di, df, 0.90, 0.10);

        MakePortfolio makePortfolio = new MakePortfolio(companies, period, 5);

        try {
            makePortfolio.make();
        } catch (IOException ex) {
            Logger.getLogger(PortfolioEficiente.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (Portfolio p : makePortfolio.getPortfolios()) {
            System.out.println(p.toString());
        }

    }

}
