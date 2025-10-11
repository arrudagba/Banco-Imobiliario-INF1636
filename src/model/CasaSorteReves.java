package model;

public class CasaSorteReves extends Casa {
    public CasaSorteReves(int posicao, String nome) {
        super(posicao, nome, TipoCasa.SORTE_REVES);
    }
    
    @Override
    public void executarAcao(Jogador jogador) {
    }
}