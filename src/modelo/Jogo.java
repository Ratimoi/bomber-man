package modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import excecoes.ArquivoException;
import excecoes.LimiteBombaException;
import excecoes.LimiteDicaException;
import excecoes.MovimentoInvalidoException;
import modelo.blocos.BlocoDestrutivo;
import modelo.bombas.Bomba;
import modelo.bombas.BombaArmadilha;
import modelo.bombas.Explosao;
import modelo.inimigos.Inimigo;
import modelo.itens.Item;
import modelo.itens.PortaSaida;
import modelo.itens.PowerUpBomba;
import modelo.itens.PowerUpRaio;
import modelo.personagens.Jogador;
import persistencia.GeradorMapa;

public class Jogo {
    private static final int TAMANHO_PADRAO = 11;
    private static final int TEMPO_BOMBA = 5;

    private Bomba bombaPlantadaNoTurno;
    private Bomba[] bombas;
    private int dicasRestantes;
    private Dificuldade dificuldade;
    private EstadoJogo estado;
    private List<Posicao> fogo = new ArrayList<>();
    private long inicioMs;
    private Inimigo[] inimigos;
    private Jogador jogador;
    private List<String> mensagens = new ArrayList<>();
    private boolean modoDebug;
    private String nomeJogador;
    private boolean reinicioPendente;
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

