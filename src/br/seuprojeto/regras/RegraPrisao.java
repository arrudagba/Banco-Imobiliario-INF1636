package br.seuprojeto.regras;

import br.seuprojeto.modelo.*; // Jogador, Tabuleiro, Baralho

public final class RegraPrisao {
    private RegraPrisao() {}

    /** Envia o jogador para a prisão. */
    public static void enviarParaPrisao(Jogador j, Tabuleiro tabuleiro) {
        j.setPosicao(tabuleiro.getIndicePrisao());
        j.setPreso(true);
        j.setTentativasSairPrisao(0);
        j.setDuplasConsecutivas(0);
    }

    /** Tenta sair da prisão com dupla (sem multa). Retorna true se saiu. */
    public static boolean tentarSairPrisaoComDados(Jogador j, int[] dados) {
        if (!j.isPreso()) return false;
        j.incrementarTentativasSairPrisao();
        if (RegraDados.ehDupla(dados)) {
            j.setPreso(false);
            j.setTentativasSairPrisao(0);
            return true; // quem chamar deve mover o peão pela soma dos dados
        }
        return false;
    }

    /** Usa carta “Saída Livre da Prisão”, devolvendo-a ao baralho. */
    public static boolean usarCartaSaidaLivre(Jogador j, Baralho baralho) {
        if (!j.isPreso()) return false;
        if (j.possuiCartaSaidaLivrePrisao()) {
            j.consumirCartaSaidaLivrePrisao();
            baralho.devolverCartaSaidaLivrePrisaoAoFundo();
            j.setPreso(false);
            j.setTentativasSairPrisao(0);
            return true;
        }
        return false;
    }

    /**  3 duplas seguidas enviam à prisão. */
    public static void verificarTresDuplasConsecutivas(Jogador j, int[] dados, Tabuleiro tabuleiro) {
        if (RegraDados.ehDupla(dados)) {
            j.setDuplasConsecutivas(j.getDuplasConsecutivas() + 1);
            if (j.getDuplasConsecutivas() >= 3) enviarParaPrisao(j, tabuleiro);
        } else {
            j.setDuplasConsecutivas(0);
        }
    }
}
