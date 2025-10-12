package model;

import java.util.ArrayList;
import java.util.List;

public class Banco {
    private int saldo;
    private List<CasaPropriedade> propriedadesDisponiveis;
    
    public Banco() {
        this.saldo = 200000;
        this.propriedadesDisponiveis = new ArrayList<>();
    }
    
    public int getSaldo() {
        return saldo;
    }
    
    public void creditar(int valor) {
        saldo += valor;
    }
    
    public boolean debitar(int valor) {
        if (saldo >= valor) {
            saldo -= valor;
            return true;
        }
        return false;
    }
    
    public void pagarHonorarios(Jogador jogador) {
        if (debitar(200)) {
            jogador.creditar(200);
        }
    }
    
    public void receberImposto(int valor) {
        creditar(valor);
    }
    
    public void venderPropriedade(Jogador jogador, CasaPropriedade propriedade) {
        int valorVenda = (int)(propriedade.getPreco() * 0.9);
        if (debitar(valorVenda)) {
            jogador.creditar(valorVenda);
            propriedade.setProprietario(null);
            propriedadesDisponiveis.add(propriedade);
        }
    }
    
    public void addPropriedadeDisponivel(CasaPropriedade propriedade) {
        propriedadesDisponiveis.add(propriedade);
    }
    
    public List<CasaPropriedade> getPropriedadesDisponiveis() {
        return propriedadesDisponiveis;
    }
}