package model;

public interface Compravel {
    int getPreco();
    boolean temProprietario();
    void setProprietario(Jogador jogador);
    Jogador getProprietario();
}