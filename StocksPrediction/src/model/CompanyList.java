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
public class CompanyList {
    //"EGIE3",??
    //RUMO3 Só 2015

//    private static final String[] static_listaEmpresas = {"ABEV3", "CIEL3", "BBSE3", "BRFS3", "BRKM5", "BRML3", "BVMF3", "CCRO3", "CMIG4", "CPFE3", "CSAN3", "CSNA3", "CTIP3",  "EMBR3", "ENBR3", "EQTL3", "ESTC3", "FIBR3", "GGBR4", "GOAU4", "HYPE3", "ITSA4", "ITUB4", "JBSS3", "KROT3", "LAME4", "LREN3", "MRVE3", "MULT3", "NATU3", "PCAR4", "PETR3", "PETR4", "QUAL3", "RADL3", "SBSP3", "SMLE3", "SUZB5", "UGPA3", "USIM5", "VALE3", "VALE5", "VIVT4", "WEGE3"};
    private static final String[] static_listaEmpresas = {"PETR4"};

//    public CompanyList() {
//        this.listaEmpresas = new String[]
//    }
    public static int getNumeroTotalDeEmpresas() {
        return static_listaEmpresas.length;
    }

    public static String[] getListaDeEmpresas() {
        String[] nova = new String[static_listaEmpresas.length];
        for (int i = 0; i < static_listaEmpresas.length; i++) {
            nova[i] = static_listaEmpresas[i] + ".SA";
        }

        return nova;
    }

    public static String getSimboloEmpresa(int indiceEmpresa) {
        return static_listaEmpresas[indiceEmpresa] + ".SA";
    }

}
