package modelo;

public enum Direcao {
    NORTE(0, -1),
    SUL(0, 1),
    LESTE(1, 0),
    OESTE(-1, 0);

    private final int dColuna;
    private final int dLinha;

    Direcao(int dColuna, int dLinha) {
        this.dColuna = dColuna;
        this.dLinha = dLinha;
    }

    public int getdColuna() {
        return dColuna;
    }

    public int getdLinha() {
        return dLinha;
    }
}
