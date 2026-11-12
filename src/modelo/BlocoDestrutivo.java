package modelo;

public class BlocoDestrutivo extends Bloco {
    private Item itemOculto;

    BlocoDestrutivo(boolean transponivel,Item itemOculto) {
        this.itemOculto = itemOculto;
        super(transponivel);
    }

    public char getSimbolo() {
        return 'C';
    }

    public boolean isDestruido() {
        return false;
    }
}
