package model;

public class CasaInicio extends Casa {
    
    public CasaInicio(int posicao, String nome) {
        super(posicao, nome, TipoCasa.INICIO);
    }
    
    @Override
    public void executarAcao(Jogador jogador) {
        Banco.getInstance().pagarHonorarios(jogador);
    }
}