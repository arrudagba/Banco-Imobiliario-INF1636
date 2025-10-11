package br.com.bancoimobiliario.model;

public class CasaPropriedade implements Casa{
	
	// Dados Popriedade
	private int idx;
	private String nome;
	private TipoCasa tipo;
	private GrupoCor cor;
    private int preco;
    private int aluguel;
	
    // Dono
 	private Jogador dono;
    
	// Costrução
	private int casas;
	private boolean hotel;
	
	
	public CasaPropriedade(int indice, String nome, GrupoCor cor, int preco, int aluguel) {
        this.idx = indice; 
        this.nome = nome; 
        this.cor = cor;
        this.preco = preco; 
        this.aluguel = aluguel;
    }
	
	// Métodos da interface
	@Override public int getIdx() {return idx;}
	@Override public String getNome() {return nome;}
	@Override public TipoCasa getTipo() {return tipo;}
	
	// Propriedade
	public GrupoCor getCor() { return cor; }
    public int getPreco() { return preco; }
    public int getAluguel() { return aluguel; }

    // Dono
    public Jogador getDono() { return dono; }
    public void setDono(Jogador dono) { this.dono = dono; }
    public boolean temDono() { return dono != null; }

    // Construção
    public int getCasas() { return casas; }
    public boolean hasHotel() { return hotel; }
    public void setCasas(int qtd) { this.casas = qtd; }
    public void setHotel(boolean hotel) { this.hotel = hotel; }
	
	
}
