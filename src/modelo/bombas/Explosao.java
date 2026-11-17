package modelo.bombas;

import java.util.ArrayList;
import java.util.List;

import modelo.Celula;
import modelo.Direcao;
import modelo.Posicao;
import modelo.Tabuleiro;
import modelo.blocos.Bloco;
import modelo.blocos.BlocoDestrutivo;
import modelo.blocos.BlocoIndestrutivo;
import modelo.itens.Item;

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
                    celula.removerBloco();

                    Explosao explosaoEmCadeia = armadilha.getBombaOriginal().explodir(tab);

                    celulasAtingidas.add(posicaoAtual);
                    celulasAtingidas.addAll(explosaoEmCadeia.getCelulasAtingidas());
                    break;
                }

                if (bloco instanceof BlocoDestrutivo) {
                    BlocoDestrutivo destrutivo = (BlocoDestrutivo) bloco;
                    Item itemOculto = destrutivo.getItemOculto();

                    celula.removerBloco();

                    if (itemOculto != null) {
                        itemOculto.revelar();
                        celula.setItem(itemOculto);
                    }

                    celulasAtingidas.add(posicaoAtual);
                    break;
                } else if (bloco == null) {
                    celulasAtingidas.add(posicaoAtual);
                }
            }
        }
    }
}
