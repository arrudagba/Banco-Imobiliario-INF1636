package model;

public class CasaPropriedade extends Casa implements Compravel {
	
    private int preco;
    private Jogador proprietario;
    private int numCasas;
    private boolean temHotel;
    private int numHotel = 0;
    
    public CasaPropriedade(int posicao, String nome, int preco) {
        super(posicao, nome, TipoCasa.PROPRIEDADE);
        this.preco = preco;
        this.proprietario = null;
        this.numCasas = 0;
        this.temHotel = false;
    }
    
    public int getPreco() { return preco; }
    public Jogador getProprietario() { return proprietario; }
    public int getNumCasas() { return numCasas; }
    public boolean isTemHotel() { return temHotel; }
    public int getNumHotel() { return numHotel; }
    
    public void setProprietario(Jogador proprietario) { this.proprietario = proprietario; }
    
    public boolean construirCasa() {
        if (numCasas < 4 && !temHotel) {
            numCasas++;
            return true;
        }
        return false;
    }
    
    public boolean construirHotel() {
        if (numCasas >= 1 && !temHotel) {
            temHotel = true;
            numHotel += 1;
            numCasas = 0;
            return true;
        }
        return false;
    }
    
    public boolean temProprietario() {
        return proprietario != null;
    }
    
    /**
     * Calcula o valor do aluguel da propriedade.
     * Fórmula: Va = Vb + Vc*n + Vh
     * Vb: valor base (10% do valor do território)
     * Vc: valor do aluguel de uma casa (15% do valor do território)
     * n: número de casas
     * Vh: valor do aluguel do hotel (30% do valor do território)
     */
    public int calcularAluguel() {
        double vb = preco * 0.1;  // Valor base: 10% do território
        double vc = preco * 0.15; // Valor por casa: 15% do território
        double vh = temHotel ? (preco * 0.3) : 0; // Valor do hotel: 30% do território
        
        return (int) (vb + (vc * numCasas) + vh);
    }
    
    /**
     * Retorna o custo de construir uma casa (50% do valor do território)
     */
    public int getCustoCasa() {
        return (int) (preco * 0.5);
    }
    
    /**
     * Retorna o custo de construir um hotel (100% do valor do território)
     */
    public int getCustoHotel() {
        return preco;
    }
    
    @Override
    public void executarAcao(Jogador jogador) {
        if (!temProprietario()) {
            if (jogador.podeComprarPropriedade(this)) {
                jogador.comprarPropriedade(this);
            }
        } else if (proprietario != jogador && numCasas > 0) {
            jogador.pagarAluguel(this);
        }
    }
    
    
}