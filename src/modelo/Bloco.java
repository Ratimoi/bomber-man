package modelo;

abstract public class Bloco extends ElementoMapa {
    protected boolean transponivel;

    Bloco(Posicao posicao, boolean transponivel) {
        super(posicao);
        this.transponivel = transponivel;
    }

    public boolean isTransponivel() {
        return false;
    }
}
