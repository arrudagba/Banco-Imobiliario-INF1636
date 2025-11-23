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
    private Carta ultimaCartaSacada;

    private ModelFacade() {
        this.banco = Banco.getInstance();
        this.tabuleiro = new Tabuleiro();
        this.jogadores = new ArrayList<>();
        this.rng = new Random();
        this.baralho = inicializarBaralho();
    }

    public static ModelFacade getInstance() {
        if (instance == null) instance = new ModelFacade();
        return instance;
    }

    /** Inicia nova partida */
    public void iniciarPartida(String[] nomes) {
        iniciarPartida(nomes, -1);
    }
    
    /** Inicia partida com jogador da vez específico (para carregamento) */
    public void iniciarPartida(String[] nomes, int jogadorDaVezInicial) {
        jogadores.clear();
        for (String nome : nomes) {
            jogadores.add(new Jogador(nome));
        }
        if (jogadorDaVezInicial < 0) {
            // SEMPRE começa no jogador 0 para nova partida
            this.jogadorDaVez = 0;
        } else {
            this.jogadorDaVez = jogadorDaVezInicial;
        }
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
            // Pula jogadores falidos
            while (jogadores.get(jogadorDaVez).estaFalido()) {
                jogadorDaVez = (jogadorDaVez + 1) % jogadores.size();
            }
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
        if (baralho == null) return null;
        ultimaCartaSacada = baralho.comprarCarta();
        return ultimaCartaSacada;
    }
    
    /** Retorna a última carta sacada */
    public Carta getUltimaCartaSacada() {
        return ultimaCartaSacada;
    }
    
    /** Aplica efeito de carta de Sorte/Revés */
    public void aplicarEfeitoCarta(Carta carta) {
        if (carta == null) return;
        
        Jogador atual = getJogadorDaVez();
        int valor = carta.getValor();
        
        // Carta especial: Saída livre da prisão
        if (carta.getTipo() == TipoCartas.SAIDA_LIVRE) {
            atual.setCartaSaidaLivre(true);
            return;
        }
        
        // Carta especial: Voltar ao início
        if ("Volte ao início".equals(carta.getDescricao())) {
            atual.setPosicao(0);
            atual.creditar(200); // Recebe ao passar pelo início
            return;
        }
        
        // Carta especial: Vá para prisão
        if ("Vá para a prisão".equals(carta.getDescricao())) {
            atual.setPreso(true);
            atual.setPosicao(10);
            atual.setDuplasConsecutivas(0);
            return;
        }
        
        // Cartas com valor monetário
        if (valor > 0) {
            atual.creditar(valor);
        } else if (valor < 0) {
            atual.debitar(Math.abs(valor));
        }
    }
    
    public String[] getDescricao(int pos) {
        return tabuleiro.getDesc(pos);
    }
    
    public String[] getNomesTodasPropriedades() {
        java.util.List<String> nomes = new java.util.ArrayList<>();
        for (int i = 0; i < tabuleiro.getTamanho(); i++) {
            Casa c = tabuleiro.getCasa(i);
            if (c instanceof CasaPropriedade || c instanceof CasaCompanhia) {
                nomes.add(c.getNome());
            }
        }
        return nomes.toArray(new String[0]);
    }
    
    /** Calcula patrimônio total do jogador (saldo + valor propriedades) */
    public int calcularPatrimonio(int indiceJogador) {
        if (indiceJogador < 0 || indiceJogador >= jogadores.size()) return 0;
        
        Jogador j = jogadores.get(indiceJogador);
        int patrimonio = j.getSaldo();
        
        // Soma valor de mercado das propriedades (90% do preço)
        for (CasaPropriedade p : j.getPropriedades()) {
            patrimonio += (int)(p.getPreco() * 0.9);
        }
        
        return patrimonio;
    }
    
    /** Decreta falência de um jogador */
    public void decretarFalencia(int indiceJogador) {
        if (indiceJogador >= 0 && indiceJogador < jogadores.size()) {
            Jogador j = jogadores.get(indiceJogador);
            // Remove propriedades
            for (CasaPropriedade p : new ArrayList<>(j.getPropriedades())) {
                p.setProprietario(null);
            }
            j.getPropriedades().clear();
            // Marca como falido (saldo negativo)
            if (j.getSaldo() >= 0) {
                j.debitar(j.getSaldo() + 1);
            }
        }
    }
    
    /** Obtém propriedades do jogador com preço de venda */
    public String[] getPropriedadesJogadorParaVenda(int indiceJogador) {
        if (indiceJogador < 0 || indiceJogador >= jogadores.size()) return null;
        
        Jogador j = jogadores.get(indiceJogador);
        if (j.getPropriedades().isEmpty()) return null;
        
        List<String> props = new ArrayList<>();
        for (CasaPropriedade p : j.getPropriedades()) {
            int precoVenda = (int)(p.getPreco() * 0.5);
            props.add(p.getNome() + " ($" + precoVenda + ")");
        }
        return props.toArray(new String[0]);
    }
    
    
    // Salvar jogo
    
    public static class SaveState implements java.io.Serializable {
        public java.util.List<PlayerState> players = new java.util.ArrayList<>();
        public int currentIndex;
        public int nJogadores;
    }
    public static class PlayerState implements java.io.Serializable {
        public String nome;
        public int saldo;
        public int posicao;
        public boolean preso;
        public boolean cartaSaidaLivre;
        public int tentativasPrisao;
        public int duplasConsecutivas;
        public java.util.List<String> propriedades = new java.util.ArrayList<>();
    }

    // Cria o snapshot do estado atual
    public SaveState snapshot() {
        SaveState s = new SaveState();
        s.currentIndex = jogadorDaVez;
        s.nJogadores = jogadores.size();
        for (Jogador j : jogadores) {
            PlayerState ps = new PlayerState();
            ps.nome = j.getNome();
            ps.saldo = j.getSaldo();
            ps.posicao = j.getPosicao();
            ps.preso = j.isPreso();
            ps.cartaSaidaLivre = j.isCartaSaidaLivre();
            ps.tentativasPrisao = j.getTentativasPrisao();
            ps.duplasConsecutivas = j.getDuplasConsecutivas();
            for (CasaPropriedade p : j.getPropriedades()) {
                ps.propriedades.add(p.getNome() + "|" + p.getNumCasas() + "|" + (p.isTemHotel() ? 1 : 0));
            }
            s.players.add(ps);
        }
        return s;
    }
    
    /** Restaura estado salvo */
    public void restaurarEstado(SaveState state) {
        if (state == null || state.players.isEmpty()) return;
        
        for (int i = 0; i < state.players.size() && i < jogadores.size(); i++) {
            PlayerState ps = state.players.get(i);
            Jogador j = jogadores.get(i);
            
            // Restaurar saldo
            int diferenca = ps.saldo - j.getSaldo();
            if (diferenca > 0) {
                j.creditar(diferenca);
            } else if (diferenca < 0) {
                j.debitar(Math.abs(diferenca));
            }
            
            // Restaurar posição e estados
            j.setPosicao(ps.posicao);
            j.setPreso(ps.preso);
            j.setCartaSaidaLivre(ps.cartaSaidaLivre);
            j.setTentativasPrisao(ps.tentativasPrisao);
            j.setDuplasConsecutivas(ps.duplasConsecutivas);
            
            // Restaurar propriedades
            for (String propData : ps.propriedades) {
                String[] parts = propData.split("\\|");
                String nomeProp = parts[0];
                int numCasas = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                int numHoteis = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
                
                // Encontrar propriedade no tabuleiro
                for (int pos = 0; pos < tabuleiro.getTamanho(); pos++) {
                    Casa c = tabuleiro.getCasa(pos);
                    if (c.getNome().equals(nomeProp) && c instanceof CasaPropriedade) {
                        CasaPropriedade prop = (CasaPropriedade) c;
                        prop.setProprietario(j);
                        j.addPropriedade(prop);
                        
                        // Restaurar casas e hotéis
                        for (int k = 0; k < numCasas; k++) {
                            prop.construirCasa();
                        }
                        if (numHoteis > 0) {
                            prop.construirHotel();
                        }
                        break;
                    }
                }
            }
        }
    }
    
    /** Inicializa baralho com 30 cartas */
    private Baralho inicializarBaralho() {
        List<Carta> cartas = new ArrayList<>();
        
        // Cartas de crédito
        cartas.add(new Carta("chance1", "Você ganhou $25", TipoCartas.SORTE, 25));
        cartas.add(new Carta("chance2", "Você ganhou $150", TipoCartas.SORTE, 150));
        cartas.add(new Carta("chance3", "Você ganhou $80", TipoCartas.SORTE, 80));
        cartas.add(new Carta("chance4", "Você ganhou $200", TipoCartas.SORTE, 200));
        cartas.add(new Carta("chance5", "Você ganhou $50", TipoCartas.SORTE, 50));
        cartas.add(new Carta("chance6", "Você ganhou $50", TipoCartas.SORTE, 50));
        cartas.add(new Carta("chance7", "Você ganhou $100", TipoCartas.SORTE, 100));
        cartas.add(new Carta("chance8", "Você ganhou $100", TipoCartas.SORTE, 100));
        cartas.add(new Carta("chance9", "Saída livre da prisão", TipoCartas.SAIDA_LIVRE, 0));
        cartas.add(new Carta("chance10", "Volte ao início", TipoCartas.SORTE, 0)); // Valor 0 pois já recebe $200 em aplicarEfeitoCarta
        
        // Cartas de débito
        cartas.add(new Carta("chance11", "Você perdeu $50", TipoCartas.REVES, -50));
        cartas.add(new Carta("chance12", "Você ganhou $45", TipoCartas.SORTE, 45));
        cartas.add(new Carta("chance13", "Você ganhou $100", TipoCartas.SORTE, 100));
        cartas.add(new Carta("chance14", "Você ganhou $100", TipoCartas.SORTE, 100));
        cartas.add(new Carta("chance15", "Você ganhou $20", TipoCartas.SORTE, 20));
        cartas.add(new Carta("chance16", "Você perdeu $15", TipoCartas.REVES, -15));
        cartas.add(new Carta("chance17", "Você perdeu $25", TipoCartas.REVES, -25));
        cartas.add(new Carta("chance18", "Você perdeu $45", TipoCartas.REVES, -45));
        cartas.add(new Carta("chance19", "Você perdeu $30", TipoCartas.REVES, -30));
        cartas.add(new Carta("chance20", "Você perdeu $100", TipoCartas.REVES, -100));
        cartas.add(new Carta("chance21", "Você perdeu $100", TipoCartas.REVES, -100));
        cartas.add(new Carta("chance22", "Você perdeu $40", TipoCartas.REVES, -40));
        cartas.add(new Carta("chance23", "Vá para a prisão", TipoCartas.REVES, 0));
        cartas.add(new Carta("chance24", "Você perdeu $30", TipoCartas.REVES, -30));
        cartas.add(new Carta("chance25", "Você perdeu $50", TipoCartas.REVES, -50));
        cartas.add(new Carta("chance26", "Você perdeu $25", TipoCartas.REVES, -25));
        cartas.add(new Carta("chance27", "Você perdeu $30", TipoCartas.REVES, -30));
        cartas.add(new Carta("chance28", "Você perdeu $45", TipoCartas.REVES, -45));
        cartas.add(new Carta("chance29", "Você perdeu $50", TipoCartas.REVES, -50));
        cartas.add(new Carta("chance30", "Você perdeu $50", TipoCartas.REVES, -50));
        
        return new Baralho(cartas);
    }
    
}
