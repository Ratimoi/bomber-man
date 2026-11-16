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

    public List<Posicao> getCelulasAtingidas() {
        return celulasAtingidas;
    }

    public void propagar(Tabuleiro tab) {
        for (Direcao dir : Direcao.values()) {
            for (int passo = 1; passo <= raio; passo++) {
                int linha = origem.getLinha() + dir.getdLinha() * passo;
                int coluna = origem.getColuna() + dir.getdColuna() * passo;

                Posicao posicaoAtual = new Posicao(coluna, linha);
                Celula celula = tab.getCelula(posicaoAtual);

                if (celula == null) {
                    break;
                }

                Bloco bloco = celula.getBloco();

                if (bloco instanceof BlocoIndestrutivo) {
                    break;
                }

                if (bloco instanceof BombaArmadilha) {
                    BombaArmadilha armadilha = (BombaArmadilha) bloco;
                    Explosao explosaoEmCadeia = armadilha.getBombaOriginal().explodir(tab);

                    celula.removerBloco();
                    celulasAtingidas.add(posicaoAtual);
                    celulasAtingidas.addAll(explosaoEmCadeia.getCelulasAtingidas());
                    break;
                }

                if (bloco instanceof BlocoDestrutivo) {
                    celula.removerBloco();
                    celulasAtingidas.add(posicaoAtual);
                    break;
                } else if (bloco == null) {
                    celulasAtingidas.add(posicaoAtual);
                }
            }
        }
    }
}
