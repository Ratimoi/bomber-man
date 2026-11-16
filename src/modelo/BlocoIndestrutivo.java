package modelo;

public class BlocoIndestrutivo extends Bloco {
    BlocoIndestrutivo(Posicao posicao, boolean transponivel) {
        super(posicao, transponivel);
    }

    public char getSimbolo() {
        return '█';
    }
}
