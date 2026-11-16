package modelo.itens;

import modelo.ElementoMapa;
import modelo.Jogo;
import modelo.Posicao;
import modelo.personagens.Jogador;

abstract public class Item extends ElementoMapa {
    protected boolean visivel;

    Item(Posicao posicao) {
        super(posicao);
    }

    abstract public void coletar(Jogador j, Jogo jogo);

    public void revelar() {
        visivel = true;
    }
}
