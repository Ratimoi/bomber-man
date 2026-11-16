package modelo.bombas;

import modelo.Posicao;
import modelo.blocos.Bloco;

public class BombaArmadilha extends Bloco {
    private Bomba bombaOriginal;
    private char simboloDisfarce;

    public BombaArmadilha(Posicao posicao, Bomba bombaOriginal, char simboloDisfarce) {
        super(posicao, false);
        this.bombaOriginal = bombaOriginal;
        this.simboloDisfarce = simboloDisfarce;
    }

    @Override
    public char getSimbolo() {
        return simboloDisfarce;
    }

    public Bomba getBombaOriginal() {
        return bombaOriginal;
    }
}