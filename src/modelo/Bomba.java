package modelo;

public class Bomba extends ElementoMapa {
    private Jogador dono;
    private Jogo jogo;
    private int raio;
    private int tempoRestante;
    private int tempoTotal;

    Bomba(Posicao posicao,Jogador dono,Jogo jogo,int raio, int tempoRestante,int tempoTotal) {
        super(posicao);
        this.dono = dono;
        this.jogo = jogo;
        this.raio =raio;
        this.tempoRestante = tempoRestante;



    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public int getRaio() {
        return raio;
    }

    public void setRaio(int raio) {
        this.raio = raio;
    }

    public int getTempoRestante() {
        return tempoRestante;
    }

    public void setTempoRestante(int tempoRestante) {
        this.tempoRestante = tempoRestante;
    }

    public int getTempoTotal() {
        return tempoTotal;
    }

    public void setTempoTotal(int tempoTotal) {
        this.tempoTotal = tempoTotal;
    }

    public Jogador getDono() {
        return dono;
    }

    public void setDono(Jogador dono) {
        this.dono = dono;
    }

    public char getSimbolo(){
        return 'b';
    }

    public Explosao explodir(Tabuleiro tab) {
        Explosao explosao = new Explosao();

        return explosao;
    }
}
