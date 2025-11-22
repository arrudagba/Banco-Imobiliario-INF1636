package controller;

import model.*;
import controller.observer.*;
import view.JanelaInicialView;
import view.TabuleiroView;

import java.util.*;



/**

 * Controlador principal do jogo.
 *
 * Responsável por coordenar a comunicação entre a View e o ModelFacade.
 * 
 * Requisitos cobertos na 2ª iteração:
 *  - Singleton
 *  - Cria jogadores e sorteia ordem
 *  - Lança dados e envia resultado ao ModelFacade
 *  - Solicita ao ModelFacade o deslocamento e estado do jogo
 *  - Gera e registra cartas Sorte/Revés
 *  - Notifica observadores
 
 */

public class GameController implements ObservadoApi {

    /* ---------- Singleton ---------- */
    private static GameController instancia;
    public static GameController getInstancia() {
        if (instancia == null) instancia = new GameController();
        return instancia;
    }

    /* ---------- Dependências ---------- */
    private final ModelFacade model;
    private final Random rng = new Random();
    private final Map<String, List<ObservadorApi>> observadores = new HashMap<>();

    /* ---------- Estado local ---------- */
    private int dado1 = 1;
    private int dado2 = 1;
    private int soma = 2;

    /* ---------- Construtor privado ---------- */
    private GameController() {
        this.model = ModelFacade.getInstance();
        inicializarEventos();
    }

    /* ---------- Fluxo de telas ---------- */
    public void start() {
        view.Images.carregar();
        new JanelaInicialView(this);
    }

    public void iniciarNovaPartida(String[] nomeJogadores) {
    	
    	String[] nomes = new String[nomeJogadores.length];
        for (int i = 0; i < nomeJogadores.length; i++) {
            nomes[i] = nomeJogadores[i];
        }

        model.iniciarPartida(nomes);
        notifica("estadoAtualizado");

        new TabuleiroView(this).setVisible(true);
    }
    
    public void iniciarPartidaCarregada() {
        notifica("estadoAtualizado");
        new TabuleiroView(this).setVisible(true);
    }

    /* ---------- Dados ---------- */
    public int getDado1() { return dado1; }
    public int getDado2() { return dado2; }
    public int getSoma() {return soma; }
    
    public void rolarDados() {
        int[] valores = model.lancarDados();
        dado1 = valores[0];
        dado2 = valores[1];
        soma = valores[2];
        notifica("novoValorDados");
    }

    /* ---------- Jogada ---------- */
    
    /** Processa a jogada do jogador da vez */
    public void processarJogada() {
        Jogador jogadorAtual = model.getJogadorDaVez();
        
        // Verificar se está na prisão
        if (jogadorAtual.isPreso()) {
            tratarJogadorNaPrisao(jogadorAtual);
        } else {
            tratarJogadaNormal(jogadorAtual);
        }
        
        notifica("estadoAtualizado");
    }
    
    /** Trata jogador que está na prisão */
    private void tratarJogadorNaPrisao(Jogador jogador) {
        // Verificar se tem carta de saída livre
        if (jogador.isCartaSaidaLivre()) {
            // Notifica View para perguntar ao jogador
            notifica("oferecerCartaSaidaLivre");
            return; // View chamará usarCartaSaidaLivre() se jogador aceitar
        }
        
        // Lançar dados para tentar sair
        int[] dados;
        if (modoManual) {
            // View fornecerá os valores via setDadosManuais()
            dados = new int[]{dado1, dado2, dado1 + dado2};
        } else {
            dados = model.lancarDados();
            dado1 = dados[0];
            dado2 = dados[1];
            soma = dados[2];
            notifica("novoValorDados");
        }
        
        // Usar método da CasaPrisao
        Casa casaPrisao = model.getTabuleiro().getCasaPrisao();
        if (casaPrisao instanceof CasaPrisao) {
            boolean conseguiuSair = ((CasaPrisao) casaPrisao).tentarSairComDados(jogador, dados);
            if (conseguiuSair) {
                notifica("saiuPrisaoDupla");
                moverJogador(jogador, dados[2]);
                model.passarVez(false);
                notifica("passouVez");
            } else if (!jogador.isPreso()) {
                // Saiu após 3 tentativas
                notifica("saiuPrisao3Tentativas");
                moverJogador(jogador, dados[2]);
                model.passarVez(false);
                notifica("passouVez");
            } else {
                notifica("tentativaPrisaoFalhou");
                model.passarVez(false);
                notifica("passouVez");
            }
        }
    }
    
