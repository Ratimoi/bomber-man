package modelo.inimigos;

import java.util.Random;

import modelo.Celula;
import modelo.Direcao;
import modelo.Posicao;
import modelo.Tabuleiro;
import modelo.bombas.Bomba;
import modelo.bombas.BombaArmadilha;
import modelo.personagens.Jogador;

public class InimigoSabotador extends Inimigo {
    public InimigoSabotador(Posicao posicao) {
        super(posicao, 15, 1);
    }

    @Override
    public void agir(Tabuleiro tab, Jogador j) {
        Bomba bombaEncontrada = procurarBomba(tab);

        boolean sobreJogador = bombaEncontrada != null
                && bombaEncontrada.getPosicao().getLinha() == j.getPosicao().getLinha()
                && bombaEncontrada.getPosicao().getColuna() == j.getPosicao().getColuna();

        if (bombaEncontrada != null && !sobreJogador) {
            BombaArmadilha armadilha = desarmar(bombaEncontrada);

            if (armadilha != null) {
                Celula celula = tab.getCelula(bombaEncontrada.getPosicao());
                celula.removerBomba();
                celula.setBloco(armadilha);
                return;
            }
        }

        super.agir(tab, j);
    }

    @Override
    public Direcao decidirMovimento(Tabuleiro tab, Jogador j) {
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

    public BombaArmadilha desarmar(Bomba b) {
        if (!b.naMetadeInicial()) {
            return null;
        }

        char disfarce = (new Random().nextInt(1, 3) == 1) ? '█' : '#';

        BombaArmadilha bombaArmadilha = new BombaArmadilha(b.getPosicao(), b, disfarce);

        return bombaArmadilha;
    }

    @Override
    public char getSimbolo() {
        return 'S';
    }

    public Bomba procurarBomba(Tabuleiro tab) {
        for (Direcao dir : Direcao.values()) {
            for (int passo = 1; passo <= 2; passo++) {
                int linha = this.posicao.getLinha() + dir.getdLinha() * passo;
                int coluna = this.posicao.getColuna() + dir.getdColuna() * passo;

                Posicao posicaoAtual = new Posicao(coluna, linha);
                Celula celula = tab.getCelula(posicaoAtual);

                if (celula == null) {
                    break;
                }

                if (celula.getBloco() != null) {
                    break;
                }

                Bomba bomba = celula.getBomba();

                if (bomba != null) {
                    return bomba;
                }
            }
        }

        return null;
    }
}
