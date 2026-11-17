package modelo.personagens;

import excecoes.LimiteBombaException;
import modelo.Posicao;
import modelo.Tabuleiro;

public class Jogador extends Personagem {
    private int bombasAtivas;
    private int maxBombas;
    private String nome;
    private int pontuacao;
    private int raioExplosao;
    private int vidas;

    public Jogador(Posicao posicao, String nome) {
        this(posicao, nome, 3, 1, 0, 1, 0);
    }

    public Jogador(Posicao posicao, String nome, int vidas, int maxBombas, int bombasAtivas, int raioExplosao, int pontuacao) {
        super(posicao);
        this.nome = nome;
        this.vidas = vidas;
        this.maxBombas = maxBombas;
        this.bombasAtivas = bombasAtivas;
        this.raioExplosao = raioExplosao;
        this.pontuacao = pontuacao;
    }

    public void adicionarPontos(int pontos) {
        pontuacao += pontos;
    }

    public void aumentarVida() {
        vidas++;
    }

    public void aumentarRaio() {
        raioExplosao++;
    }

    public void aumentarBomba() {
        maxBombas++;
    }

    public char getSimbolo() {
        return 'P';
    }

    public int getBombasAtivas() {
        return bombasAtivas;
    }

    public int getMaxBombas() {
        return maxBombas;
    }

    public String getNome() {
        return nome;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public int getRaioExplosao() {
        return raioExplosao;
    }

    public int getVidas() {
        return vidas;
    }

    public void liberarBomba() {
        bombasAtivas--;
    }

    public void perdeVida() {
        vidas--;
    }

    public void zerarBombasAtivas() {
        bombasAtivas = 0;
    }

    public void plantarBomba() throws LimiteBombaException {
        if (bombasAtivas >= maxBombas) {
            throw new LimiteBombaException();
        }
        bombasAtivas++;
    }

    @Override
    public void receberDano(Tabuleiro tab) {
        perdeVida();
        if (vidas <= 0) {
            vivo = false;
        }
    }
}
