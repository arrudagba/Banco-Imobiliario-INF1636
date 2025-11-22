package model;

public class CasaInicio extends Casa {
    private Banco banco;
    
    public CasaInicio(int posicao, String nome, Banco banco) {
        super(posicao, nome, TipoCasa.INICIO);
        this.banco = banco;
    }
    
    @Override
    public void executarAcao(Jogador jogador) {
        banco.pagarHonorarios(jogador);
    }
}