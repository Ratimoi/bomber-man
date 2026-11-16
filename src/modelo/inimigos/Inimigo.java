package modelo.inimigos;

import excecoes.MovimentoInvalidoException;
import modelo.Direcao;
import modelo.Jogo;
import modelo.Posicao;
import modelo.Tabuleiro;
import modelo.personagens.Jogador;
import modelo.personagens.Personagem;

abstract public class Inimigo extends Personagem {
    protected Jogo jogo;
    protected int pontos;
    protected int velocidade;

    Inimigo(Posicao posicao, int pontos, int velocidade) {
        super(posicao);
        this.pontos = pontos;
        this.velocidade = velocidade;
    }

    public void agir(Tabuleiro tab, Jogador j) {
        Direcao dir = decidirMovimento(tab, j);

        try {
            this.mover(dir, tab);
        } catch (MovimentoInvalidoException ignored) {

        }
    }

    abstract public Direcao decidirMovimento(Tabuleiro tab, Jogador j);
}
