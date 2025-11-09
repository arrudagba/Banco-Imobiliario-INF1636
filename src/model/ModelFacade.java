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
    
    public String[] getDescricao(int pos) {
        return tabuleiro.getDesc(pos);
    }
    
    public String[] getNomesTodasPropriedades() {
        java.util.List<String> nomes = new java.util.ArrayList<>();
        for (int i = 0; i < tabuleiro.getTamanho(); i++) {
            Casa c = tabuleiro.getCasa(i);
            if (c instanceof CasaPropriedade) {
                CasaPropriedade p = (CasaPropriedade) c;
                nomes.add(p.getNome());
            }
        }
        return nomes.toArray(new String[0]);
    }
    
    
    // Salvar jogo
    
    public static class SaveState implements java.io.Serializable {
        public java.util.List<PlayerState> players = new java.util.ArrayList<>();
        public int currentIndex;
    }
    public static class PlayerState implements java.io.Serializable {
        public String nome;
        public int saldo;
        public int posicao;
        public java.util.List<String> propriedades = new java.util.ArrayList<>();
    }

    // Cria o snapshot do estado atual
    public SaveState snapshot() {
        SaveState s = new SaveState();
        s.currentIndex = jogadorDaVez;
        for (Jogador j : jogadores) {
            PlayerState ps = new PlayerState();
            ps.nome = j.getNome();
            ps.saldo = j.getSaldo();
            ps.posicao = j.getPosicao();
            for (CasaPropriedade p : j.getPropriedades()) {
                ps.propriedades.add(p.getNome());
            }
            s.players.add(ps);
        }
        return s;
    }
    
}
