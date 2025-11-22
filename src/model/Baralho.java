package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Baralho {
    private Queue<Carta> cartas; // fila circular de cartas
    private int cartaSaidaLivreDisponivel;
    private Carta ultimaCarta; // última carta sacada (para exibir na tela)

    public Baralho(List<Carta> cartasIniciais) {
        if (cartasIniciais == null) {
            cartasIniciais = new ArrayList<>();
        }
        this.cartas = new LinkedList<>(cartasIniciais);
        Collections.shuffle((List<?>) this.cartas);
        this.cartaSaidaLivreDisponivel = 1;
        this.ultimaCarta = null;
    }

    public Carta comprarCarta() {
        if (cartas.isEmpty()) return null;
        Carta c = cartas.poll();
        cartas.add(c); // volta pro final da fila
        ultimaCarta = c;
        return c;
    }

    public Carta getUltimaCarta() {
        return ultimaCarta;
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
