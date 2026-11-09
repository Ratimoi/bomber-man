package excecoes;

public class MovimentoInvalidoException extends RuntimeException {
    public MovimentoInvalidoException(String message) {
        super(message);
    }
}
