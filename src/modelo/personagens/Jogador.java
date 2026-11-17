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
        super(posicao);
        bombasAtivas = 0;
        maxBombas = 1;
        this.nome = nome;
        pontuacao = 0;
        raioExplosao = 1;
        vidas = 3;
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
