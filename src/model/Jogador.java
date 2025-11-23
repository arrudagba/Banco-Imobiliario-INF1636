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
        int custoCasa = propriedade.getCustoCasa(); // 50% do valor do território
        if (propriedade.getProprietario() == this && saldo >= custoCasa) {
            if (propriedade.construirCasa()) {
                debitar(custoCasa);
                return true;
            }
        }
        return false;
    }
    
    public boolean construirHotel(CasaPropriedade propriedade) {
        int custoHotel = propriedade.getCustoHotel(); // 100% do valor do território
        if (propriedade.getProprietario() == this && saldo >= custoHotel) {
            if (propriedade.construirHotel()) {
                debitar(custoHotel);
                return true;
            }
        }
        return false;
    }
    
    public boolean pagarAluguel(CasaPropriedade propriedade) {
        int aluguel = propriedade.calcularAluguel();
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
        // Vende todas as propriedades por metade do valor
        for (CasaPropriedade propriedade : new ArrayList<>(propriedades)) {
            venderPropriedadeParaBanco(propriedade, banco);
        }
        // Se ainda estiver negativo, está falido
        return saldo >= 0;
    }
    
    public boolean estaFalido() {
        return saldo < 0;
    }
}