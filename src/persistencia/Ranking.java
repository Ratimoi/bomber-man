package persistencia;

import java.util.ArrayList;
import java.util.Collections;

public class Ranking {
    private ArrayList<RegistroRanking> registros;

    public Ranking() {
        this.registros = new ArrayList<>();
    }

    public void adicionar(RegistroRanking r) {
        registros.add(r);
        Collections.sort(registros);
    }

    public ArrayList<RegistroRanking> getRegistros() {
        return new ArrayList<>(registros);
    }

    public ArrayList<RegistroRanking> getTop10() {
        int limite = Math.min(10, registros.size());

        return new ArrayList<>(registros.subList(0, limite));
    }
}
