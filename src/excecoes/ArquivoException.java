package excecoes;

public class ArquivoException extends JogoException {
    ArquivoException() {
        super("Arquivo inválido ou inexistente\n");
    }
}
