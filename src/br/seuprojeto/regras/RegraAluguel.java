package br.seuprojeto.regras;

import br.seuprojeto.modelo.*; // Jogador, Propriedade, Banco

public final class RegraAluguel {
    private RegraAluguel() {}

    /** Regra 5: pagar aluguel automaticamente se houver >=1 casa/hotel. */
    public static boolean pagarAluguel(Jogador pagador, Propriedade prop, Banco banco) {
        Jogador credor = prop.getProprietario();
        if (credor == null || credor == pagador) return true;
        if (prop.getCasas() < 1 && !prop.temHotel()) return true;

        int valor = prop.calcularAluguel();
        if (pagador.getSaldo() < valor) return false;

        banco.debitar(pagador, valor);
        banco.creditar(credor, valor);
        return true;
    }
}
