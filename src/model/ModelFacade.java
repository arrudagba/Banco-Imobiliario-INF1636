package model;

import java.util.*;

public class ModelFacade {
    private static ModelFacade instance;
    private Banco banco;
    private Tabuleiro tabuleiro;
    private Baralho baralho;
    private List<Jogador> jogadores;
    private int jogadorDaVez;
    private Random rng;

    private ModelFacade() {
        this.banco = new Banco();
        this.tabuleiro = new Tabuleiro();
        this.jogadores = new ArrayList<>();
        this.rng = new Random();
    }

    public static ModelFacade getInstance() {
        if (instance == null) instance = new ModelFacade();
        return instance;
    }

    /** Inicia nova partida */
    public void iniciarPartida(String[] nomes) {
        jogadores.clear();
        for (String nome : nomes) {
            jogadores.add(new Jogador(nome));
        }
        this.jogadorDaVez = rng.nextInt(jogadores.size());
    }

    /** Lança os dados */
    public int[] lancarDados() {
        return Dado.lancarDados(rng);
    }

    /** Move o jogador atual */
    public void moverJogador(int[] dados) {
        Jogador atual = getJogadorDaVez();
        int novaPos = (atual.getPosicao() + dados[2]) % tabuleiro.getTamanho();
        atual.setPosicao(novaPos);

        Casa casa = tabuleiro.getCasa(novaPos);
        casa.executarAcao(atual);
    }

    /** Passa a vez */
    public void passarVez(boolean repetirJogada) {
        if (!repetirJogada) {
            jogadorDaVez = (jogadorDaVez + 1) % jogadores.size();
        }
    }

    /** Verifica se o jogo terminou */
    public boolean fimDoJogo() {
        long ativos = jogadores.stream().filter(j -> !j.estaFalido()).count();
        return ativos <= 1;
    }

    /** Retorna o jogador atual */
    public Jogador getJogadorDaVez() {
        return jogadores.get(jogadorDaVez);
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public Banco getBanco() {
        return banco;
    }

    public int getJogadorDaVezIndex() {
        return jogadorDaVez;
    }

    /** Jogador usa carta de saída livre */
    public boolean usarCartaSaidaLivre() {
        Jogador j = getJogadorDaVez();
        if (j.isCartaSaidaLivre()) {
            j.setCartaSaidaLivre(false);
            baralho.devolverCartaSaidaLivre();
            return true;
        }
        return false;
    }

    /** Compra uma carta de sorte ou revés */
    public Carta sacarCarta() {
        return baralho.comprarCarta();
    }
}
