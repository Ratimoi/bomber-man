package modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import excecoes.LimiteBombaException;
import excecoes.MovimentoInvalidoException;
import modelo.bombas.Bomba;
import modelo.bombas.Explosao;
import modelo.inimigos.Inimigo;
import modelo.inimigos.InimigoComum;
import modelo.itens.Item;
import modelo.itens.PortaSaida;
import modelo.personagens.Jogador;

public class Jogo {
    private Bomba[] bombas;
    private int dicasRestantes;
    private Dificuldade dificuldade;
    private EstadoJogo estado;
    private Inimigo[] inimigos;
    private Jogador jogador;
    private boolean modoDebug;
    private Tabuleiro tabuleiro;
    private int turno;

    public Jogo(Dificuldade dificuldade) {
        this.dificuldade = dificuldade;
    }

    public void alternarDebug() {
        this.modoDebug = !this.modoDebug;
    }

    public EstadoJogo getEstado() {
        return estado;
    }

    public void imprimir() {
        int tamanho = this.tabuleiro.getTamanho();

        for (int linha = 0; linha < tamanho; linha++) {
            for (int coluna = 0; coluna < tamanho; coluna++) {
                System.out.print(getSimboloNaPosicao(new Posicao(coluna, linha)));
            }
            System.out.println();
        }
    }

    private char getSimboloNaPosicao(Posicao posicao) {
        if (!jogador.isDestruido() && mesmaPosicao(jogador.getPosicao(), posicao)) {
            return jogador.getSimbolo();
        }

        for (Inimigo inimigo : inimigos) {
            if (!inimigo.isDestruido() && mesmaPosicao(inimigo.getPosicao(), posicao)) {
                return inimigo.getSimbolo();
            }
        }

        return this.tabuleiro.getCelula(posicao).getSimbolo();
    }

    public void atualizarBombas() {
        List<Bomba> restantes = new ArrayList<>();

        for (Bomba bomba : bombas) {
            Celula celula = this.tabuleiro.getCelula(bomba.getPosicao());

            if (celula == null || celula.getBomba() != bomba) {
                continue;
            }

            bomba.tickar();

            if (bomba.getTempoRestante() <= 0) {
                Explosao explosao = bomba.explodir(this.tabuleiro);
                this.tabuleiro.getCelula(bomba.getPosicao()).removerBomba();
                bomba.getDono().liberarBomba();
                aplicarDanoExplosao(explosao);
            } else {
                restantes.add(bomba);
            }
        }

        this.bombas = restantes.toArray(new Bomba[0]);
    }

    private void aplicarDanoExplosao(Explosao explosao) {
        if (!jogador.isDestruido() && explosao.atinge(jogador.getPosicao())) {
            jogador.receberDano(this.tabuleiro);
        }

        for (Inimigo inimigo : inimigos) {
            if (!inimigo.isDestruido() && explosao.atinge(inimigo.getPosicao())) {
                inimigo.receberDano(this.tabuleiro);
            }
        }
    }

    private void avancarTurno() {
        moverInimigos();
        atualizarBombas();
        verificarColisoes();
        verificarFimJogo();
        this.turno++;
    }

    private boolean mesmaPosicao(Posicao a, Posicao b) {
        return a.getLinha() == b.getLinha() && a.getColuna() == b.getColuna();
    }

    private void coletarItemNaPosicao(Posicao posicao) {
        Celula celula = this.tabuleiro.getCelula(posicao);
        Item item = celula.getItem();

        if (item != null) {
            item.coletar(this.jogador, this);

            if (!(item instanceof PortaSaida) || ((PortaSaida) item).isAberta()) {
                celula.setItem(null);
            }
        }
    }

    public void esperar() {
        avancarTurno();
    }

    private void montarFase() {
        int tamanho = 11;
        this.tabuleiro = new Tabuleiro(tamanho);

        int quantidade = dificuldade.getQuantidadeInimigos();
        this.inimigos = new Inimigo[quantidade];
        for (int i = 0; i < quantidade; i++) {
            Posicao posicaoInimigo = new Posicao(tamanho - 1, tamanho - 1);
            this.inimigos[i] = new InimigoComum(posicaoInimigo);
        }

        this.bombas = new Bomba[0];
        this.turno = 0;
    }

    public void iniciar() {
        montarFase();

        this.jogador = new Jogador(new Posicao(0, 0), "Jogador");
        this.dicasRestantes = 3;
        this.estado = EstadoJogo.EM_ANDAMENTO;
    }

    public void moverInimigos() {
        for (Inimigo inimigo : inimigos) {
            if (!inimigo.isDestruido()) {
                inimigo.agir(this.tabuleiro, this.jogador);
            }
        }
    }

    public void moverJogador(Direcao dir) {
        try {
            jogador.mover(dir, this.tabuleiro);
            coletarItemNaPosicao(jogador.getPosicao());
        } catch (MovimentoInvalidoException ignored) {

        }
    }

    public String pedirDica() {
        if (dicasRestantes <= 0) {
            return "Sem dicas restantes.";
        }

        dicasRestantes--;
        return "Fuja dos inimigos e plante bombas perto de blocos destrutiveis.";
    }

    public void plantarBomba() {
        try {
            jogador.plantarBomba();
        } catch (LimiteBombaException e) {
            return;
        }

        Bomba bomba = new Bomba(jogador.getPosicao(), jogador, this, jogador.getRaioExplosao(), 3, 3);
        this.tabuleiro.getCelula(jogador.getPosicao()).setBomba(bomba);

        Bomba[] novas = Arrays.copyOf(bombas, bombas.length + 1);
        novas[bombas.length] = bomba;
        this.bombas = novas;
    }

    public void processarTurno(char cmd) {
        switch (cmd) {
            case 'N' -> moverJogador(Direcao.NORTE);
            case 'S' -> moverJogador(Direcao.SUL);
            case 'L' -> moverJogador(Direcao.LESTE);
            case 'O' -> moverJogador(Direcao.OESTE);
            case 'B' -> plantarBomba();
            case 'D' -> pedirDica();
            default -> {
            }
        }

        avancarTurno();
    }

    public void reiniciarFase() {
        montarFase();

        this.jogador.setPosicao(new Posicao(0, 0));
        this.estado = EstadoJogo.EM_ANDAMENTO;
    }

    public void reiniciarJogo() {
        iniciar();
    }

    public void verificarColisoes() {
        if (jogador.isDestruido()) {
            return;
        }

        for (Inimigo inimigo : inimigos) {
            if (!inimigo.isDestruido() && mesmaPosicao(inimigo.getPosicao(), jogador.getPosicao())) {
                jogador.receberDano(this.tabuleiro);
                return;
            }
        }
    }

    public boolean todosInimigosDerrotados() {
        for (Inimigo inimigo : inimigos) {
            if (!inimigo.isDestruido()) {
                return false;
            }
        }
        return true;
    }

    public void vencer() {
        this.estado = EstadoJogo.VITORIA;
    }

    public boolean verificarFimJogo() {
        if (jogador.isDestruido()) {
            this.estado = EstadoJogo.DERROTA;
            return true;
        }

        return this.estado == EstadoJogo.VITORIA;
    }
}
