package excecoes;

public class LimiteDicaException extends JogoException {
    LimiteDicaException() {
        super("Limite de dicas foi atingido\n");
    }
}
