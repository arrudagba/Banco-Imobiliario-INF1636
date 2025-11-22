package model;

public class CasaImposto extends Casa {
    private int valor;
    private Banco banco;
    
    public CasaImposto(int pos, String nome, int valor, Banco banco) {
        super(pos, nome, TipoCasa.IMPOSTO);
        this.valor = valor;
        this.banco = banco;
    }
    
    @Override
    public void executarAcao(Jogador jogador) {

        if (jogador.debitar(valor)) {
            banco.receberImposto(valor);
        }
    }
}