    /** Trata jogada normal (fora da prisão) */
    private void tratarJogadaNormal(Jogador jogador) {
        // Lançar dados
        int[] dados;
        if (modoManual) {
            dados = new int[]{dado1, dado2, dado1 + dado2};
        } else {
            dados = model.lancarDados();
            dado1 = dados[0];
            dado2 = dados[1];
            soma = dados[2];
            notifica("novoValorDados");
        }
        
        // Mover jogador
        moverJogador(jogador, dados[2]);
        
        // Verifica duplas
        boolean ehDupla = (dados[0] == dados[1]);
        if (ehDupla) {
            int duplas = jogador.getDuplasConsecutivas() + 1;
            jogador.setDuplasConsecutivas(duplas);
            if (duplas >= 3) {
                // Três duplas seguidas = prisão
                jogador.setPreso(true);
                jogador.setPosicao(10);
                jogador.setDuplasConsecutivas(0);
                notifica("prisao");
                model.passarVez(false);
                notifica("passouVez");
            } else {
                notifica("dupla");
                // Não passa a vez - joga novamente
            }
        } else {
            jogador.setDuplasConsecutivas(0);
            model.passarVez(false);
            notifica("passouVez");
        }
        
        // Verifica fim de jogo
        if (model.fimDoJogo()) {
            notifica("fimDoJogo");
        }
    }
    
    /** Move o jogador e processa a casa */
    private void moverJogador(Jogador jogador, int casas) {
        int posAtual = jogador.getPosicao();
        int novaPos = (posAtual + casas) % model.getTabuleiro().getTamanho();
        jogador.setPosicao(novaPos);
        
        // Verifica se passou pelo início
        if (novaPos < posAtual) {
            jogador.creditar(200);
            notifica("passouInicio");
        }
        
        notifica("novaPosJogador");
        
        Casa casa = model.getTabuleiro().getCasa(novaPos);
        processarCasa(jogador, casa);
    }
    
    /** Processa a casa onde o jogador parou */
    private void processarCasa(Jogador jogador, Casa casa) {
        TipoCasa tipo = casa.getTipo();
        
        switch (tipo) {
            case PROPRIEDADE:
                notifica("cairamPropriedade");
                break;
                
            case COMPANHIA:
                notifica("cairamCompanhia");
                break;
                
            case SORTE_REVES:
                processarCasaSorteReves(jogador);
                break;
                
            case VA_PARA_PRISAO:
                casa.executarAcao(jogador);
                notifica("prisao");
                break;
                
            case IMPOSTO:
                casa.executarAcao(jogador);
                notifica("pagouImposto");
                break;
                
            case RECEBIMENTO:
                casa.executarAcao(jogador);
                notifica("recebeuDividendos");
                break;
                
            case PRISAO:
                casa.executarAcao(jogador);
                break;
                
            case INICIO:
            case LIVRE:
            default:
                break;
        }
    }
    
    /** Processa carta de Sorte/Revés */
    private void processarCasaSorteReves(Jogador jogador) {
        Carta carta = model.sacarCarta();
        if (carta != null) {
            notifica("novaCarta");
            model.aplicarEfeitoCarta(carta);
            
            // Notificações específicas por tipo de carta
            if (carta.getDescricao().contains("prisão")) {
                notifica("cartaPrisao");
            } else if (carta.getDescricao().contains("início")) {
                notifica("cartaInicio");
            } else if (carta.getTipo() == TipoCartas.SAIDA_LIVRE) {
                notifica("cartaSaidaLivre");
            } else if (carta.getValor() != 0) {
                notifica("cartaMonetaria");
            }
        }
    }
    
    /** Usa carta de saída livre (chamado pela View quando jogador aceita) */
    public void usarCartaSaidaLivre() {
        Jogador jogador = model.getJogadorDaVez();
        jogador.setCartaSaidaLivre(false);
        jogador.setPreso(false);
        jogador.setTentativasPrisao(0);
        notifica("usouSaidaLivre");
        tratarJogadaNormal(jogador);
    }
    
    /** Define valores dos dados manualmente (modo manual) */
    public void setDadosManuais(int d1, int d2) {
        dado1 = clampDado(d1);
        dado2 = clampDado(d2);
        soma = dado1 + dado2;
    }
    
    /** Retorna a última carta sacada */
    public Carta getUltimaCarta() {
        return model.getUltimaCartaSacada();
    }

    /* ---------- Auxiliares ---------- */
    private int clampDado(int v) {
        if (v < 1) return 1;
        if (v > 6) return 6;
        return v;
    }

    /* ---------- Acesso à Model ---------- */
    public List<Jogador> getJogadores() { return model.getJogadores(); }
    public Jogador getJogadorDaVez() { return model.getJogadorDaVez(); }
    public Tabuleiro getTabuleiro() { return model.getTabuleiro(); }
    public Carta sacarCarta() { return model.sacarCarta(); }
    public void aplicarEfeitoCarta(Carta carta) { model.aplicarEfeitoCarta(carta); }
    public void passarVez() { model.passarVez(false); }
    
    
    // ----- Propriedades do jogador da vez -----
   
    public String[] getDescricao(int pos) {
        return model.getDescricao(pos);
    }
    
    public String[] getNomesTodasPropriedades() {
        return model.getNomesTodasPropriedades();
    }

