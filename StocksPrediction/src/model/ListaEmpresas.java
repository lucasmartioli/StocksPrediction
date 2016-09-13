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
public class ListaEmpresas {

    private final String[] listaEmpresas;

    public ListaEmpresas() {
        this.listaEmpresas = new String[]{"ABEV3", "BBAS3", "BBDC3", "BBDC4", "BBSE3", "BRFS3", "BRKM5", "BRML3", "BVMF3", "CCRO3", "CIEL3", "CMIG4", "CPFE3", "CSAN3", "CSNA3", "CTIP3", "EGIE3", "EMBR3", "ENBR3", "EQTL3", "ESTC3", "FIBR3", "GGBR4", "GOAU4", "HYPE3", "ITSA4", "ITUB4", "JBSS3", "KLBN1", "KROT3", "LAME4", "LREN3", "MRVE3", "MULT3", "NATU3", "PCAR4", "PETR3", "PETR4", "QUAL3", "RADL3", "RUMO3", "SBSP3", "SMLE3", "SUZB5", "UGPA3", "USIM5", "VALE3", "VALE5", "VIVT4", "WEGE3"};
    }
    
    public int getNumeroTotalDeEmpresas() {
        return listaEmpresas.length;
    }
    
    public String getSimboloEmpresa( int indiceEmpresa ) {
        return listaEmpresas[ indiceEmpresa ] + ".SA";
    }

}
