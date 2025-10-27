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