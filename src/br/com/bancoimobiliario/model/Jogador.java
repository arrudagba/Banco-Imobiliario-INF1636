package br.com.bancoimobiliario.model;

import java.util.*;

public class Jogador {

	// Dados Jogador
	private int num;
	private double montante;
	private boolean preso;
	private int pos;
	
	List<CasaPropriedade> propriedades = new ArrayList<>();
	
	public Jogador(int num) {
		this.num = num;
		this.montante = 4000;
	}
	
	// Prisão
	private int tentativa;
	
	public boolean getPreso() {return preso;}
	public void setPreso(boolean status) {this.preso = status; return;}
	public void incTentativa() {tentativa++; return;}
	public void reniciarTentativa() {tentativa = 0; return;}
	
	// Posição
	public int getPos() {return pos;}
	public void setPos(int pos) {this.pos = pos; return;}
	
	// Dados
	private int duplasConsec;
	
	public int getDuplasConsec() {return duplasConsec;}
	public void setDuplasConsec(int n) {duplasConsec = n; return;}
	
	// Transacao
	public void creditar(int v){ montante += v; }
    public void debitar(int v){ montante -= v; }
    
    public boolean comprarProp(CasaPropriedade casa) {
    	int valor = casa.getAluguel();
    	if (!casa.temDono() && montante >= valor) {
    		propriedades.add(casa);
    		debitar(valor);
    		return true;
    	}
    	return false;	
    }
    
    // Mopvimentação
    public void mover(int n) {pos += n;return;}
    
}
