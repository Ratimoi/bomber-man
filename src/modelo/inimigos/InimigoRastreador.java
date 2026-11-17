package modelo.inimigos;

import java.util.Random;

import modelo.Direcao;
import modelo.Posicao;
import modelo.Tabuleiro;
import modelo.personagens.Jogador;

public class InimigoRastreador extends Inimigo {
    private boolean perseguindo;

    public InimigoRastreador(Posicao posicao) {
        super(posicao, 10, 2);
        perseguindo = false;
    }

    public boolean avistar(Tabuleiro tab, Jogador j) {
        for (Direcao dir : Direcao.values()) {
            for (int passo = 1; passo <= tab.getTamanho(); passo++) {
                int linha = this.posicao.getLinha() + dir.getdLinha() * passo;
                int coluna = this.posicao.getColuna() + dir.getdColuna() * passo;

                Posicao posicaoAtual = new Posicao(coluna, linha);

                if (tab.getCelula(posicaoAtual) == null) {
                    break;
                }

                if (tab.getCelula(posicaoAtual).getBloco() != null) {
                    break;
                }

                if (posicaoAtual.getColuna() == j.getPosicao().getColuna() && posicaoAtual.getLinha() == j.getPosicao().getLinha()) {
                    return this.perseguindo = true;
                }
            }
        }

        return false;
    }

    @Override
    public Direcao decidirMovimento(Tabuleiro tab, Jogador j) {
        if (perseguindo || avistar(tab, j)) {
            if (j.getPosicao().getLinha() < this.posicao.getLinha()) {
                return Direcao.NORTE;
            }

            if (j.getPosicao().getLinha() > this.posicao.getLinha()) {
                return Direcao.SUL;
            }

            if (j.getPosicao().getColuna() < this.posicao.getColuna()) {
                return Direcao.OESTE;
            }

            if (j.getPosicao().getColuna() > this.posicao.getColuna()) {
                return Direcao.LESTE;
            }
        }

        Random random = new Random();
        int numero = random.nextInt(1, 5);

        return switch (numero) {
            case 1 -> Direcao.NORTE;
            case 2 -> Direcao.SUL;
            case 3 -> Direcao.OESTE;
            case 4 -> Direcao.LESTE;
            default -> null;
        };
    }

    @Override
    public char getSimbolo() {
        return 'R';
    }
}
