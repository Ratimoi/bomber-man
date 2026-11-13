package modelo;

abstract public class ElementoMapa {
    protected Posicao posicao;

    ElementoMapa(Posicao posicao) {

        this.posicao = posicao;
    }

    public Posicao getPosicao() {
        return this.posicao;
    }

    public abstract char getSimbolo();

    public void setPosicao(Posicao p) {
        this.posicao = posicao;
    }
}
