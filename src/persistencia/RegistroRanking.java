package persistencia;

public class RegistroRanking {
    private String nome;
    private int pontuacao;
    private long tempoSegundos;

    RegistroRanking(String nome, int pontuacao, long tempoSegundos) {
        this.nome = nome;
        this.pontuacao = pontuacao;
        this.tempoSegundos = tempoSegundos;
    }

    public int compareTo(RegistroRanking o) {
        return 0;
    }
}
