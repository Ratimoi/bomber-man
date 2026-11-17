import java.util.Scanner;

import modelo.Dificuldade;
import modelo.EstadoJogo;
import modelo.Jogo;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Dificuldade dificuldade = escolherDificuldade(scanner);
        Jogo jogo = new Jogo(dificuldade);
        jogo.iniciar();

        while (!jogo.verificarFimJogo()) {
            jogo.imprimir();
            System.out.print("Comando (N/S/L/O mover, B bomba, D dica, E esperar, Q sair): ");

            String entrada = scanner.nextLine().trim().toUpperCase();
            if (entrada.isEmpty()) {
                continue;
            }

            char comando = entrada.charAt(0);

            switch (comando) {
                case 'Q':
                    System.out.println("Saindo...");
                    scanner.close();
                    return;
                case 'D':
                    System.out.println(jogo.pedirDica());
                    break;
                case 'E':
                    jogo.esperar();
                    break;
                default:
                    jogo.processarTurno(comando);
                    break;
            }
        }

        jogo.imprimir();

        if (jogo.getEstado() == EstadoJogo.VITORIA) {
            System.out.println("Parabens, voce venceu!");
        } else {
            System.out.println("Game over.");
        }

        scanner.close();
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
