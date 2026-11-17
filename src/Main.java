import java.util.ArrayList;
import java.util.Scanner;

import excecoes.ArquivoException;
import excecoes.LimiteDicaException;
import excecoes.MovimentoInvalidoException;
import modelo.Dificuldade;
import modelo.EstadoJogo;
import modelo.Jogo;
import modelo.bombas.Bomba;
import modelo.personagens.Jogador;
import persistencia.GerenciadorArquivo;
import persistencia.Ranking;
import persistencia.RegistroRanking;

public class Main {
    private enum Saida {
        FIM_DE_JOGO, MENU, REINICIAR
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GerenciadorArquivo arquivos = new GerenciadorArquivo();
        boolean sair = false;

        while (!sair) {
            int opcao = Tela.escolher(scanner, "BOMBERMAN",
                    "Bem-vindo! Destrua os caixotes, derrote todos os\ninimigos e encontre a porta de saída.",
                    "Jogar", "Ranking", "Sair");

            switch (opcao) {
                case 1:
                    menuJogar(scanner, arquivos);
                    break;
                case 2:
                    mostrarRanking(scanner, arquivos);
                    break;
                default:
                    sair = true;
                    break;
            }
        }

        Tela.limpar();
        System.out.println("Ate a proxima!");
        scanner.close();
    }

    private static void menuJogar(Scanner scanner, GerenciadorArquivo arquivos) {
        int opcao = Tela.escolher(scanner, "JOGAR", null, "Novo jogo", "Carregar jogo salvo", "Voltar");

        if (opcao == 1) {
            Tela.limpar();
            Tela.cabecalho("NOVO JOGO");
            System.out.println();
            System.out.print("Digite seu nome: ");
            String nome = scanner.nextLine().trim();

            int nivel = Tela.escolher(scanner, "DIFICULDADE", "O nivel facil nao tem inimigo sabotador.", "Facil", "Medio", "Dificil");
            Dificuldade dificuldade = Dificuldade.values()[nivel - 1];

            Jogo jogo = new Jogo(dificuldade, nome.isEmpty() ? "Jogador" : nome);
            jogo.iniciar();
            jogar(scanner, arquivos, jogo);
        } else if (opcao == 2) {
            try {
                jogar(scanner, arquivos, arquivos.carregarJogo(GerenciadorArquivo.CAMINHO_SAVE));
            } catch (ArquivoException e) {
                mostrarErro(scanner, e);
            }
        }
    }

    private static void jogar(Scanner scanner, GerenciadorArquivo arquivos, Jogo jogo) {
        while (true) {
            Saida saida = rodarPartida(scanner, arquivos, jogo);

            if (saida == Saida.MENU) {
                return;
            }

            if (saida == Saida.FIM_DE_JOGO) {
                registrarNoRanking(arquivos, jogo);

                String resultado = jogo.getEstado() == EstadoJogo.VITORIA ? "VITORIA!" : "GAME OVER";
                String resumo = resultado + "\nPontos: " + jogo.getJogador().getPontuacao() + " | Tempo: " + jogo.getTempoSegundos() + "s";

                if (Tela.escolher(scanner, "FIM DE JOGO", resumo, "Reiniciar Jogo", "Novo Jogo") == 2) {
                    return;
                }
            }

            jogo.reiniciarJogo();
        }
    }

    private static Saida rodarPartida(Scanner scanner, GerenciadorArquivo arquivos, Jogo jogo) {
        String aviso = null;

        while (!jogo.verificarFimJogo()) {
            imprimirTela(jogo, aviso);
            aviso = null;

            if (jogo.isReinicioPendente()) {
                Tela.pausar(scanner, "Pressione ENTER para reiniciar a fase...");
                jogo.reiniciarFase();
                continue;
            }

            System.out.print("Comando: ");
            String entrada = scanner.nextLine().trim().toUpperCase();

            if (entrada.isEmpty()) {
                continue;
            }

            switch (entrada.charAt(0)) {
                case 'Q':
                    Saida saida = menuSair(scanner, arquivos, jogo);

                    if (saida != null) {
                        return saida;
                    }
                    break;
                case 'W':
                    aviso = executar(jogo, 'N');
                    break;
                case 'A':
                    aviso = executar(jogo, 'O');
                    break;
                case 'S':
                    aviso = executar(jogo, 'S');
                    break;
                case 'D':
                    aviso = executar(jogo, 'L');
                    break;
                case 'B':
                    aviso = executar(jogo, 'B');
                    break;
                case 'H':
                    try {
                        aviso = jogo.pedirDica();
                    } catch (LimiteDicaException e) {
                        aviso = "Sem dicas restantes.";
                    }
                    break;
                case 'E':
                    jogo.esperar();
                    break;
                case 'X':
                    jogo.alternarDebug();
                    aviso = jogo.isModoDebug()
                            ? "DEBUG ligado: power-ups e a porta aparecem sob os caixotes."
                            : "DEBUG desligado.";
                    break;
                default:
                    aviso = "Comando invalido.";
                    break;
            }
        }

        imprimirTela(jogo, null);
        System.out.println();
        System.out.println(jogo.getEstado() == EstadoJogo.VITORIA ? "*** VOCE VENCEU! ***" : "*** GAME OVER ***");
        Tela.pausar(scanner, "Pressione ENTER para continuar...");

        return Saida.FIM_DE_JOGO;
    }

