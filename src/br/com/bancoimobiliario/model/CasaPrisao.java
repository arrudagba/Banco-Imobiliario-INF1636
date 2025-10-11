package br.com.bancoimobiliario.model;

public class CasaPrisao {
	
	// Dados da prisão
	private int pos;
	private TipoCasa tipo;
	
	public CasaPrisao(int idx) {
		this.pos = idx;
	}
	
	// Envia o jogador para a prisão.
    public static void enviarParaPrisao(Jogador j, int posPrisao) {
        j.setPos(posPrisao);
        j.setPreso(true);
        j.reniciarTentativa();
        j.setDuplasConsec(0);
    }

    // Tenta sair da prisão com dupla (sem multa). Retorna true se saiu.
    public static boolean tentarSairPrisaoComDados(Jogador j, int[] dados) {
        if (!j.getPreso()) return false;
        j.incTentativa();
        if (RegraDados.ehDupla(dados)) {
            j.setPreso(false);
            j.reniciarTentativa();
            return true; // quem chamar deve mover o peão pela soma dos dados
        }
        return false;
    }

    // Usa carta “Saída Livre da Prisão”, devolvendo-a ao baralho.
    public static boolean usarCartaSaidaLivre(Jogador j, Baralho baralho) {
        if (!j.getPreso()) return false;
        if (j.possuiCartaSaidaLivrePrisao()) {
            j.consumirCartaSaidaLivrePrisao();
            baralho.devolverCartaSaidaLivrePrisaoAoFundo();
            j.setPreso(false);
            j.setTentativasSairPrisao(0);
            return true;
        }
        return false;
    }

    // 3 duplas seguidas enviam à prisão.
    public static void verificarTresDuplasConsecutivas(Jogador j, int[] dados, Tabuleiro tabuleiro) {
        if (Dado.ehDupla(dados)) {
            j.setDuplasConsecutivas(j.getDuplasConsecutivas() + 1);
            if (j.getDuplasConsecutivas() >= 3) enviarParaPrisao(j, tabuleiro);
        } else {
            j.setDuplasConsecutivas(0);
        }
    }
}
