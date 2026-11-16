package modelo;

public enum Dificuldade {
    FACIL("mapas/facil.txt", 5),
    MEDIO("mapas/medio.txt", 10),
    DIFICIL("mapas/dificil.txt", 15);

    private String arquivoMapa;
    private int quantidadeInimigos;

    Dificuldade(String arquivoMapa, int quantidadeInimigos) {
        this.arquivoMapa = arquivoMapa;
        this.quantidadeInimigos = quantidadeInimigos;
    }

    public boolean temSabotador() {
        return this != FACIL;
    }
}
