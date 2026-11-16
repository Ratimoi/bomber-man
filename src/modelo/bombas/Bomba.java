package modelo.bombas;

import modelo.ElementoMapa;
import modelo.Jogo;
import modelo.Posicao;
import modelo.Tabuleiro;
import modelo.personagens.Jogador;

public class Bomba extends ElementoMapa {
    private Jogador dono;
    private Jogo jogo;
    private int raio;
    private int tempoRestante;
    private int tempoTotal;

    Bomba(Posicao posicao, Jogador dono, Jogo jogo, int raio, int tempoRestante, int tempoTotal) {
        super(posicao);
        this.dono = dono;
        this.jogo = jogo;
        this.raio = raio;
        this.tempoRestante = tempoRestante;
        this.tempoTotal = tempoTotal;
    }

    public Explosao explodir(Tabuleiro tab) {
        Explosao explosao = new Explosao(this.posicao, raio);
        explosao.propagar(tab);
        return explosao;
    }

    public char getSimbolo(){
        return 'B';
    }

    public int getTempoRestante() {
        return tempoRestante;
    }

    public boolean naMetadeInicial() {
        return this.tempoRestante >= this.tempoTotal / 2;
    }
}
