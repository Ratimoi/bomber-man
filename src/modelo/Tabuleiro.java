package modelo;

public class Tabuleiro {
    private Celula[][] celulas;
    private int tamanho;

    public Tabuleiro(int tamanho) {
        this.celulas = new Celula[tamanho][tamanho];
        this.tamanho = tamanho;

        for (int linha = 0; linha < tamanho; linha++) {
            for (int coluna = 0; coluna < tamanho; coluna++) {
                Posicao p = new Posicao(coluna, linha);
                this.celulas[linha][coluna] = new Celula(null, null, null, p);
            }
        }
    }

    public Celula getCelula(Posicao p) {
        if (posicaoValida(p)) {
            return celulas[p.getLinha()][p.getColuna()];
        }
        return null;
    }

    public int getTamanho() {
        return tamanho;
    }

    public void imprimir() {
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                System.out.print(celulas[i][j].getSimbolo());
            }
            System.out.println();
        }
    }

    public boolean isLivre(Posicao p) {
        if (getCelula(p) != null) {
            return getCelula(p).isTransponivel();
        }
        return false;
    }

    public boolean posicaoValida(Posicao p) {
        return p.getColuna() >= 0 && p.getColuna() < tamanho && p.getLinha() >= 0 && p.getLinha() < tamanho;
    }
}
