package modelo.blocos;

import modelo.Posicao;
import modelo.itens.Item;

public class BlocoDestrutivo extends Bloco {
    private Item itemOculto;

    BlocoDestrutivo(Posicao posicao, boolean transponivel, Item itemOculto) {
        super(posicao, transponivel);
        this.itemOculto = itemOculto;
    }

    public char getSimbolo() {
        return '#';
    }

    public Item getItemOculto() {
        return itemOculto;
    }

    public boolean isDestruido() {
        return true;
    }
}
