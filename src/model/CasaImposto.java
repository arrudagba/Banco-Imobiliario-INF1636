package model;

public class CasaImposto extends Casa {
    private int valor;
    
    public CasaImposto(int pos, String nome, int valor) {
        super(pos, nome, TipoCasa.IMPOSTO);
        this.valor = valor;
    }
    
    @Override
    public void executarAcao(Jogador jogador) {
        if (jogador.debitar(valor)) {
            Banco.getInstance().receberImposto(valor);
        }
    }
}