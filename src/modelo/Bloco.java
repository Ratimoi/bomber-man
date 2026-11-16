package modelo;

abstract public class Bloco extends ElementoMapa {
    protected boolean transponivel;

    Bloco(Posicao posicao,boolean transponivel) {

        this.transponivel = transponivel;
        super(posicao);
    }

    public boolean isTransponivel() {
        return false;
    }
}
