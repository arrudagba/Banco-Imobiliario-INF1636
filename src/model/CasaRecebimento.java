package model;

public class CasaRecebimento extends Casa {

	private int pos;
	private String nome;
	private int valor;
	
	public CasaRecebimento(int pos, String nome,int valor) {
		super(pos, nome, TipoCasa.RECEBIMENTO);
		this.valor = valor;
	}
	
	@Override
    public void executarAcao(Jogador jogador) {
		jogador.creditar(valor);
    }
	
}
