package modelo.itens;

import modelo.Jogo;
import modelo.Posicao;
import modelo.personagens.Jogador;

public class PortaSaida extends Item {
    private boolean aberta;

    public PortaSaida(Posicao posicao) {
        super(posicao);
        aberta = false;
    }

    public void abrir() {
        aberta = true;
    }

    public boolean isAberta() {
        return aberta;
    }

    @Override
    public void coletar(Jogador j, Jogo jogo) {
        if (jogo.todosInimigosDerrotados()) {
            abrir();
            jogo.vencer();
        }
    }

    @Override
    public char getSimbolo() {
        return 'D';
    }
}
