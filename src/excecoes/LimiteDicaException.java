package excecoes;

public class LimiteDicaException extends JogoException {
    public LimiteDicaException() {
        super("Limite de dicas foi atingido\n");
    }
}
