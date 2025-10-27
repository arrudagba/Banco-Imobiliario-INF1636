package controller;

import model.*;
import controller.observer.*;
import view.JanelaInicialView;
import view.TabuleiroView;

import java.io.File;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import Controller.Historico;

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
        // model.inicializarBaralhoChance();
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
    public void processarJogadaComValores(int d1, int d2, int soma) {
        dado1 = clampDado(d1);
        dado2 = clampDado(d2);

        int[] dados = new int[]{dado1, dado2, dado1 + dado2};
        model.moverJogador(dados);

        // Verifica se caiu em casa de Sorte/Revés
        Jogador jogador = model.getJogadorDaVez();
        Casa casa = model.getTabuleiro().getCasa(jogador.getPosicao());

        if (casa.getTipo() == TipoCasa.SORTE_REVES) {
            Carta carta = model.sacarCarta();
            if (carta != null) {
                notifica("novaCarta");
            }
        }

        notifica("novaPosJogador");
        model.passarVez(false);
        notifica("passouVez");
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
    public Carta getUltimaCartaComprada() { return model.sacarCarta(); }
    
    
    // ----- Propriedades do jogador da vez -----
    public String[] getNomesPropriedadesJogadorDaVez() {
        Jogador j = model.getJogadorDaVez();
        if (j == null || j.getPropriedades().isEmpty()) return new String[0];
        return j.getPropriedades().stream()
                .map(CasaPropriedade::getNome)
                .toArray(String[]::new);
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

    public void salvarPartida() {
    	JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    	fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
    	fileChooser.setDialogTitle("Salvar partida");
    	fileChooser.setSelectedFile(new File("partida_salva"));
    	int userSelection = fileChooser.showSaveDialog(TabuleiroView);
    	if (userSelection == JFileChooser.APPROVE_OPTION) {
    		Historico.salvarPartida(fileChooser.getSelectedFile() + ".txt", nJogadores, godMode);
        	JOptionPane.showMessageDialog(frameTabuleiro, "Partida salva com sucesso!", "PARTIDA SALVA", JOptionPane.INFORMATION_MESSAGE);
    	}
    }
    
    /* ---------- Sistema de observadores ---------- */
    private void inicializarEventos() {
        String[] eventos = {
            "novoValorDados",
            "novaPosJogador",
            "novaCarta",
            "passouVez",
            "estadoAtualizado"
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