    /** Retorna o preço da propriedade pelo nome */
    public int getPrecoPropJogadorDaVez(String nome) {
        CasaPropriedade p = getPropriedadePorNomeJogadorDaVez(nome);
        return (p == null) ? 0 : p.getPreco();
    }

    /** Retorna o titular (nome do dono) da propriedade */
    public String getTitularPropJogadorDaVez(String nome) {
        CasaPropriedade p = getPropriedadePorNomeJogadorDaVez(nome);
        if (p == null || p.getProprietario() == null) return "sem titular";
        return p.getProprietario().getNome();
    }

    /** Retorna o número de casas */
    public int getCasasPropJogadorDaVez(String nome) {
        CasaPropriedade p = getPropriedadePorNomeJogadorDaVez(nome);
        return (p == null) ? 0 : p.getNumCasas();
    }

    /** Retorna o número de hotéis */
    public int getHoteisPropJogadorDaVez(String nome) {
        CasaPropriedade p = getPropriedadePorNomeJogadorDaVez(nome);
        return (p == null || !p.isTemHotel()) ? 0 : 1;
    }

    // ---- helper interno ----
    private CasaPropriedade getPropriedadePorNomeJogadorDaVez(String nome) {
        Jogador j = model.getJogadorDaVez();
        if (j == null) return null;
        for (CasaPropriedade p : j.getPropriedades()) {
            if (p.getNome().equalsIgnoreCase(nome)) return p;
        }
        return null;
    }

    // Salvar partida com JFileChooser
    public java.nio.file.Path salvarJogo() {
        try {
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser(
                javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory()
            );
            fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter("Arquivo de Texto (*.txt)", "txt")
            );
            fileChooser.setDialogTitle("Salvar Partida");
            fileChooser.setSelectedFile(new java.io.File("partida_salva.txt"));
            
            int userSelection = fileChooser.showSaveDialog(null);
            
            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                String caminho = file.getAbsolutePath();
                if (!caminho.endsWith(".txt")) {
                    caminho += ".txt";
                }
                
                boolean sucesso = HistoricoJogo.salvarPartida(caminho, modoManual);
                return sucesso ? java.nio.file.Paths.get(caminho) : null;
            }
            return null;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    // Carregar partida com JFileChooser
    public boolean carregarJogo() {
        try {
            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser(
                javax.swing.filechooser.FileSystemView.getFileSystemView().getHomeDirectory()
            );
            fileChooser.setDialogTitle("Carregar Partida");
            fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter("Arquivo de Texto (*.txt)", "txt")
            );
            
            int userSelection = fileChooser.showOpenDialog(null);
            
            if (userSelection == javax.swing.JFileChooser.APPROVE_OPTION) {
                String caminho = fileChooser.getSelectedFile().getAbsolutePath();
                return HistoricoJogo.carregarPartida(caminho, this);
            }
            return false;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    /** Calcula patrimônios e retorna vencedor */
    public int[] calcularPatrimonios() {
        List<Jogador> jogs = model.getJogadores();
        int[] patrimonios = new int[jogs.size()];
        for (int i = 0; i < jogs.size(); i++) {
            patrimonios[i] = model.calcularPatrimonio(i);
        }
        return patrimonios;
    }
    
    /* - Manipulação de dados */
    
    private boolean modoManual;

    public void setModoManual(boolean valor) {
        this.modoManual = valor;
    }
    
    public boolean isModoManual() {
        return modoManual;
    }
    
    /* - Sistema de observadores */
    private void inicializarEventos() {
        String[] eventos = {
            "novoValorDados",
            "novaPosJogador",
            "novaCarta",
            "passouVez",
            "estadoAtualizado",
            "oferecerCartaSaidaLivre",
            "usouSaidaLivre",
            "saiuPrisaoDupla",
            "saiuPrisao3Tentativas",
            "tentativaPrisaoFalhou",
            "dupla",
            "prisao",
            "passouInicio",
            "cairamPropriedade",
            "cairamCompanhia",
            "pagouImposto",
            "recebeuDividendos",
            "cartaPrisao",
            "cartaInicio",
            "cartaSaidaLivre",
            "cartaMonetaria",
            "fimDoJogo"
        };
        for (String e : eventos) {
            observadores.put(e, new ArrayList<>());
        }
    }

    @Override
    public void registraObservador(String evento, ObservadorApi o) {
        if (observadores.containsKey(evento)) {
            observadores.get(evento).add(o);
        }
    }

    @Override
    public void removeObservador(String evento, ObservadorApi o) {
        if (observadores.containsKey(evento)) {
            observadores.get(evento).remove(o);
        }
    }

    @Override
    public void notifica(String evento) {
        if (!observadores.containsKey(evento)) return;
        for (ObservadorApi o : observadores.get(evento)) {
            o.atualiza(evento);
        }
    }
    
    
}
