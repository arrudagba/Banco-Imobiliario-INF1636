package model;

import java.util.ArrayList;
import java.util.List;

public class Jogador {
    private String nome;
    private int saldo;
    private int posicao;
    private boolean preso;
    private int tentativasPrisao;
    private int duplasConsecutivas;
    private boolean cartaSaidaLivre;
    private List<CasaPropriedade> propriedades;
    
    public Jogador(String nome) {
        this.nome = nome;
        this.saldo = 4000;
        this.posicao = 0;
        this.preso = false;
        this.tentativasPrisao = 0;
        this.duplasConsecutivas = 0;
        this.cartaSaidaLivre = false;
        this.propriedades = new ArrayList<>();
    }
    
    public String getNome() { return nome; }
    public int getSaldo() { return saldo; }
    public int getPosicao() { return posicao; }
    public boolean isPreso() { return preso; }
    public int getTentativasPrisao() { return tentativasPrisao; }
    public int getDuplasConsecutivas() { return duplasConsecutivas; }
    public boolean isCartaSaidaLivre() { return cartaSaidaLivre; }
    public List<CasaPropriedade> getPropriedades() { return propriedades; }
    
    public void setPosicao(int posicao) { this.posicao = posicao; }
    public void setPreso(boolean preso) { this.preso = preso; }
    public void setTentativasPrisao(int tentativas) { this.tentativasPrisao = tentativas; }
    public void setDuplasConsecutivas(int duplas) { this.duplasConsecutivas = duplas; }
    public void setCartaSaidaLivre(boolean carta) { this.cartaSaidaLivre = carta; }
    
    public void incrementarTentativasPrisao() { this.tentativasPrisao++; }
    
    public void creditar(int valor) { this.saldo += valor; }
    
    public boolean debitar(int valor) {
            saldo -= valor;
            return true;
    }
    
    public void addPropriedade(CasaPropriedade propriedade) {
        propriedades.add(propriedade);
    }
    
    public boolean podeComprarPropriedade(Compravel propriedade) {
        return saldo >= propriedade.getPreco() && !propriedade.temProprietario();
    }

    public boolean comprarPropriedade(Compravel propriedade) {
        if (podeComprarPropriedade(propriedade)) {
            debitar(propriedade.getPreco());
            propriedade.setProprietario(this);
            if (propriedade instanceof CasaPropriedade) {
                addPropriedade((CasaPropriedade) propriedade);
            }
            return true;
        }
        return false;
    }
    
    public boolean construirCasa(CasaPropriedade propriedade) {
        if (propriedade.getProprietario() == this && saldo >= propriedade.getPreco()) {
            if (propriedade.construirCasa()) {
                debitar(propriedade.getPreco());
                return true;
            }
        }
        return false;
    }
    
    public boolean pagarAluguel(CasaPropriedade propriedade) {
        int aluguel = propriedade.getPreco(); // aluguel por enquanto com o valor da casa(como está na planilha)
        if (debitar(aluguel)) {
            propriedade.getProprietario().creditar(aluguel);
            return true;
        }
        return false;
    }
    
    public boolean venderPropriedadeParaBanco(CasaPropriedade propriedade, Banco banco) {
        if (propriedades.contains(propriedade)) {
            banco.venderPropriedade(this, propriedade);
            propriedades.remove(propriedade);
            return true;
        }
        return false;
    }
    
    public boolean tratarFalencia(Banco banco) {
        for (CasaPropriedade propriedade : new ArrayList<>(propriedades)) {
            venderPropriedadeParaBanco(propriedade, banco);
            if (saldo >= 0) {
                return true;
            }
        }
        return saldo >= 0;
    }
    
    public boolean estaFalido() {
        return saldo < 0;
    }
}