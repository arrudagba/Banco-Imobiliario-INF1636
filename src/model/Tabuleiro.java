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
    	// Índices 0-39 (40 casas no total)
        casas.add(new CasaInicio(0, "PARTIDA", banco));                          // 0
        casas.add(new CasaPropriedade(1, "Leblon", 100));                        // 1
        casas.add(new CasaSorteReves(2, "Sorte ou Revés"));                      // 2
        casas.add(new CasaPropriedade(3, "Av. Presidente Vargas", 60));          // 3
        casas.add(new CasaPropriedade(4, "Av. Nossa Sra. De Copacabana", 60));   // 4
        casas.add(new CasaCompanhia(5, "Companhia Ferroviária", 200));           // 5
        casas.add(new CasaPropriedade(6, "Av. Brigadeiro Faria Lima", 240));     // 6
        casas.add(new CasaCompanhia(7, "Companhia de Viação", 200));             // 7
        casas.add(new CasaPropriedade(8, "Av. Rebouças", 220));                  // 8
        casas.add(new CasaPropriedade(9, "Av. 9 de Julho", 220));                // 9
        
        casas.add(new CasaPrisao(10, "Prisão"));                                 // 10
        casas.add(new CasaPropriedade(11, "Av. Europa", 200));                   // 11
        casas.add(new CasaSorteReves(12, "Sorte ou Revés"));                     // 12
        casas.add(new CasaPropriedade(13, "Rua Augusta", 180));                  // 13
        casas.add(new CasaPropriedade(14, "Av. Pacaembú", 180));                 // 14
        casas.add(new CasaCompanhia(15, "Companhia de Táxi", 150));              // 15
        casas.add(new CasaSorteReves(16, "Sorte ou Revés"));                     // 16
        casas.add(new CasaPropriedade(17, "Interlagos", 350));                   // 17
        casas.add(new CasaRecebimento(18, "Lucros ou Dividendos", 200));         // 18
        casas.add(new CasaPropriedade(19, "Morumbi", 400));                      // 19
        
        casas.add(new CasaLivre(20, "PARADA LIVRE"));                            // 20
        casas.add(new CasaPropriedade(21, "Flamengo", 120));                     // 21
        casas.add(new CasaSorteReves(22, "Sorte ou Revés"));                     // 22
        casas.add(new CasaPropriedade(23, "Botafogo", 100));                     // 23
        casas.add(new CasaImposto(24, "Imposto de Renda", 200, banco));          // 24
        casas.add(new CasaCompanhia(25, "Companhia de Navegação", 150));         // 25
        casas.add(new CasaPropriedade(26, "Av. Brasil", 160));                   // 26
        casas.add(new CasaSorteReves(27, "Sorte ou Revés"));                     // 27
        casas.add(new CasaPropriedade(28, "Av. Paulista", 140));                 // 28
        casas.add(new CasaPropriedade(29, "Jardim Europa", 140));                // 29
        
        casas.add(new CasaVaPrisao(30, "VÁ PARA PRISÃO"));                       // 30
        casas.add(new CasaPropriedade(31, "Copacabana", 260));                   // 31
        casas.add(new CasaCompanhia(32, "Companhia de Aviação", 200));           // 32
        casas.add(new CasaPropriedade(33, "Av. Vieira Souto", 320));             // 33
        casas.add(new CasaPropriedade(34, "Av. Atlântica", 300));                // 34
        casas.add(new CasaCompanhia(35, "Companhia de Táxi Aéreo", 200));        // 35
        casas.add(new CasaPropriedade(36, "Ipanema", 300));                      // 36
        casas.add(new CasaSorteReves(37, "Sorte ou Revés"));                     // 37
        casas.add(new CasaPropriedade(38, "Jardim Paulista", 280));              // 38
        casas.add(new CasaPropriedade(39, "Brooklin", 260));                     // 39
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

    public String[] getDesc(int pos) {
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

        } 
        
        return new String[]{""};
    }
}
