package model;

public class CasaPrisao extends Casa {
    private static final int MAX_TENTATIVAS = 3;
    
    public CasaPrisao(int posicao, String nome) {
        super(posicao, nome, TipoCasa.PRISAO);
    }
    
    public void enviarParaPrisao(Jogador jogador) {
        jogador.setPreso(true);
        jogador.setTentativasPrisao(0);
        jogador.setPosicao(this.getPosicao());
    }
    
    public boolean tentarSairComDados(Jogador jogador, int[] dados) {
        if (!jogador.isPreso()) return false;
        
        jogador.incrementarTentativasPrisao();
        
        if (Dado.ehDupla(dados)) {
            jogador.setPreso(false);
            jogador.setTentativasPrisao(0);
            return true;
        }
        
        return false;
    }
    
    public boolean usarCartaSaidaLivre(Jogador jogador, Baralho baralho) {
        if (!jogador.isPreso()) return false;
        
        if (jogador.isCartaSaidaLivre()) {
            jogador.setCartaSaidaLivre(false);
            baralho.devolverCartaSaidaLivre();
            jogador.setPreso(false);
            jogador.setTentativasPrisao(0);
            return true;
        }
        return false;
    }
    
    public static void verificarTresDuplasConsecutivas(Jogador jogador, int[] dados, Tabuleiro tabuleiro) {
        if (Dado.ehDupla(dados)) {
            jogador.setDuplasConsecutivas(jogador.getDuplasConsecutivas() + 1);
            if (jogador.getDuplasConsecutivas() >= 3) {
                CasaPrisao prisao = (CasaPrisao) tabuleiro.getCasaPrisao();
                prisao.enviarParaPrisao(jogador);
            }
        } else {
            jogador.setDuplasConsecutivas(0);
        }
    }
    
    @Override
    public void executarAcao(Jogador jogador) {
    }
}