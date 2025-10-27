package model;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {
    static private final List<Casa> casas = new ArrayList();
    private final Banco banco;

    public Tabuleiro() {
        this.banco = new Banco();          // inicializa o banco
        inicializarTabuleiro();
    }

    private void inicializarTabuleiro() {
        casas.add(new CasaInicio(1, "PARTIDA", banco));
        casas.add(new CasaPropriedade(2, "Leblon", 100));
        casas.add(new CasaPropriedade(3, "Av. Presidente Vargas", 60));
        casas.add(new CasaPropriedade(4, "Av. Nossa Sra. De Copacabana", 60));
        casas.add(new CasaCompanhia(5, "Companhia Ferroviária", 200));
        casas.add(new CasaPropriedade(6, "Av. Brigadeiro Faria Lima", 240));
        casas.add(new CasaCompanhia(7, "Companhia de Viação", 200));
        casas.add(new CasaPropriedade(8, "Av. Rebouças", 220));

        casas.add(new CasaPropriedade(9,  "Av. 9 de Julho", 220));
        casas.add(new CasaPrisao(10, "Prisão"));

        casas.add(new CasaPropriedade(11, "Av. Europa", 200));
        casas.add(new CasaPropriedade(12, "Rua Augusta", 180));
        casas.add(new CasaPropriedade(13, "Av. Pacaembú", 180));
        casas.add(new CasaCompanhia(14, "Companhia de Táxi", 150));
        casas.add(new CasaPropriedade(15, "Interlagos", 350));

        casas.add(new CasaRecebimento(16, "Lucros ou Dividendos", 200));
        casas.add(new CasaPropriedade(17, "Morumbi", 400));
        casas.add(new CasaLivre(18, "PARADA LIVRE"));

        casas.add(new CasaPropriedade(19, "Flamengo", 120));
        casas.add(new CasaPropriedade(20, "Botafogo", 100));

        casas.add(new CasaImposto(21, "Imposto de Renda", 200, banco));

        casas.add(new CasaCompanhia(22, "Companhia de Navegação", 150));

        casas.add(new CasaPropriedade(23, "Av. Brasil", 160));
        casas.add(new CasaPropriedade(24, "Av. Paulista", 140));
        casas.add(new CasaPropriedade(25, "Jardim Europa", 140));

        casas.add(new CasaVaPrisao(26, "VÁ PARA PRISÃO"));

        casas.add(new CasaPropriedade(27, "Copacabana", 260));
        casas.add(new CasaCompanhia(28, "Companhia de Aviação", 200));
        casas.add(new CasaPropriedade(29, "Av. Vieira Souto", 320));
        casas.add(new CasaPropriedade(30, "Av. Atlântica", 300));
        casas.add(new CasaCompanhia(31, "Companhia de Táxi Aéreo", 200));
        casas.add(new CasaPropriedade(32, "Ipanema", 300));
        casas.add(new CasaPropriedade(33, "Jardim Paulista", 280));
        casas.add(new CasaPropriedade(34, "Brooklin", 260));
    }

    public Casa getCasa(int posicao) {
        return casas.get(Math.floorMod(posicao, casas.size()));
    }

    public Casa getCasaPrisao() {
        for (Casa casa : casas) {
            if (casa.getTipo() == TipoCasa.PRISAO) return casa;
        }
        return null;
    }

    public Casa getCasaVaPrisao() {
        for (Casa casa : casas) {
            if (casa.getTipo() == TipoCasa.VA_PARA_PRISAO) return casa;
        }
        return null;
    }

    static public int getPos(String p) {
        int pos = 0;
        for (Casa c : casas) {
            if (p.startsWith(c.getNome())) return pos;
            pos++;
        }
        return -1;
    }

    public int getTamanho() {
        return casas.size();
    }

    static public String[] getDesc(int pos) {
        if (pos < 0 || pos >= casas.size()) return new String[]{""};

        Casa c = casas.get(pos);

        if (c instanceof CasaPropriedade) {
            CasaPropriedade t = (CasaPropriedade) c;
            String[] descricao = new String[4];
            descricao[0] = "Preço: $" + t.getPreco();
            descricao[1] = "Titular: " + (t.getProprietario() == null ? "sem titular" : t.getProprietario().getNome());
            descricao[2] = "Casas: " + t.getNumCasas();
            descricao[3] = "Hotéis: " + t.getNumHotel();
            return descricao;

        } else if (c instanceof CasaCompanhia) {
            CasaCompanhia cp = (CasaCompanhia) c;
            String[] descricao = new String[2];
            descricao[0] = "Preço: $" + cp.getPreco();
            descricao[1] = "Titular: " + (cp.getProprietario() == null ? "sem titular" : cp.getProprietario().getNome());
            return descricao;
        }

        return new String[]{""};
    }
}
