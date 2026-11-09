package modelo;

public class Bomba extends ElementoMapa {
    private Jogador dono;
    private Jogo jogo;
    private int raio;
    private int tempoRestante;
    private int tempoTotal;

    Bomba() {

    }

    public Explosao explodir(Tabuleiro tab) {
        Explosao explosao = new Explosao();

        return explosao;
    }
}
