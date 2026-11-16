package modelo;

import excecoes.MovimentoInvalidoException;

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
