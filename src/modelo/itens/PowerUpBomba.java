package modelo.itens;

import modelo.Posicao;
import modelo.personagens.Jogador;

public class PowerUpBomba extends PowerUp {
    public PowerUpBomba(Posicao posicao) {
        super(posicao);
    }

    @Override
    public void aplicar(Jogador j) {
        j.aumentarBomba();
    }

    @Override
    public char getSimbolo() {
        return 'U';
    }
}
