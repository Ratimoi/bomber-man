package persistencia;

public class Ranking {
    private RegistroRanking[] registros;

    public void adicionar(RegistroRanking r) {

    }

    public RegistroRanking[] getTop10() {
        RegistroRanking[] top10 = new RegistroRanking[10];

        System.arraycopy(this.registros, 0, top10, 0, 10);

        return top10;
    }
}
