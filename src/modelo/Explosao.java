package modelo;

import java.util.ArrayList;
import java.util.List;

public class Explosao {
    private List<Posicao> celulasAtingidas;
    private Posicao origem;
    private int raio;

    Explosao(Posicao origem, int raio) {
        this.origem = origem;
        this.raio = raio;
        this.celulasAtingidas = new ArrayList<>();

        celulasAtingidas.add(origem);
    }

    public boolean atinge(Posicao p) {
        for (Posicao pos : celulasAtingidas) {
            if (pos.getLinha() == p.getLinha() && pos.getColuna() == p.getColuna()) {
                return true;
            }
        }
        return false;
    }

    public void propagar(Tabuleiro tab) {
        for (Direcao dir : Direcao.values()) {
            for (int passo = 1; passo <= raio; passo++) {
                int linha = origem.getLinha() + dir.getdLinha() * passo;
                int coluna = origem.getColuna() + dir.getdColuna() * passo;

                Posicao posicaoAtual = new Posicao(coluna, linha);

                if (tab.getCelula(posicaoAtual) == null) {
                    break;
                }

                if (tab.getCelula(posicaoAtual).getBloco() instanceof BlocoIndestrutivo) {
                    break;
                }

                if (tab.getCelula(posicaoAtual).getBloco() instanceof BlocoDestrutivo) {
                    tab.getCelula(posicaoAtual).removerBloco();
                    celulasAtingidas.add(posicaoAtual);
                    break;
                } else if (tab.getCelula(posicaoAtual).getBloco() == null) {
                    celulasAtingidas.add(posicaoAtual);
                }
            }
        }
    }
}
