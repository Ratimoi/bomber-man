package excecoes;

public class MovimentoInvalidoException extends JogoException {
    MovimentoInvalidoException() {
        super("Movimento inválido\n");
    }
}
