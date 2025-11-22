package model;

public class CasaCompanhia extends Casa implements Compravel {
    private int preco;
    private Jogador proprietario;
    
    public CasaCompanhia(int posicao, String nome, int preco) {
        super(posicao, nome, TipoCasa.COMPANHIA);
        this.preco = preco;
        this.proprietario = null;
    }
    
    public int getPreco() { return preco; }
    public Jogador getProprietario() { return proprietario; }
    public void setProprietario(Jogador proprietario) { this.proprietario = proprietario; }
    public boolean temProprietario() { return proprietario != null; }
    
    public int calcularAluguel(int somaDados) {
        return somaDados * 10;
    }
    
    @Override
    public void executarAcao(Jogador jogador) {
        // A lógica de compra será tratada no Controller/View
    }
}