    public int getInimigosRestantes() {
        int vivos = 0;

        for (Inimigo inimigo : inimigos) {
            if (!inimigo.isDestruido()) {
                vivos++;
            }
        }

        return vivos;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public List<String> getMensagens() {
        return new ArrayList<>(mensagens);
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

    public boolean isReinicioPendente() {
        return reinicioPendente;
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
        this.reinicioPendente = false;
        this.bombaPlantadaNoTurno = null;
        limparTurno();
        this.tempoAnteriorMs = tempoSegundos * 1000;
        this.inicioMs = System.currentTimeMillis();
    }

    public void imprimir() {
        int tamanho = this.tabuleiro.getTamanho();
        StringBuilder cabecalho = new StringBuilder("  ");

        for (int coluna = 0; coluna < tamanho; coluna++) {
            cabecalho.append(String.format("%2d ", coluna));
        }

        System.out.println(cabecalho);

        for (int linha = 0; linha < tamanho; linha++) {
            System.out.print((char) ('A' + linha) + " ");

            for (int coluna = 0; coluna < tamanho; coluna++) {
                System.out.print(" " + getSimboloNaPosicao(new Posicao(coluna, linha)) + " ");
            }
            System.out.println();
        }
    }

    private char getSimboloNaPosicao(Posicao posicao) {
        for (Posicao emChamas : fogo) {
            if (mesmaPosicao(emChamas, posicao)) {
                return '+';
            }
        }

        if (!jogador.isDestruido() && mesmaPosicao(jogador.getPosicao(), posicao)) {
            return jogador.getSimbolo();
        }

        for (Inimigo inimigo : inimigos) {
            if (!inimigo.isDestruido() && mesmaPosicao(inimigo.getPosicao(), posicao)) {
                return inimigo.getSimbolo();
            }
        }

        Celula celula = this.tabuleiro.getCelula(posicao);

        if (modoDebug && celula.getBloco() instanceof BlocoDestrutivo) {
            Item oculto = ((BlocoDestrutivo) celula.getBloco()).getItemOculto();

            if (oculto != null) {
                return oculto.getSimbolo();
            }
        }

        return celula.getSimbolo();
    }

    public void atualizarBombas() {
        List<Bomba> restantes = new ArrayList<>();

        for (Bomba bomba : bombas) {
            Celula celula = this.tabuleiro.getCelula(bomba.getPosicao());

            if (celula == null || celula.getBomba() != bomba) {
                bomba.getDono().liberarBomba();
                continue;
            }

            if (bomba != bombaPlantadaNoTurno) {
                bomba.tick();
            }

            if (bomba.getTempoRestante() <= 0) {
                Explosao explosao = bomba.explodir(this.tabuleiro);

                celula.removerBomba();
                bomba.getDono().liberarBomba();
                mensagens.add("Uma bomba explodiu!");
                fogo.addAll(explosao.getCelulasAtingidas());
                aplicarDanoExplosao(explosao);
            } else {
                restantes.add(bomba);
            }
        }

        this.bombas = restantes.toArray(new Bomba[0]);
        this.bombaPlantadaNoTurno = null;
    }

    private void aplicarDanoExplosao(Explosao explosao) {
        if (!jogador.isDestruido() && explosao.atinge(jogador.getPosicao())) {
            jogadorAtingido("Voce foi atingido pela explosao!");
        }

        for (Inimigo inimigo : inimigos) {
            if (!inimigo.isDestruido() && explosao.atinge(inimigo.getPosicao())) {
                inimigo.receberDano(this.tabuleiro);
                jogador.adicionarPontos(inimigo.getPontos());
                mensagens.add("Inimigo derrotado! +" + inimigo.getPontos() + " pontos.");
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

            if (item instanceof PowerUpBomba) {
                mensagens.add("Power-up! Agora voce pode plantar mais uma bomba.");
            } else if (item instanceof PowerUpRaio) {
                mensagens.add("Power-up! O raio da explosao aumentou.");
            } else if (item instanceof PortaSaida) {
                mensagens.add(((PortaSaida) item).isAberta()
                        ? "Voce escapou pela porta de saida!"
                        : "A porta esta trancada: derrote todos os inimigos primeiro.");
            }

            if (!(item instanceof PortaSaida) || ((PortaSaida) item).isAberta()) {
                celula.setItem(null);
            }
        }
    }

    private Direcao direcaoDe(char cmd) {
        return switch (cmd) {
            case 'N' -> Direcao.NORTE;
            case 'S' -> Direcao.SUL;
            case 'L' -> Direcao.LESTE;
            case 'O' -> Direcao.OESTE;
            default -> null;
        };
    }

    private void detonarArmadilha(Celula celula, BombaArmadilha armadilha) {
        celula.removerBloco();

        Explosao explosao = armadilha.getBombaOriginal().explodir(this.tabuleiro);

        mensagens.add("Era uma armadilha! Ela detonou.");
        fogo.addAll(explosao.getCelulasAtingidas());
        aplicarDanoExplosao(explosao);
    }

    private void jogadorAtingido(String motivo) {
        if (reinicioPendente || jogador.isDestruido()) {
            return;
        }

        jogador.receberDano(this.tabuleiro);

        if (jogador.isDestruido()) {
            mensagens.add(motivo + " Suas vidas acabaram.");
        } else {
            reinicioPendente = true;
            mensagens.add(motivo + " Vidas restantes: " + jogador.getVidas() + ".");
        }
    }

    private void limparTurno() {
        fogo.clear();
        mensagens.clear();
    }

    public void esperar() {
        limparTurno();
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
        this.bombaPlantadaNoTurno = null;
        this.reinicioPendente = false;
        this.turno = 0;
        limparTurno();
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

    public void moverJogador(Direcao dir) throws MovimentoInvalidoException {
        Posicao atual = jogador.getPosicao();
        Posicao destino = new Posicao(atual.getColuna() + dir.getdColuna(), atual.getLinha() + dir.getdLinha());
        Celula celulaDestino = this.tabuleiro.getCelula(destino);

        if (celulaDestino != null && celulaDestino.getBloco() instanceof BombaArmadilha) {
            limparTurno();
            detonarArmadilha(celulaDestino, (BombaArmadilha) celulaDestino.getBloco());
            return;
        }

        jogador.mover(dir, this.tabuleiro);
        limparTurno();
        coletarItemNaPosicao(jogador.getPosicao());
    }

    public String pedirDica() throws LimiteDicaException {
        if (dicasRestantes <= 0) {
            throw new LimiteDicaException();
        }

        dicasRestantes--;
        return "Fuja dos inimigos e plante bombas perto de blocos destrutiveis.";
    }

    public void plantarBomba() {
        Celula celula = this.tabuleiro.getCelula(jogador.getPosicao());

        if (celula.getBomba() != null) {
            mensagens.add("Ja existe uma bomba aqui.");
            return;
        }

        try {
            jogador.plantarBomba();
        } catch (LimiteBombaException e) {
            mensagens.add("Limite de bombas atingido!");
            return;
        }

        Bomba bomba = new Bomba(jogador.getPosicao(), jogador, this, jogador.getRaioExplosao(), TEMPO_BOMBA, TEMPO_BOMBA);
        celula.setBomba(bomba);

        Bomba[] novas = Arrays.copyOf(bombas, bombas.length + 1);
        novas[bombas.length] = bomba;
        this.bombas = novas;
        this.bombaPlantadaNoTurno = bomba;
        mensagens.add("Bomba plantada! Ela explode em " + TEMPO_BOMBA + " turnos.");
    }

    public void processarTurno(char cmd) throws MovimentoInvalidoException {
        Direcao dir = direcaoDe(cmd);

        if (dir != null) {
            moverJogador(dir);
        } else {
            limparTurno();

            if (cmd == 'B') {
                plantarBomba();
            } else if (cmd == 'D') {
                try {
                    mensagens.add(pedirDica());
                } catch (LimiteDicaException e) {
                    mensagens.add("Sem dicas restantes.");
                }
            }
        }

        avancarTurno();
    }

    public void reiniciarFase() {
        montarFase();

        this.jogador.setPosicao(new Posicao(0, 0));
        this.jogador.zerarBombasAtivas();
        this.estado = EstadoJogo.EM_ANDAMENTO;
        mensagens.add("Fase reiniciada.");
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
                jogadorAtingido("Um inimigo te pegou!");
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
