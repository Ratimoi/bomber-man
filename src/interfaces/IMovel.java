package interfaces;

import excecoes.MovimentoInvalidoException;
import modelo.Direcao;
import modelo.Tabuleiro;

public interface IMovel {
    public void mover(Direcao dir, Tabuleiro tab) throws MovimentoInvalidoException;
}
