package modelo;

public class Tabuleiro {
    private final Celula[][] celulas;


    public Tabuleiro(int tamanho) {
        this.celulas = new Celula[tamanho][tamanho];

        for(int i =0;i<tamanho;i++){
            for(int j = 0;j<tamanho;j++){
                this.celulas[i][j] = new Celula();
            }
        }

    }
}
