package modelo;

public class Posicao {
    private int coluna;
    private int linha;

    Posicao(int coluna, int linha) {
        this.coluna = coluna;
        this.linha = linha;
    }

    public int getColuna() {
        return this.coluna;
    }

    public int getLinha() {
        return this.linha;
    }
}
