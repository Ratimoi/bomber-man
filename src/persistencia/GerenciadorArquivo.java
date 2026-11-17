package persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import excecoes.ArquivoException;
import modelo.Celula;
import modelo.Dificuldade;
import modelo.Jogo;
import modelo.Posicao;
import modelo.Tabuleiro;
import modelo.blocos.Bloco;
import modelo.blocos.BlocoDestrutivo;
import modelo.blocos.BlocoIndestrutivo;
import modelo.bombas.Bomba;
import modelo.bombas.BombaArmadilha;
import modelo.inimigos.Inimigo;
import modelo.inimigos.InimigoComum;
import modelo.inimigos.InimigoRastreador;
import modelo.inimigos.InimigoSabotador;
import modelo.itens.Item;
import modelo.itens.PortaSaida;
import modelo.itens.PowerUpBomba;
import modelo.itens.PowerUpRaio;
import modelo.personagens.Jogador;

public class GerenciadorArquivo {
    public static final String CAMINHO_RANKING = "ranking.txt";
    public static final String CAMINHO_SAVE = "saves/jogo_salvo.txt";

    private static final String CABECALHO = "BOMBERMAN_SAVE_1";

    public Jogo carregarJogo(String caminho) throws ArquivoException {
        ArrayList<String> linhas = lerLinhas(caminho);

        if (linhas.isEmpty() || !linhas.get(0).equals(CABECALHO)) {
            throw new ArquivoException(caminho + " nao e um jogo salvo valido");
        }

        try {
            int i = 1;

            Dificuldade dificuldade = Dificuldade.valueOf(valor(linhas.get(i++), "dificuldade"));
            long semente = Long.parseLong(valor(linhas.get(i++), "semente"));
            int turno = Integer.parseInt(valor(linhas.get(i++), "turno"));
            int dicas = Integer.parseInt(valor(linhas.get(i++), "dicas"));
            boolean debug = Boolean.parseBoolean(valor(linhas.get(i++), "debug"));
            long tempo = Long.parseLong(valor(linhas.get(i++), "tempo"));
            int tamanho = Integer.parseInt(valor(linhas.get(i++), "tamanho"));
            String[] dados = valor(linhas.get(i++), "jogador").split(";", 8);

            Tabuleiro tab = new Tabuleiro(tamanho);
            Posicao posicaoJogador = new Posicao(Integer.parseInt(dados[0]), Integer.parseInt(dados[1]));

            if (!tab.posicaoValida(posicaoJogador)) {
                throw new ArquivoException("posicao do jogador fora do tabuleiro em " + caminho);
            }

            String nome = dados[7];
            Jogador jogador = new Jogador(posicaoJogador, nome, Integer.parseInt(dados[2]), Integer.parseInt(dados[3]),
                    Integer.parseInt(dados[4]), Integer.parseInt(dados[5]), Integer.parseInt(dados[6]));
            Jogo jogo = new Jogo(dificuldade, nome);

            ArrayList<Bomba> bombas = new ArrayList<>();
            ArrayList<Inimigo> inimigos = new ArrayList<>();

            for (; i < linhas.size(); i++) {
                if (linhas.get(i).isBlank()) {
                    continue;
                }

                String[] c = linhas.get(i).split(";");
                Posicao posicao = posicaoDe(tab, c[1], c[2], caminho);
                Celula celula = tab.getCelula(posicao);

                switch (c[0]) {
                    case "BLOCO_INDESTRUTIVEL":
                        celula.setBloco(new BlocoIndestrutivo(posicao, false));
                        break;
                    case "BLOCO_DESTRUTIVEL":
                        Item oculto = c[3].equals("-") ? null : criarItem(c[3].charAt(0), posicao);

                        celula.setBloco(new BlocoDestrutivo(posicao, false, oculto));
                        break;
                    case "ARMADILHA":
                        Bomba original = new Bomba(posicao, jogador, jogo, Integer.parseInt(c[4]), Integer.parseInt(c[5]), Integer.parseInt(c[6]));

                        celula.setBloco(new BombaArmadilha(posicao, original, c[3].charAt(0)));
                        break;
                    case "BOMBA":
                        Bomba bomba = new Bomba(posicao, jogador, jogo, Integer.parseInt(c[3]), Integer.parseInt(c[4]), Integer.parseInt(c[5]));

                        celula.setBomba(bomba);
                        bombas.add(bomba);
                        break;
                    case "ITEM":
                        Item item = criarItem(c[3].charAt(0), posicao);

                        item.revelar();
                        celula.setItem(item);
                        break;
                    case "INIMIGO":
                        inimigos.add(criarInimigo(c[3].charAt(0), posicao));
                        break;
                    default:
                        throw new ArquivoException("registro desconhecido '" + c[0] + "' em " + caminho);
                }
            }

            jogo.restaurar(tab, jogador, inimigos, bombas, turno, dicas, debug, semente, tempo);

            return jogo;
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new ArquivoException(caminho + " esta corrompido");
        }
    }

