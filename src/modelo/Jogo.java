package modelo;

public class Jogo {
    private Bomba[] bombas;
    private int dicasRestantes;
    private Dificuldade dificuldade;
    private EstadoJogo estado;
    private Inimigo[] Inimigos;
    private Jogador jogador;
    private boolean modoDebug;
    private Tabuleiro tabuleiro;
    private int turno;

    Jogo() {

    }

    public void alternarDebug() {
        this.modoDebug = !this.modoDebug;
    }

    public void atualizarBombas() {
        for (int i = 0; i < bombas.length; i++) {
            if (bombas[i].getTempoRestante() == 0) {
                bombas[i].explodir(this.tabuleiro);
            }
        }
    }

    public void esperar() {

    }

    public void iniciar() {

    }

    public void moverInimigos() {

    }

    public void moverJogador(Direcao dir) {

    }

    public String pedirDica() {
        return "dica";
    }

    public void plantarBomba() {

    }

    public void processarTurno(char cmd) {

    }

    public void reiniciarFase() {

    }

    public void reiniciarJogo() {

    }

    public void verificarColisoes() {

    }

    public boolean verificarFimJogo() {
        return false;
    }
}
