import java.io.IOException;
import java.util.Scanner;

public class Tela {
    private static final String LINHA = "=".repeat(44);

    public static void limpar() {
        if (System.console() == null) {
            System.out.println();
            return;
        }

        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("\n\n\n");
        }
    }

    public static void cabecalho(String titulo) {
        int espacos = Math.max(0, (LINHA.length() - titulo.length()) / 2);

        System.out.println(LINHA);
        System.out.println(" ".repeat(espacos) + titulo);
        System.out.println(LINHA);
    }

    public static void separador() {
        System.out.println("-".repeat(LINHA.length()));
    }

    public static void pausar(Scanner scanner, String texto) {
        System.out.println();
        System.out.print(texto + " ");
        scanner.nextLine();
    }

    public static void legenda() {
        System.out.println("Legenda: P jogador  C comum  R rastreador  S sabotador  █ parede");
        System.out.println("         # caixote  B bomba  + fogo  U +bomba  O +raio  D porta");
    }

    public static void comandos() {
        System.out.println("Comandos:  [W] cima   [A] esquerda   [S] baixo   [D] direita");
        System.out.println("           [B] plantar bomba   [H] dica   [E] esperar   [X] debug   [Q] sair");
    }

    public static int escolher(Scanner scanner, String titulo, String texto, String... opcoes) {
        String aviso = null;

        while (true) {
            limpar();
            cabecalho(titulo);
            System.out.println();

            if (texto != null && !texto.isEmpty()) {
                System.out.println(texto);
                System.out.println();
            }

            for (int i = 0; i < opcoes.length; i++) {
                System.out.println("  [" + (i + 1) + "] " + opcoes[i]);
            }

            System.out.println();

            if (aviso != null) {
                System.out.println(aviso);
                System.out.println();
            }

            System.out.print("Escolha uma opcao: ");

            String entrada = scanner.nextLine().trim();

            for (int i = 0; i < opcoes.length; i++) {
                if (entrada.equals(String.valueOf(i + 1))) {
                    return i + 1;
                }
            }

            aviso = "Opcao invalida. Digite um numero de 1 a " + opcoes.length + ".";
        }
    }
}
