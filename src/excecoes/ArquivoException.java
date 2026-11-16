package excecoes;

public class ArquivoException extends JogoException {
    public ArquivoException() {
        super("Arquivo inválido ou inexistente\n");
    }
}