    private static String executar(Jogo jogo, char comando) {
        try {
            jogo.processarTurno(comando);
            return null;
        } catch (MovimentoInvalidoException e) {
            return "Movimento invalido: ha uma parede ou o limite do mapa. Tente outra direcao.";
        }
    }

    private static Saida menuSair(Scanner scanner, GerenciadorArquivo arquivos, Jogo jogo) {
        int opcao = Tela.escolher(scanner, "PAUSA", null,
                "Salvar jogo e voltar ao menu",
                "Reiniciar jogo",
                "Novo jogo (volta ao menu sem salvar)",
                "Continuar jogando");

        switch (opcao) {
            case 1:
                try {
                    arquivos.salvarJogo(jogo, GerenciadorArquivo.CAMINHO_SAVE);
                    Tela.limpar();
                    Tela.cabecalho("JOGO SALVO");
                    Tela.pausar(scanner, "Jogo salvo em " + GerenciadorArquivo.CAMINHO_SAVE + ". Pressione ENTER...");
                    return Saida.MENU;
                } catch (ArquivoException e) {
                    mostrarErro(scanner, e);
                    return null;
                }
            case 2:
                return Saida.REINICIAR;
            case 3:
                return Saida.MENU;
            default:
                return null;
        }
    }

    private static void mostrarErro(Scanner scanner, ArquivoException e) {
        Tela.limpar();
        Tela.cabecalho("ERRO");
        System.out.println();
        System.out.println(e.getMessage().trim());
        Tela.pausar(scanner, "Pressione ENTER para voltar...");
    }

    private static void registrarNoRanking(GerenciadorArquivo arquivos, Jogo jogo) {
        Ranking ranking;

        try {
            ranking = arquivos.carregarRanking();
        } catch (ArquivoException e) {
            ranking = new Ranking();
        }

        Jogador jogador = jogo.getJogador();

        ranking.adicionar(new RegistroRanking(jogador.getNome(), jogador.getPontuacao(), jogo.getTempoSegundos()));

        try {
            arquivos.salvarRanking(ranking);
        } catch (ArquivoException e) {
            System.out.println(e.getMessage().trim());
        }
    }

    private static void mostrarRanking(Scanner scanner, GerenciadorArquivo arquivos) {
        Tela.limpar();
        Tela.cabecalho("RANKING - TOP 10");
        System.out.println();

        try {
            ArrayList<RegistroRanking> top10 = arquivos.carregarRanking().getTop10();

            if (top10.isEmpty()) {
                System.out.println("Nenhum registro ainda. Jogue uma partida!");
            }

            for (int i = 0; i < top10.size(); i++) {
                System.out.println(String.format("%2d. ", i + 1) + top10.get(i));
            }
        } catch (ArquivoException e) {
            System.out.println(e.getMessage().trim());
        }

        Tela.pausar(scanner, "Pressione ENTER para voltar...");
    }

    private static void imprimirTela(Jogo jogo, String aviso) {
        Jogador jogador = jogo.getJogador();

        Tela.limpar();
        Tela.cabecalho("BOMBERMAN | " + jogador.getNome() + " | " + jogo.getDificuldade() + (jogo.isModoDebug() ? " | DEBUG" : ""));
        jogo.imprimir();
        Tela.separador();
        System.out.println("Vidas: " + jogador.getVidas()
                + " | Bombas: " + jogador.getBombasAtivas() + "/" + jogador.getMaxBombas()
                + " | Raio: " + jogador.getRaioExplosao()
                + " | Pontos: " + jogador.getPontuacao()
                + " | Dicas: " + jogo.getDicasRestantes());
        System.out.println("Turno: " + jogo.getTurno()
                + " | Tempo: " + jogo.getTempoSegundos() + "s"
                + " | Inimigos: " + jogo.getInimigosRestantes()
                + " | Explosao em: " + temposDasBombas(jogo));
        Tela.separador();

        for (String mensagem : jogo.getMensagens()) {
            System.out.println("> " + mensagem);
        }

        if (aviso != null) {
            System.out.println("> " + aviso);
        }

        System.out.println();
        Tela.legenda();
        Tela.comandos();
    }

    private static String temposDasBombas(Jogo jogo) {
        Bomba[] bombas = jogo.getBombas();

        if (bombas.length == 0) {
            return "-";
        }

        StringBuilder tempos = new StringBuilder();

        for (int i = 0; i < bombas.length; i++) {
            tempos.append(i == 0 ? "" : ", ").append(bombas[i].getTempoRestante());
        }

        return tempos + " turno(s)";
    }
}
