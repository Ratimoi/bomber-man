package modelo;

public class Celula {
    private Bloco bloco;
    private Bomba bomba;
    private Item item;
    private Posicao posicao;

    Celula(Bloco bloco, Bomba bomba, Item item, Posicao posicao) {
        this.bloco = bloco;
        this.bomba = bomba;
        this.item = item;
        this.posicao = posicao;
    }

    public Bloco getBloco() {
        return bloco;
    }

    public Bomba getBomba() {
        return bomba;
    }

    public char getSimbolo() {
        if (bloco != null) {
            return bloco.getSimbolo();
        } else if (bomba != null) {
            return bomba.getSimbolo();
        } else if (item != null){
            return item.getSimbolo();
        }

        return ' ';
    }

    public boolean isTransponivel() {
        return bloco == null && bomba == null;
    }

    public void removerBloco() {
        bloco = null;
    }

    public void removerBomba() {
        bomba = null;
    }

    public void setBloco(Bloco bloco) {
        this.bloco = bloco;
    }
}
