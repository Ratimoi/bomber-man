package persistencia;

public class RegistroRanking implements Comparable<RegistroRanking> {
    private String nome;
    private int pontuacao;
    private long tempoSegundos;

    public RegistroRanking(String nome, int pontuacao, long tempoSegundos) {
        String limpo = nome == null ? "" : nome.replace(';', ' ').replace('\n', ' ').replace('\r', ' ').trim();

        this.nome = limpo.isEmpty() ? "Anonimo" : limpo;
        this.pontuacao = pontuacao;
        this.tempoSegundos = tempoSegundos;
    }

    @Override
    public int compareTo(RegistroRanking o) {
        if (this.pontuacao != o.pontuacao) {
            return Integer.compare(o.pontuacao, this.pontuacao);
        }

        return Long.compare(this.tempoSegundos, o.tempoSegundos);
    }

    public String getNome() {
        return nome;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public long getTempoSegundos() {
        return tempoSegundos;
    }

    @Override
    public String toString() {
        return nome + " - " + pontuacao + " pts - " + tempoSegundos + "s";
    }
}
