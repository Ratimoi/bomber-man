package modelo;

public class BlocoDestrutivo extends Bloco {
    private Item itemOculto;

    BlocoDestrutivo(Posicao posicao, boolean transponivel, Item itemOculto) {
        super(posicao, transponivel);
        this.itemOculto = itemOculto;
    }

    public char getSimbolo() {
        return '#';
    }

    public boolean isDestruido() {
        return true;
    }
}
