package model;

public class CasaPrisao extends Casa{
	
    private static final int MAX_TENTATIVAS = 3;
    
    public CasaPrisao(int posicao, String nome) {
        super(posicao, nome, TipoCasa.PRISAO);
    }
    
    
    public boolean tentarSairComDados(Jogador jogador, int[] dados) {
        if (!jogador.isPreso()) return false;
        
        jogador.incrementarTentativasPrisao();
        
        if (Dado.ehDupla(dados)) {
            jogador.setPreso(false);
            jogador.setTentativasPrisao(0);
            
            return true;
        }
        
        if (jogador.getTentativasPrisao() >= MAX_TENTATIVAS) {
        	// a multa ao banqueiro não será considerada
        	int pos = jogador.getPosicao();
        	int dado = dados[2];
        	jogador.setPreso(false);
        	jogador.setPosicao(pos + dado);
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
            jogador.setDuplasConsecutivas(0);
            return true;
        }
        return false;
    }
    
    
    @Override
    public void executarAcao(Jogador jogador) {
    	jogador.setPreso(true);
        jogador.setTentativasPrisao(0);
        jogador.setDuplasConsecutivas(0);
    }
}