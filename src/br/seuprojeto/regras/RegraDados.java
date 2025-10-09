package br.seuprojeto.regras;

import java.util.Random;

public final class RegraDados {
    private RegraDados() {}

    /** Regra 1: lançar dados (virtual). */
    public static int[] lancarDados(Random rng) {
        int d1 = rng.nextInt(6) + 1;
        int d2 = rng.nextInt(6) + 1;
        return new int[]{ d1, d2 };
    }

    /** Dupla? */
    public static boolean ehDupla(int[] dados) {
        return dados != null && dados.length == 2 && dados[0] == dados[1];
    }
}
