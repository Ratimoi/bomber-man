package modelo.itens;

import modelo.Posicao;
import modelo.personagens.Jogador;

public class PowerUpRaio extends PowerUp {
    public PowerUpRaio(Posicao posicao) {
        super(posicao);
    }

    @Override
    public void aplicar(Jogador j) {
        j.aumentarRaio();
    }

    @Override
    public char getSimbolo() {
        return 'O';
    }
}
