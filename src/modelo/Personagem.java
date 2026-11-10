package modelo;

import excecoes.MovimentoInvalidoException;
import interfaces.IDestrutivel;
import interfaces.IMovel;

abstract public class Personagem extends ElementoMapa implements IMovel, IDestrutivel {
    protected boolean vivo;

    Personagem(Posicao posicao) {
        super(posicao);
        this.vivo = true;
    }

    @Override
    public boolean isDestruido() {
        return !vivo;
    }

    @Override
    public void mover(Direcao dir, Tabuleiro tab) throws MovimentoInvalidoException {

    }

    @Override
    public void receberDano(Tabuleiro tab) {

    }
}
