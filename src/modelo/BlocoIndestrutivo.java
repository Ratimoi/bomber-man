package modelo;

public class BlocoIndestrutivo extends Bloco {
    BlocoIndestrutivo(boolean transponivel) {
        super(transponivel);
    }

    public char getSimbolo() {
        return '#';
    }
}
