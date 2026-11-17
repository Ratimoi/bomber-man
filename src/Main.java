import java.util.ArrayList;
import java.util.Scanner;

import excecoes.ArquivoException;
import modelo.Dificuldade;
import modelo.EstadoJogo;
import modelo.Jogo;
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

        System.out.println("Bem-vindo ao Bomberman!");

        boolean sair = false;

        while (!sair) {
            System.out.println();
            System.out.println("1 - Jogar");
            System.out.println("2 - Ranking");
            System.out.println("3 - Sair");
            System.out.print("> ");

            switch (scanner.nextLine().trim()) {
                case "1":
                    menuJogar(scanner, arquivos);
                    break;
                case "2":
                    mostrarRanking(arquivos);
                    break;
                case "3":
                    sair = true;
                    break;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }

        scanner.close();
    }

    private static void menuJogar(Scanner scanner, GerenciadorArquivo arquivos) {
        System.out.println();
        System.out.println("1 - Novo jogo");
        System.out.println("2 - Carregar jogo salvo");
        System.out.println("3 - Voltar");
        System.out.print("> ");

        switch (scanner.nextLine().trim()) {
            case "1":
                System.out.print("Digite seu nome: ");
                String nome = scanner.nextLine().trim();
                Dificuldade dificuldade = escolherDificuldade(scanner);
                Jogo novo = new Jogo(dificuldade, nome.isEmpty() ? "Jogador" : nome);

                novo.iniciar();
                jogar(scanner, arquivos, novo);
                break;
            case "2":
                try {
                    jogar(scanner, arquivos, arquivos.carregarJogo(GerenciadorArquivo.CAMINHO_SAVE));
                } catch (ArquivoException e) {
                    System.out.println(e.getMessage().trim());
                }
                break;
            case "3":
                break;
            default:
                System.out.println("Opcao invalida.");
                break;
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

                if (perguntarFimDeJogo(scanner) == Saida.MENU) {
                    return;
                }
            }

            jogo.reiniciarJogo();
        }
    }

    private static Saida rodarPartida(Scanner scanner, GerenciadorArquivo arquivos, Jogo jogo) {
        while (!jogo.verificarFimJogo()) {
            imprimirTela(jogo);
            System.out.print("Comando (W/A/S/D mover, B bomba, H dica, E esperar, Q sair): ");

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
                    jogo.processarTurno('N');
                    break;
                case 'A':
                    jogo.processarTurno('O');
                    break;
                case 'S':
                    jogo.processarTurno('S');
                    break;
                case 'D':
                    jogo.processarTurno('L');
                    break;
                case 'B':
                    jogo.processarTurno('B');
                    break;
                case 'H':
                    System.out.println(jogo.pedirDica());
                    break;
                case 'E':
                    jogo.esperar();
                    break;
                default:
                    System.out.println("Comando invalido.");
                    break;
            }
        }

        imprimirTela(jogo);

        if (jogo.getEstado() == EstadoJogo.VITORIA) {
            System.out.println("Parabens, voce venceu!");
        } else {
            System.out.println("Game over.");
        }

        return Saida.FIM_DE_JOGO;
    }

    private static Saida menuSair(Scanner scanner, GerenciadorArquivo arquivos, Jogo jogo) {
        System.out.println();
        System.out.println("1 - Salvar jogo e voltar ao menu");
        System.out.println("2 - Reiniciar jogo");
        System.out.println("3 - Novo jogo (volta ao menu sem salvar)");
        System.out.println("4 - Continuar jogando");
        System.out.print("> ");

        switch (scanner.nextLine().trim()) {
            case "1":
                try {
                    arquivos.salvarJogo(jogo, GerenciadorArquivo.CAMINHO_SAVE);
                    System.out.println("Jogo salvo.");
                    return Saida.MENU;
                } catch (ArquivoException e) {
                    System.out.println(e.getMessage().trim());
                    return null;
                }
            case "2":
                return Saida.REINICIAR;
            case "3":
                return Saida.MENU;
            default:
                return null;
        }
    }

    private static Saida perguntarFimDeJogo(Scanner scanner) {
        while (true) {
            System.out.println();
            System.out.println("1 - Reiniciar Jogo");
            System.out.println("2 - Novo Jogo");
            System.out.print("> ");

            switch (scanner.nextLine().trim()) {
                case "1":
                    return Saida.REINICIAR;
                case "2":
                    return Saida.MENU;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }

    private static void registrarNoRanking(GerenciadorArquivo arquivos, Jogo jogo) {
        Ranking ranking;

        try {
            ranking = arquivos.carregarRanking();
        } catch (ArquivoException e) {
            System.out.println(e.getMessage().trim());
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

    private static void mostrarRanking(GerenciadorArquivo arquivos) {
        try {
            ArrayList<RegistroRanking> top10 = arquivos.carregarRanking().getTop10();

            System.out.println();
            System.out.println("=== RANKING (Top 10) ===");

            if (top10.isEmpty()) {
                System.out.println("Nenhum registro ainda.");
            }

            for (int i = 0; i < top10.size(); i++) {
                System.out.println((i + 1) + ". " + top10.get(i));
            }
        } catch (ArquivoException e) {
            System.out.println(e.getMessage().trim());
        }
    }

    private static void imprimirTela(Jogo jogo) {
        Jogador jogador = jogo.getJogador();

        System.out.println();
        System.out.println();
        System.out.println("=".repeat(30));
        jogo.imprimir();
        System.out.println("Vidas: " + jogador.getVidas()
                + " | Bombas: " + jogador.getBombasAtivas() + "/" + jogador.getMaxBombas()
                + " | Raio: " + jogador.getRaioExplosao()
                + " | Pontos: " + jogador.getPontuacao()
                + " | Dicas: " + jogo.getDicasRestantes());
    }

    private static Dificuldade escolherDificuldade(Scanner scanner) {
        System.out.println("Escolha a dificuldade:");
        System.out.println("1 - Facil");
        System.out.println("2 - Medio");
        System.out.println("3 - Dificil");

        while (true) {
            System.out.print("> ");
            String entrada = scanner.nextLine().trim();

            switch (entrada) {
                case "1":
                    return Dificuldade.FACIL;
                case "2":
                    return Dificuldade.MEDIO;
                case "3":
                    return Dificuldade.DIFICIL;
                default:
                    System.out.println("Opcao invalida.");
                    break;
            }
        }
    }
}
