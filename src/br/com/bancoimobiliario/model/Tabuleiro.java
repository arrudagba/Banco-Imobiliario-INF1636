package br.com.bancoimobiliario.model;

import java.util.*;

public class Tabuleiro {
	
	private List<Casa> casas = new ArrayList<>();
	
	public Tabuleiro(List<Casa> casas) {
        this.casas = new ArrayList<>(casas);
    }
	
	public int getTamanho() {return casas.size();}
	
}
	
