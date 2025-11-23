package controller;

import model.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

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
            Tabuleiro tabuleiro = model.getTabuleiro();
            List<Jogador> jogadores = model.getJogadores();
            
            StringBuilder sb = new StringBuilder();
            
            // ===== CABEÇALHO =====
            sb.append("# BANCO IMOBILIÁRIO - PARTIDA SALVA\n");
            sb.append("# Arquivo gerado automaticamente\n\n");
            
            // ===== CONFIGURAÇÕES DO JOGO =====
            sb.append("[CONFIGURACOES]\n");
            sb.append("MODO_MANUAL=").append(modoManual ? "1" : "0").append("\n");
            sb.append("NUM_JOGADORES=").append(jogadores.size()).append("\n");
            sb.append("JOGADOR_DA_VEZ=").append(model.getJogadorDaVezIndex()).append("\n\n");
            
            // ===== DADOS DOS JOGADORES =====
            sb.append("[JOGADORES]\n");
            for (int i = 0; i < jogadores.size(); i++) {
                Jogador j = jogadores.get(i);
                sb.append("JOGADOR_").append(i).append("=");
                sb.append(j.getNome()).append("|");
                sb.append(j.getSaldo()).append("|");
                sb.append(j.getPosicao()).append("|");
                sb.append(j.isPreso() ? "1" : "0").append("|");
                sb.append(j.isCartaSaidaLivre() ? "1" : "0").append("|");
                sb.append(j.getTentativasPrisao()).append("|");
                sb.append(j.getDuplasConsecutivas()).append("\n");
            }
            sb.append("\n");
            
            // ===== PROPRIEDADES =====
            sb.append("[PROPRIEDADES]\n");
            sb.append("# Formato: POSICAO|NOME|PRECO|PROPRIETARIO|NUM_CASAS|TEM_HOTEL\n");
            for (int pos = 0; pos < tabuleiro.getTamanho(); pos++) {
                Casa casa = tabuleiro.getCasa(pos);
                if (casa instanceof CasaPropriedade) {
                    CasaPropriedade prop = (CasaPropriedade) casa;
                    sb.append("PROP_").append(pos).append("=");
                    sb.append(pos).append("|");
                    sb.append(prop.getNome()).append("|");
                    sb.append(prop.getPreco()).append("|");
                    
                    // Nome do proprietário ou "NENHUM"
                    if (prop.getProprietario() != null) {
                        sb.append(prop.getProprietario().getNome());
                    } else {
                        sb.append("NENHUM");
                    }
                    sb.append("|");
                    sb.append(prop.getNumCasas()).append("|");
                    sb.append(prop.isTemHotel() ? "1" : "0").append("\n");
                }
            }
            sb.append("\n");
            
            // ===== COMPANHIAS =====
            sb.append("[COMPANHIAS]\n");
            sb.append("# Formato: POSICAO|NOME|PRECO|PROPRIETARIO\n");
            for (int pos = 0; pos < tabuleiro.getTamanho(); pos++) {
                Casa casa = tabuleiro.getCasa(pos);
                if (casa instanceof CasaCompanhia) {
                    CasaCompanhia comp = (CasaCompanhia) casa;
                    sb.append("COMP_").append(pos).append("=");
                    sb.append(pos).append("|");
                    sb.append(comp.getNome()).append("|");
                    sb.append(comp.getPreco()).append("|");
                    
                    // Nome do proprietário ou "NENHUM"
                    if (comp.getProprietario() != null) {
                        sb.append(comp.getProprietario().getNome());
                    } else {
                        sb.append("NENHUM");
                    }
                    sb.append("\n");
                }
            }
            sb.append("\n");
            
            // ===== CARTAS ESPECIAIS =====
            sb.append("[CARTAS_ESPECIAIS]\n");
            sb.append("# Jogadores com carta de saída livre da prisão\n");
            for (int i = 0; i < jogadores.size(); i++) {
                Jogador j = jogadores.get(i);
                if (j.isCartaSaidaLivre()) {
                    sb.append("CARTA_SAIDA_LIVRE=").append(j.getNome()).append("\n");
                }
            }
            sb.append("\n");
            
            // ===== ESTATÍSTICAS =====
            sb.append("[ESTATISTICAS]\n");
            for (int i = 0; i < jogadores.size(); i++) {
                Jogador j = jogadores.get(i);
                sb.append("STAT_").append(i).append("=");
                sb.append(j.getNome()).append("|");
                sb.append("Propriedades: ").append(j.getPropriedades().size()).append("|");
                sb.append("Posição: ").append(j.getPosicao()).append("|");
                sb.append("Status: ").append(j.isPreso() ? "PRESO" : "LIVRE").append("\n");
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
            int[] saldos = null;
            int[] posicoes = null;
            boolean[] presos = null;
            boolean[] cartasSaidaLivre = null;
            int[] tentativasPrisao = null;
            int[] duplasConsecutivas = null;
            
            String secaoAtual = "";
            
            for (String linha : linhas) {
                linha = linha.trim();
                
                // Ignorar comentários e linhas vazias
                if (linha.isEmpty() || linha.startsWith("#")) continue;
                
                // Detectar seções
                if (linha.startsWith("[")) {
                    secaoAtual = linha;
                    continue;
                }
                
                // Processar configurações
                if (secaoAtual.equals("[CONFIGURACOES]")) {
                    if (linha.startsWith("MODO_MANUAL=")) {
                        modoManual = linha.substring(12).equals("1");
                    }
                    else if (linha.startsWith("NUM_JOGADORES=")) {
                        numJogadores = Integer.parseInt(linha.substring(14));
                        nomes = new String[numJogadores];
                        saldos = new int[numJogadores];
                        posicoes = new int[numJogadores];
                        presos = new boolean[numJogadores];
                        cartasSaidaLivre = new boolean[numJogadores];
                        tentativasPrisao = new int[numJogadores];
                        duplasConsecutivas = new int[numJogadores];
                    }
                    else if (linha.startsWith("JOGADOR_DA_VEZ=")) {
                        jogadorDaVez = Integer.parseInt(linha.substring(15));
                    }
                }
                
                // Processar jogadores
                else if (secaoAtual.equals("[JOGADORES]") && linha.startsWith("JOGADOR_")) {
                    int idx = Integer.parseInt(linha.substring(8, linha.indexOf('=')));
                    String dados = linha.substring(linha.indexOf('=') + 1);
                    String[] partes = dados.split("\\|");
                    
                    nomes[idx] = partes[0];
                    saldos[idx] = Integer.parseInt(partes[1]);
                    posicoes[idx] = Integer.parseInt(partes[2]);
                    presos[idx] = partes[3].equals("1");
                    cartasSaidaLivre[idx] = partes[4].equals("1");
                    tentativasPrisao[idx] = Integer.parseInt(partes[5]);
                    duplasConsecutivas[idx] = Integer.parseInt(partes[6]);
                }
            }
            
            if (nomes == null || numJogadores == 0) {
                return false;
            }
            
            // IMPORTANTE: Configurar modo manual ANTES de inicializar a partida
            controller.setModoManual(modoManual);
            
            // Inicializar partida
            ModelFacade model = ModelFacade.getInstance();
            model.iniciarPartida(nomes, jogadorDaVez);
            
            // Restaurar estados dos jogadores
            List<Jogador> jogadores = model.getJogadores();
            for (int i = 0; i < numJogadores && i < jogadores.size(); i++) {
                Jogador j = jogadores.get(i);
                
                // Ajustar saldo
                int diferenca = saldos[i] - j.getSaldo();
                if (diferenca > 0) {
                    j.creditar(diferenca);
                } else if (diferenca < 0) {
                    j.debitar(Math.abs(diferenca));
                }
                
                j.setPosicao(posicoes[i]);
                j.setPreso(presos[i]);
                j.setCartaSaidaLivre(cartasSaidaLivre[i]);
                j.setTentativasPrisao(tentativasPrisao[i]);
                j.setDuplasConsecutivas(duplasConsecutivas[i]);
            }
            
            // Restaurar propriedades e companhias
            Tabuleiro tabuleiro = model.getTabuleiro();
            secaoAtual = "";
            
            for (String linha : linhas) {
                linha = linha.trim();
                if (linha.isEmpty() || linha.startsWith("#")) continue;
                if (linha.startsWith("[")) {
                    secaoAtual = linha;
                    continue;
                }
                
                // Restaurar propriedades
                if (secaoAtual.equals("[PROPRIEDADES]") && linha.startsWith("PROP_")) {
                    String dados = linha.substring(linha.indexOf('=') + 1);
                    String[] partes = dados.split("\\|");
                    
                    int pos = Integer.parseInt(partes[0]);
                    String nomeProprietario = partes[3];
                    int numCasas = Integer.parseInt(partes[4]);
                    boolean temHotel = partes[5].equals("1");
                    
                    Casa casa = tabuleiro.getCasa(pos);
                    if (casa instanceof CasaPropriedade && !nomeProprietario.equals("NENHUM")) {
                        CasaPropriedade prop = (CasaPropriedade) casa;
                        
                        // Encontrar jogador proprietário
                        for (Jogador j : jogadores) {
                            if (j.getNome().equals(nomeProprietario)) {
                                prop.setProprietario(j);
                                j.addPropriedade(prop);
                                
                                // Restaurar casas e hotéis
                                for (int k = 0; k < numCasas; k++) {
                                    prop.construirCasa();
                                }
                                if (temHotel) {
                                    prop.construirHotel();
                                }
                                break;
                            }
                        }
                    }
                }
                
                // Restaurar companhias
                else if (secaoAtual.equals("[COMPANHIAS]") && linha.startsWith("COMP_")) {
                    String dados = linha.substring(linha.indexOf('=') + 1);
                    String[] partes = dados.split("\\|");
                    
                    int pos = Integer.parseInt(partes[0]);
                    String nomeProprietario = partes[3];
                    
                    Casa casa = tabuleiro.getCasa(pos);
                    if (casa instanceof CasaCompanhia && !nomeProprietario.equals("NENHUM")) {
                        CasaCompanhia comp = (CasaCompanhia) casa;
                        
                        // Encontrar jogador proprietário
                        for (Jogador j : jogadores) {
                            if (j.getNome().equals(nomeProprietario)) {
                                comp.setProprietario(j);
                                break;
                            }
                        }
                    }
                }
                
                // Restaurar cartas especiais
                else if (secaoAtual.equals("[CARTAS_ESPECIAIS]") && linha.startsWith("CARTA_SAIDA_LIVRE=")) {
                    String nomeJogador = linha.substring(18); // Remove "CARTA_SAIDA_LIVRE="
                    
                    // Encontrar jogador e dar a carta
                    for (Jogador j : jogadores) {
                        if (j.getNome().equals(nomeJogador)) {
                            j.setCartaSaidaLivre(true);
                            break;
                        }
                    }
                }
            }
            
            // Verificar e tratar jogadores com saldo negativo após carregar
            Banco banco = model.getBanco();
            for (Jogador j : jogadores) {
                if (j.getSaldo() < 0) {
                    j.tratarFalencia(banco);
                }
            }
            
            // Notificar View para atualizar interface com o jogador correto
            controller.notifica("estadoAtualizado");
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
