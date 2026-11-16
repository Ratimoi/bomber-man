package modelo;

import excecoes.LimiteBombaException;

public class Jogador extends Personagem {
    private int bombasAtivas;
    private int maxBombas;
    private String nome;
    private int pontuacao;
    private int raioExplosao;
    private int vidas;

    Jogador(Posicao posicao, String nome) {
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

    public char getSimbolo() {
        return 'P';
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
}
