package model;

public class CasaLivre extends Casa {
	
    public CasaLivre(int posicao, String nome) {
        super(posicao, nome, TipoCasa.LIVRE);
    }
    
    @Override
    public void executarAcao(Jogador jogador) {
    }
    
}