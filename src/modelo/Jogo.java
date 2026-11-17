package modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import excecoes.ArquivoException;
import excecoes.LimiteBombaException;
import excecoes.MovimentoInvalidoException;
import modelo.bombas.Bomba;
import modelo.bombas.Explosao;
import modelo.inimigos.Inimigo;
import modelo.itens.Item;
import modelo.itens.PortaSaida;
import modelo.personagens.Jogador;
import persistencia.GeradorMapa;

public class Jogo {
    private static final int TAMANHO_PADRAO = 11;

    private Bomba[] bombas;
    private int dicasRestantes;
    private Dificuldade dificuldade;
    private EstadoJogo estado;
    private long inicioMs;
    private Inimigo[] inimigos;
    private Jogador jogador;
    private boolean modoDebug;
    private String nomeJogador;
    private long semente;
    private Tabuleiro tabuleiro;
    private long tempoAnteriorMs;
    private int turno;

    public Jogo(Dificuldade dificuldade, String nomeJogador) {
        this.dificuldade = dificuldade;
        this.nomeJogador = nomeJogador;
    }

    public void alternarDebug() {
        this.modoDebug = !this.modoDebug;
    }

    public Bomba[] getBombas() {
        return bombas.clone();
    }

    public int getDicasRestantes() {
        return dicasRestantes;
    }

    public Dificuldade getDificuldade() {
        return dificuldade;
    }

    public EstadoJogo getEstado() {
        return estado;
    }

    public Inimigo[] getInimigos() {
        return inimigos.clone();
    }

    public Jogador getJogador() {
        return jogador;
    }

    public long getSemente() {
        return semente;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public long getTempoSegundos() {
        return (tempoAnteriorMs + System.currentTimeMillis() - inicioMs) / 1000;
    }

    public int getTurno() {
        return turno;
    }

    public boolean isModoDebug() {
        return modoDebug;
    }

    public void restaurar(Tabuleiro tabuleiro, Jogador jogador, List<Inimigo> inimigos, List<Bomba> bombas, int turno, int dicasRestantes, boolean modoDebug, long semente, long tempoSegundos) {
        this.tabuleiro = tabuleiro;
        this.jogador = jogador;
        this.inimigos = inimigos.toArray(new Inimigo[0]);
        this.bombas = bombas.toArray(new Bomba[0]);
        this.turno = turno;
        this.dicasRestantes = dicasRestantes;
        this.modoDebug = modoDebug;
        this.semente = semente;
        this.estado = EstadoJogo.EM_ANDAMENTO;
        this.tempoAnteriorMs = tempoSegundos * 1000;
        this.inicioMs = System.currentTimeMillis();
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
                jogador.adicionarPontos(inimigo.getPontos());
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
        GeradorMapa gerador = new GeradorMapa(this.semente);

        try {
            this.tabuleiro = gerador.carregarArquivo(dificuldade.getArquivoMapa());
            gerador.espalharCaixotes(this.tabuleiro, dificuldade);
            gerador.espalharItens(this.tabuleiro);
        } catch (ArquivoException e) {
            this.tabuleiro = gerador.gerar(TAMANHO_PADRAO, dificuldade);
        }

        this.inimigos = gerador.espalharInimigos(this.tabuleiro, dificuldade).toArray(new Inimigo[0]);
        this.bombas = new Bomba[0];
        this.turno = 0;
    }

    private void comecar() {
        montarFase();

        this.jogador = new Jogador(new Posicao(0, 0), nomeJogador);
        this.dicasRestantes = 3;
        this.estado = EstadoJogo.EM_ANDAMENTO;
        this.tempoAnteriorMs = 0;
        this.inicioMs = System.currentTimeMillis();
    }

    public void iniciar() {
        this.semente = new Random().nextLong();
        comecar();
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
        comecar();
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
