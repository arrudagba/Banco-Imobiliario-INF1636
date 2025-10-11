package model;

public class CasaImposto extends Casa{
	
	private int pos;
	private String nome;
	private int valor;
	
	public CasaImposto(int pos, String nome,int valor) {
		super(pos, nome, TipoCasa.IMPOSTO);
		this.valor = valor;
	}
	
	@Override
    public void executarAcao(Jogador jogador) {
		jogador.debitar(valor);
    }
}
