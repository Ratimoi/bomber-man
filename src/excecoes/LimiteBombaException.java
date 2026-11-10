package excecoes;

public class LimiteBombaException extends JogoException {
    LimiteBombaException() {
        super("Limite de bomba foi atingido\n");
    }
}
