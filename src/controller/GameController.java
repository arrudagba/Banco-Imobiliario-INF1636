package controller;

import model.*;
import controller.observer.*;
import view.JanelaInicial;
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

    /* ---------- Construtor privado ---------- */
    private GameController() {
        this.model = ModelFacade.getInstance();
        inicializarEventos();
    }

    /* ---------- Fluxo de telas ---------- */
    public void start() {
        view.Images.carregar();
        new JanelaInicial(this).setVisible(true);
    }

    public void iniciarNovaPartida(int qtdJogadores) {
        // Cria nomes padrão (poderá vir da view no futuro)
        String[] nomes = new String[qtdJogadores];
        for (int i = 0; i < qtdJogadores; i++) {
            nomes[i] = "J" + (i + 1);
        }

        model.iniciarPartida(nomes);
        //model.inicializarBaralhoChance();
        notifica("estadoAtualizado");

        new TabuleiroView(this).setVisible(true);
    }

    /* ---------- Dados ---------- */
    public int getDado1() { return dado1; }
    public int getDado2() { return dado2; }

    public void rolarDados() {
        int[] valores = model.lancarDados();
        dado1 = valores[0];
        dado2 = valores[1];
        notifica("novoValorDados");
    }

    /* ---------- Jogada ---------- */
    public void processarJogadaComValores(int d1, int d2) {
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
