package model;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {
    private List<Casa> casas;
    
    public Tabuleiro() {
        casas = new ArrayList<>();
        inicializarTabuleiro();
    }
    
    private void inicializarTabuleiro() {
        casas.add(new CasaInicio(0, "Início"));
        casas.add(new CasaPropriedade(1, "Leblon", 100, 50, 10));
        casas.add(new CasaPropriedade(2, "Av. Presidente Vargas", 120, 60, 12));
        casas.add(new CasaSorteReves(3, "Sorte/Revés"));
        casas.add(new CasaPropriedade(4, "Av. Nossa Senhora de Copacabana", 140, 70, 14));
        casas.add(new CasaPrisao(5, "Prisão"));
        casas.add(new CasaPropriedade(6, "Av. Brigadeiro Faria Lima", 160, 80, 16));
        casas.add(new CasaPropriedade(7, "Av. Rebouças", 180, 90, 18));
        casas.add(new CasaSorteReves(8, "Sorte/Revés"));
        casas.add(new CasaPropriedade(9, "Av. 9 de Julho", 200, 100, 20));
        casas.add(new CasaLivre(10, "Parada Livre"));
    }
    
    public Casa getCasa(int posicao) {
        return casas.get(posicao % casas.size());
    }
    
    public Casa getCasaPrisao() {
        for (Casa casa : casas) {
            if (casa.getTipo() == TipoCasa.PRISAO) {
                return casa;
            }
        }
        return null;
    }
    
    public int getTamanho() {
        return casas.size();
    }
}