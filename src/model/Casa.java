package model;

public abstract class Casa {
    private int posicao;
    private String nome;
    private TipoCasa tipo;
    
    public Casa(int posicao, String nome, TipoCasa tipo) {
        this.posicao = posicao;
        this.nome = nome;
        this.tipo = tipo;
    }
    
    public int getPosicao() { return posicao; }
    public String getNome() { return nome; }
    public TipoCasa getTipo() { return tipo; }
    
    public abstract void executarAcao(Jogador jogador);
}