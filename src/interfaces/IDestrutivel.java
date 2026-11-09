package interfaces;

import modelo.Tabuleiro;

public interface IDestrutivel {
    public boolean isDestruido();
    public void receberDano(Tabuleiro tab);
}
