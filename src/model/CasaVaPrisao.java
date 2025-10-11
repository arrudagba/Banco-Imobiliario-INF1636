package model;

public class CasaVaPrisao extends Casa{
	
	private int pos;
	private String nome;
	private int valor;
	
	public CasaVaPrisao(int pos, String nome) {
		super(pos, nome, TipoCasa.VA_PARA_PRISAO);
	}
	
	public static void verificarTresDuplasConsecutivas(Jogador jogador, int[] dados, Tabuleiro tabuleiro) {
        if (Dado.ehDupla(dados)) {
            jogador.setDuplasConsecutivas(jogador.getDuplasConsecutivas() + 1);
            if (jogador.getDuplasConsecutivas() >= 3) {
                CasaVaPrisao prisao = (CasaVaPrisao) tabuleiro.getCasaVaPrisao();
                prisao.executarAcao(jogador);
            }
        } else {
            jogador.setDuplasConsecutivas(0);
        }
    }
	
	
	@Override
    public void executarAcao(Jogador jogador) {
		jogador.setPreso(true);
		jogador.setPosicao(10);
		jogador.setTentativasPrisao(0);
		jogador.setDuplasConsecutivas(0);
    }
	
}
