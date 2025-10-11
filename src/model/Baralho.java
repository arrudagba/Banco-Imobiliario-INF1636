package model;

import java.util.ArrayList;
import java.util.List;

public class Baralho {
    private List<Carta> cartas;
    private int cartaSaidaLivreDisponivel;
    
    public Baralho() {
        cartas = new ArrayList<>();
        cartaSaidaLivreDisponivel = 1;
    }
    
    public void devolverCartaSaidaLivre() {
        cartaSaidaLivreDisponivel++;
    }
    
    public boolean podePegarCartaSaidaLivre() {
        return cartaSaidaLivreDisponivel > 0;
    }
    
    public void pegarCartaSaidaLivre(Jogador jogador) {
        if (podePegarCartaSaidaLivre()) {
            jogador.setCartaSaidaLivre(true);
            cartaSaidaLivreDisponivel--;
        }
    }
}