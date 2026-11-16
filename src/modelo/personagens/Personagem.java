package modelo.personagens;

import excecoes.MovimentoInvalidoException;
import interfaces.IDestrutivel;
import interfaces.IMovel;
import modelo.Direcao;
import modelo.ElementoMapa;
import modelo.Posicao;
import modelo.Tabuleiro;
import modelo.blocos.Bloco;
import modelo.bombas.BombaArmadilha;

abstract public class Personagem extends ElementoMapa implements IMovel, IDestrutivel {
    protected boolean vivo;

    protected Personagem(Posicao posicao) {
        super(posicao);
        this.vivo = true;
    }

    @Override
    public boolean isDestruido() {
        return !vivo;
    }

    @Override
    public void mover(Direcao dir, Tabuleiro tab) throws MovimentoInvalidoException {
        int linha = this.posicao.getLinha() + dir.getdLinha();
        int coluna = this.posicao.getColuna() + dir.getdColuna();

        Posicao posicao = new Posicao(coluna, linha);

        if (tab.posicaoValida(posicao) && tab.isLivre(posicao)) {
            this.setPosicao(posicao);
            return;
        }

        if (tab.posicaoValida(posicao)) {
            Bloco bloco = tab.getCelula(posicao).getBloco();

            if (bloco instanceof BombaArmadilha) {
                BombaArmadilha armadilha = (BombaArmadilha) bloco;
                armadilha.getBombaOriginal().explodir(tab);
                this.receberDano(tab);
            }
        }

        throw new MovimentoInvalidoException();
    }

    @Override
    public void receberDano(Tabuleiro tab) {
        vivo = false;
    }
}
