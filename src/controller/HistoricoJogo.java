package controller;

import model.ModelFacade;
import java.io.*;
import java.nio.file.*;

/**
 * Classe responsável por salvar e carregar partidas
 */
public class HistoricoJogo {
    
    /**
     * Salva o estado atual da partida em arquivo texto
     * @param caminhoArquivo Caminho completo do arquivo
     * @param modoManual Se o jogo está em modo manual (god mode)
     * @return true se salvou com sucesso
     */
    public static boolean salvarPartida(String caminhoArquivo, boolean modoManual) {
        try {
            ModelFacade model = ModelFacade.getInstance();
            ModelFacade.SaveState estado = model.snapshot();
            
            StringBuilder sb = new StringBuilder();
            
            // Linha 1: Modo de jogo
            sb.append("MODO_MANUAL=").append(modoManual ? "true" : "false").append("\n");
            
            // Linha 2: Número de jogadores
            sb.append("NUM_JOGADORES=").append(estado.nJogadores).append("\n");
            
            // Linha 3: Jogador da vez
            sb.append("JOGADOR_DA_VEZ=").append(estado.currentIndex).append("\n");
            
            // Linhas seguintes: dados de cada jogador
            for (int i = 0; i < estado.players.size(); i++) {
                ModelFacade.PlayerState p = estado.players.get(i);
                sb.append("JOGADOR_").append(i).append("=");
                sb.append(p.nome).append("|");
                sb.append(p.saldo).append("|");
                sb.append(p.posicao).append("|");
                sb.append(p.preso ? "1" : "0").append("|");
                sb.append(p.cartaSaidaLivre ? "1" : "0").append("|");
                sb.append(p.tentativasPrisao).append("|");
                sb.append(p.duplasConsecutivas).append("|");
                sb.append(String.join(";", p.propriedades));
                sb.append("\n");
            }
            
            Files.writeString(Paths.get(caminhoArquivo), sb.toString());
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Carrega uma partida salva
     * @param caminhoArquivo Caminho do arquivo
     * @param controller Referência ao controller para iniciar o jogo
     * @return true se carregou com sucesso
     */
    public static boolean carregarPartida(String caminhoArquivo, GameController controller) {
        try {
            String conteudo = Files.readString(Paths.get(caminhoArquivo));
            String[] linhas = conteudo.split("\n");
            
            boolean modoManual = false;
            int numJogadores = 0;
            int jogadorDaVez = 0;
            String[] nomes = null;
            ModelFacade.SaveState estado = new ModelFacade.SaveState();
            
            for (String linha : linhas) {
                if (linha.startsWith("MODO_MANUAL=")) {
                    modoManual = linha.substring(12).equals("true");
                }
                else if (linha.startsWith("NUM_JOGADORES=")) {
                    numJogadores = Integer.parseInt(linha.substring(14));
                    nomes = new String[numJogadores];
                    estado.nJogadores = numJogadores;
                }
                else if (linha.startsWith("JOGADOR_DA_VEZ=")) {
                    jogadorDaVez = Integer.parseInt(linha.substring(15));
                    estado.currentIndex = jogadorDaVez;
                }
                else if (linha.startsWith("JOGADOR_")) {
                    int idx = Integer.parseInt(linha.substring(8, linha.indexOf('=')));
                    String dados = linha.substring(linha.indexOf('=') + 1);
                    String[] partes = dados.split("\\|");
                    
                    ModelFacade.PlayerState ps = new ModelFacade.PlayerState();
                    ps.nome = partes[0];
                    ps.saldo = Integer.parseInt(partes[1]);
                    ps.posicao = Integer.parseInt(partes[2]);
                    ps.preso = partes[3].equals("1");
                    ps.cartaSaidaLivre = partes[4].equals("1");
                    ps.tentativasPrisao = Integer.parseInt(partes[5]);
                    ps.duplasConsecutivas = Integer.parseInt(partes[6]);
                    
                    if (partes.length > 7 && !partes[7].isEmpty()) {
                        String[] props = partes[7].split(";");
                        for (String prop : props) {
                            ps.propriedades.add(prop);
                        }
                    }
                    
                    nomes[idx] = ps.nome;
                    estado.players.add(ps);
                }
            }
            
            if (nomes == null || numJogadores == 0) {
                return false;
            }
            
            // Inicializar partida com dados carregados
            controller.setModoManual(modoManual);
            ModelFacade model = ModelFacade.getInstance();
            model.iniciarPartida(nomes, jogadorDaVez);
            model.restaurarEstado(estado);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
