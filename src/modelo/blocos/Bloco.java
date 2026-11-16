package modelo.blocos;

import modelo.ElementoMapa;
import modelo.Posicao;

abstract public class Bloco extends ElementoMapa {
    protected boolean transponivel;

    protected Bloco(Posicao posicao, boolean transponivel) {
        super(posicao);
        this.transponivel = transponivel;
    }

    public boolean isTransponivel() {
        return transponivel;
    }
}
