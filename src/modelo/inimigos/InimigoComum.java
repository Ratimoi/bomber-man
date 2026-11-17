package modelo.inimigos;

import java.util.Random;

import modelo.Direcao;
import modelo.Posicao;
import modelo.Tabuleiro;
import modelo.personagens.Jogador;

public class InimigoComum extends Inimigo {
    public InimigoComum(Posicao posicao) {
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
