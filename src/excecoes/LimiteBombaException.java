package excecoes;

public class LimiteBombaException extends JogoException {
    public LimiteBombaException() {
        super("Limite de bomba foi atingido\n");
    }
}
