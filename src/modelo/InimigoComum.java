package modelo;

import java.util.Random;

public class InimigoComum extends Inimigo {
    InimigoComum(Posicao posicao) {
        super(posicao, 5, 1);
    }

    @Override
    public Direcao decidirMovimento(Tabuleiro tab, Jogador j) {
        Random random = new Random();
        int numero = random.nextInt(1, 5);

        return switch (numero) {
            case 1 -> Direcao.NORTE;
            case 2 -> Direcao.SUL;
            case 3 -> Direcao.OESTE;
            case 4 -> Direcao.LESTE;
            default -> null;
        };
    }

    @Override
    public char getSimbolo() {
        return 'C';
    }
}
