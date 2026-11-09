package modelo;

abstract public class Bloco extends ElementoMapa {
    protected boolean transponivel;

    Bloco(boolean transponivel) {
        this.transponivel = transponivel;
    }

    public boolean isTransponivel() {
        return false;
    }
}
