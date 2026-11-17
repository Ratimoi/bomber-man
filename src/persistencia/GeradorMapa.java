package persistencia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import excecoes.ArquivoException;
import modelo.Celula;
import modelo.Dificuldade;
import modelo.Posicao;
import modelo.Tabuleiro;
import modelo.blocos.Bloco;
import modelo.blocos.BlocoDestrutivo;
import modelo.blocos.BlocoIndestrutivo;
import modelo.inimigos.Inimigo;
import modelo.inimigos.InimigoComum;
import modelo.inimigos.InimigoRastreador;
import modelo.inimigos.InimigoSabotador;
import modelo.itens.Item;
import modelo.itens.PortaSaida;
import modelo.itens.PowerUpBomba;
import modelo.itens.PowerUpRaio;

public class GeradorMapa {
    private static final int DISTANCIA_MINIMA_INIMIGO = 5;

    private final Random random;

    public GeradorMapa() {
        this.random = new Random();
    }

    public GeradorMapa(long semente) {
        this.random = new Random(semente);
    }

    public Tabuleiro carregarArquivo(String caminho) throws ArquivoException {
        ArrayList<String> linhas = new ArrayList<>();

        try (BufferedReader leitor = new BufferedReader(new FileReader(caminho, StandardCharsets.UTF_8))) {
            String linha;

            while ((linha = leitor.readLine()) != null) {
                if (linhas.isEmpty() && linha.startsWith("﻿")) {
                    linha = linha.substring(1);
                }

                if (!linha.isBlank()) {
                    linhas.add(linha);
                }
            }
        } catch (IOException e) {
            throw new ArquivoException("nao foi possivel ler o mapa " + caminho);
        }

        int tamanho = linhas.size();

        if (tamanho == 0) {
            throw new ArquivoException("o mapa " + caminho + " esta vazio");
        }

        Tabuleiro tab = new Tabuleiro(tamanho);

        for (int linha = 0; linha < tamanho; linha++) {
            String texto = linhas.get(linha);

            if (texto.length() != tamanho) {
                throw new ArquivoException("o mapa " + caminho + " nao e quadrado (linha " + (linha + 1) + ")");
            }

            for (int coluna = 0; coluna < tamanho; coluna++) {
                Posicao posicao = new Posicao(coluna, linha);
                Celula celula = tab.getCelula(posicao);

                switch (texto.charAt(coluna)) {
                    case '█':
                        celula.setBloco(new BlocoIndestrutivo(posicao, false));
                        break;
                    case '#':
                        celula.setBloco(new BlocoDestrutivo(posicao, false, null));
                        break;
                    case '.':
                    case ' ':
                        break;
                    default:
                        throw new ArquivoException("caractere invalido no mapa " + caminho + " (linha " + (linha + 1) + ", coluna " + (coluna + 1) + ")");
                }
            }
        }

        liberarInicio(tab);

        return tab;
    }

    public ArrayList<Inimigo> espalharInimigos(Tabuleiro tab, Dificuldade dif) {
        ArrayList<Posicao> livres = new ArrayList<>();

        for (int linha = 0; linha < tab.getTamanho(); linha++) {
            for (int coluna = 0; coluna < tab.getTamanho(); coluna++) {
                Posicao posicao = new Posicao(coluna, linha);
                Celula celula = tab.getCelula(posicao);

                if (celula.isTransponivel() && celula.getItem() == null && coluna + linha >= DISTANCIA_MINIMA_INIMIGO) {
                    livres.add(posicao);
                }
            }
        }

        Collections.shuffle(livres, random);

        int quantidade = Math.min(dif.getQuantidadeInimigos(), livres.size());
        ArrayList<Inimigo> inimigos = new ArrayList<>();

        for (int i = 0; i < quantidade; i++) {
            inimigos.add(criarInimigo(i, livres.get(i), dif));
        }

        return inimigos;
    }

    public void espalharCaixotes(Tabuleiro tab, Dificuldade dif) {
        for (int linha = 0; linha < tab.getTamanho(); linha++) {
            for (int coluna = 0; coluna < tab.getTamanho(); coluna++) {
                Posicao posicao = new Posicao(coluna, linha);
                Celula celula = tab.getCelula(posicao);

                if (celula.getBloco() == null && coluna + linha > 2 && random.nextDouble() < dif.getDensidadeCaixotes()) {
                    celula.setBloco(new BlocoDestrutivo(posicao, false, null));
                }
            }
        }
    }

    public void espalharItens(Tabuleiro tab) {
        ArrayList<Posicao> caixotes = new ArrayList<>();

        for (int linha = 0; linha < tab.getTamanho(); linha++) {
            for (int coluna = 0; coluna < tab.getTamanho(); coluna++) {
                Posicao posicao = new Posicao(coluna, linha);
                Bloco bloco = tab.getCelula(posicao).getBloco();

                if (bloco instanceof BlocoDestrutivo && ((BlocoDestrutivo) bloco).getItemOculto() == null) {
                    caixotes.add(posicao);
                }
            }
        }

        if (caixotes.isEmpty()) {
            return;
        }

        Collections.shuffle(caixotes, random);

        esconder(tab, caixotes.get(0), new PortaSaida(caixotes.get(0)));

        int quantidade = Math.min(caixotes.size() - 1, Math.max(2, caixotes.size() / 6));

        for (int i = 1; i <= quantidade; i++) {
            Posicao posicao = caixotes.get(i);
            Item item = (i % 2 == 1) ? new PowerUpBomba(posicao) : new PowerUpRaio(posicao);

            esconder(tab, posicao, item);
        }
    }

    public Tabuleiro gerar(int tamanho, Dificuldade dif) {
        Tabuleiro tab = new Tabuleiro(tamanho);

        for (int linha = 1; linha < tamanho; linha += 2) {
            for (int coluna = 1; coluna < tamanho; coluna += 2) {
                Posicao posicao = new Posicao(coluna, linha);

                tab.getCelula(posicao).setBloco(new BlocoIndestrutivo(posicao, false));
            }
        }

        espalharCaixotes(tab, dif);
        espalharItens(tab);

        return tab;
    }

    private Inimigo criarInimigo(int indice, Posicao posicao, Dificuldade dif) {
        int tipo = indice % (dif.temSabotador() ? 3 : 2);

        if (tipo == 1) {
            return new InimigoRastreador(posicao);
        }

        if (tipo == 2) {
            return new InimigoSabotador(posicao);
        }

        return new InimigoComum(posicao);
    }

    private void esconder(Tabuleiro tab, Posicao posicao, Item item) {
        tab.getCelula(posicao).setBloco(new BlocoDestrutivo(posicao, false, item));
    }

    private void liberarInicio(Tabuleiro tab) {
        Posicao[] inicio = { new Posicao(0, 0), new Posicao(1, 0), new Posicao(0, 1) };

        for (Posicao posicao : inicio) {
            Celula celula = tab.getCelula(posicao);

            if (celula != null) {
                celula.removerBloco();
            }
        }
    }
}
