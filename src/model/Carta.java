package model;

 class Carta {
    private final String idImagem;   // ex.: "chance12"
    private final String descricao;
    private final TipoCartas tipo;

    public Carta(String idImagem, String descricao, TipoCartas tipo) {
        this.idImagem = idImagem;
        this.descricao = descricao;
        this.tipo = tipo;
    }
    public String getIdImagem() { return idImagem; }
    public String getDescricao() { return descricao; }
    public TipoCartas getTipo() { return tipo; }
}
