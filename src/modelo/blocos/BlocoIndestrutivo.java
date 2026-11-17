package modelo.blocos;

import modelo.Posicao;

public class BlocoIndestrutivo extends Bloco {
    public BlocoIndestrutivo(Posicao posicao, boolean transponivel) {
        super(posicao, transponivel);
    }

    public char getSimbolo() {
        return '█';
    }
}