    public Ranking carregarRanking() throws ArquivoException {
        Ranking ranking = new Ranking();

        if (!new File(CAMINHO_RANKING).exists()) {
            return ranking;
        }

        try {
            for (String linha : lerLinhas(CAMINHO_RANKING)) {
                if (linha.isBlank()) {
                    continue;
                }

                String[] campos = linha.split(";");

                if (campos.length != 3) {
                    throw new ArquivoException("linha invalida no ranking");
                }

                ranking.adicionar(new RegistroRanking(campos[0], Integer.parseInt(campos[1]), Long.parseLong(campos[2])));
            }
        } catch (NumberFormatException e) {
            throw new ArquivoException("ranking corrompido");
        }

        return ranking;
    }

    public void salvarJogo(Jogo jogo, String caminho) throws ArquivoException {
        File arquivo = new File(caminho);

        criarPasta(arquivo);

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo, StandardCharsets.UTF_8))) {
            Jogador jogador = jogo.getJogador();
            Tabuleiro tab = jogo.getTabuleiro();

            escrever(escritor, CABECALHO);
            escrever(escritor, "dificuldade=" + jogo.getDificuldade().name());
            escrever(escritor, "semente=" + jogo.getSemente());
            escrever(escritor, "turno=" + jogo.getTurno());
            escrever(escritor, "dicas=" + jogo.getDicasRestantes());
            escrever(escritor, "debug=" + jogo.isModoDebug());
            escrever(escritor, "tempo=" + jogo.getTempoSegundos());
            escrever(escritor, "tamanho=" + tab.getTamanho());
            escrever(escritor, "jogador=" + jogador.getPosicao().getColuna() + ";" + jogador.getPosicao().getLinha() + ";"
                    + jogador.getVidas() + ";" + jogador.getMaxBombas() + ";" + jogador.getBombasAtivas() + ";"
                    + jogador.getRaioExplosao() + ";" + jogador.getPontuacao() + ";" + limparNome(jogador.getNome()));

            for (int linha = 0; linha < tab.getTamanho(); linha++) {
                for (int coluna = 0; coluna < tab.getTamanho(); coluna++) {
                    escreverCelula(escritor, tab.getCelula(new Posicao(coluna, linha)), coluna, linha);
                }
            }

            for (Inimigo inimigo : jogo.getInimigos()) {
                if (!inimigo.isDestruido()) {
                    escrever(escritor, "INIMIGO;" + inimigo.getPosicao().getColuna() + ";" + inimigo.getPosicao().getLinha() + ";" + inimigo.getSimbolo());
                }
            }
        } catch (IOException e) {
            throw new ArquivoException("nao foi possivel salvar em " + caminho);
        }
    }

    public void salvarRanking(Ranking r) throws ArquivoException {
        File arquivo = new File(CAMINHO_RANKING);

        criarPasta(arquivo);

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(arquivo, StandardCharsets.UTF_8))) {
            for (RegistroRanking registro : r.getRegistros()) {
                escrever(escritor, registro.getNome() + ";" + registro.getPontuacao() + ";" + registro.getTempoSegundos());
            }
        } catch (IOException e) {
            throw new ArquivoException("nao foi possivel salvar o ranking");
        }
    }

    private void criarPasta(File arquivo) {
        File pasta = arquivo.getParentFile();

        if (pasta != null) {
            pasta.mkdirs();
        }
    }

    private Inimigo criarInimigo(char simbolo, Posicao posicao) {
        return switch (simbolo) {
            case 'C' -> new InimigoComum(posicao);
            case 'R' -> new InimigoRastreador(posicao);
            case 'S' -> new InimigoSabotador(posicao);
            default -> throw new IllegalArgumentException("inimigo desconhecido: " + simbolo);
        };
    }

    private Item criarItem(char simbolo, Posicao posicao) {
        return switch (simbolo) {
            case 'U' -> new PowerUpBomba(posicao);
            case 'O' -> new PowerUpRaio(posicao);
            case 'D' -> new PortaSaida(posicao);
            default -> throw new IllegalArgumentException("item desconhecido: " + simbolo);
        };
    }

    private void escrever(BufferedWriter escritor, String linha) throws IOException {
        escritor.write(linha);
        escritor.newLine();
    }

    private void escreverCelula(BufferedWriter escritor, Celula celula, int coluna, int linha) throws IOException {
        String local = coluna + ";" + linha;
        Bloco bloco = celula.getBloco();

        if (bloco instanceof BlocoIndestrutivo) {
            escrever(escritor, "BLOCO_INDESTRUTIVEL;" + local);
        } else if (bloco instanceof BlocoDestrutivo) {
            Item oculto = ((BlocoDestrutivo) bloco).getItemOculto();

            escrever(escritor, "BLOCO_DESTRUTIVEL;" + local + ";" + (oculto == null ? "-" : String.valueOf(oculto.getSimbolo())));
        } else if (bloco instanceof BombaArmadilha) {
            Bomba original = ((BombaArmadilha) bloco).getBombaOriginal();

            escrever(escritor, "ARMADILHA;" + local + ";" + bloco.getSimbolo() + ";" + original.getRaio() + ";"
                    + original.getTempoRestante() + ";" + original.getTempoTotal());
        }

        if (celula.getBomba() != null) {
            Bomba bomba = celula.getBomba();

            escrever(escritor, "BOMBA;" + local + ";" + bomba.getRaio() + ";" + bomba.getTempoRestante() + ";" + bomba.getTempoTotal());
        }

        if (celula.getItem() != null) {
            escrever(escritor, "ITEM;" + local + ";" + celula.getItem().getSimbolo());
        }
    }

    private ArrayList<String> lerLinhas(String caminho) throws ArquivoException {
        ArrayList<String> linhas = new ArrayList<>();

        try (BufferedReader leitor = new BufferedReader(new FileReader(caminho, StandardCharsets.UTF_8))) {
            String linha;

            while ((linha = leitor.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            throw new ArquivoException("nao foi possivel ler " + caminho);
        }

        return linhas;
    }

    private String limparNome(String nome) {
        return nome.replace(';', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    private Posicao posicaoDe(Tabuleiro tab, String coluna, String linha, String caminho) throws ArquivoException {
        Posicao posicao = new Posicao(Integer.parseInt(coluna), Integer.parseInt(linha));

        if (!tab.posicaoValida(posicao)) {
            throw new ArquivoException("posicao fora do tabuleiro em " + caminho);
        }

        return posicao;
    }

    private String valor(String linha, String chave) throws ArquivoException {
        if (!linha.startsWith(chave + "=")) {
            throw new ArquivoException("campo '" + chave + "' ausente no jogo salvo");
        }

        return linha.substring(chave.length() + 1);
    }
}
