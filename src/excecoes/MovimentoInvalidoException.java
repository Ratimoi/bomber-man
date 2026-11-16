package excecoes;

public class MovimentoInvalidoException extends JogoException {
    public MovimentoInvalidoException() {
        super("Movimento inválido\n");
    }
}
