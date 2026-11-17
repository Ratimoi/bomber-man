package modelo;

public enum Dificuldade {
    FACIL("mapas/facil.txt", 5, 0.30),
    MEDIO("mapas/medio.txt", 10, 0.40),
    DIFICIL("mapas/dificil.txt", 15, 0.50);

    private String arquivoMapa;
    private int quantidadeInimigos;
    private double densidadeCaixotes;

    Dificuldade(String arquivoMapa, int quantidadeInimigos, double densidadeCaixotes) {
        this.arquivoMapa = arquivoMapa;
        this.quantidadeInimigos = quantidadeInimigos;
        this.densidadeCaixotes = densidadeCaixotes;
    }

    public String getArquivoMapa() {
        return arquivoMapa;
    }

    public double getDensidadeCaixotes() {
        return densidadeCaixotes;
    }

    public boolean temSabotador() {
        return this != FACIL;
    }

    public int getQuantidadeInimigos() {
        return quantidadeInimigos;
    }
}
