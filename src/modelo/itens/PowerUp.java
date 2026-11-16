package modelo.itens;

import modelo.Jogo;
import modelo.Posicao;
import modelo.personagens.Jogador;

abstract public class PowerUp extends Item {
    PowerUp(Posicao posicao) {
        super(posicao);
    }

    abstract public void aplicar(Jogador j);

    @Override
    public void coletar(Jogador j, Jogo jogo) {
        aplicar(j);
    }
}
