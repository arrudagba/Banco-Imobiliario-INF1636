package model;

public class Carta {
    private final String idImagem;   // ex.: "chance12"
    private final String descricao;
    private final TipoCartas tipo;
    private final int valor;  // valor monetário (positivo para crédito, negativo para débito)

    public Carta(String idImagem, String descricao, TipoCartas tipo) {
        this(idImagem, descricao, tipo, 0);
    }
    
    public Carta(String idImagem, String descricao, TipoCartas tipo, int valor) {
        this.idImagem = idImagem;
        this.descricao = descricao;
        this.tipo = tipo;
        this.valor = valor;
    }
    
    public String getIdImagem() { return idImagem; }
    public String getDescricao() { return descricao; }
    public TipoCartas getTipo() { return tipo; }
    public int getValor() { return valor; }
}
