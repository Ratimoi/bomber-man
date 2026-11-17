package excecoes;

public class ArquivoException extends JogoException {
    public ArquivoException() {
        super("Arquivo inválido ou inexistente\n");
    }

    public ArquivoException(String detalhe) {
        super("Arquivo inválido ou inexistente: " + detalhe + "\n");
    }
}
