package model;

import org.junit.Test;
import static org.junit.Assert.*;

public class CasaPrisaoTest {
    
    @Test
    public void testVaParaPrisao() {
        Jogador jogador = new Jogador("Teste");
        CasaVaPrisao prisao = new CasaVaPrisao(26, "VÁ PARA PRISÃO");
        
        prisao.executarAcao(jogador);
        
        assertTrue(jogador.isPreso());
        assertEquals(10, jogador.getPosicao());
        assertEquals(0, jogador.getTentativasPrisao());
    }
    
    @Test
    public void testTentarSairComDadosSucesso() {
        Jogador jogador = new Jogador("Teste");
        CasaPrisao prisao = new CasaPrisao(10, "PRISÃO");
        
        prisao.executarAcao(jogador);
        
        int[] dupla = {3, 3};
        boolean saiu = prisao.tentarSairComDados(jogador, dupla);
        
        assertTrue(saiu);
        assertFalse(jogador.isPreso());
        assertEquals(0, jogador.getTentativasPrisao());
    }
    
    @Test
    public void testTentarSairComDadosFalha() {
        Jogador jogador = new Jogador("Teste");
        CasaPrisao prisao = new CasaPrisao(10, "PRISÃO");
        prisao.executarAcao(jogador);
        
        int[] naoDupla = {3, 4};
        boolean saiu = prisao.tentarSairComDados(jogador, naoDupla);
        
        assertFalse(saiu);
        assertTrue(jogador.isPreso());
        assertEquals(1, jogador.getTentativasPrisao());
    }
    
    @Test
    public void testUsarCartaSaidaLivre() {
        Jogador jogador = new Jogador("Teste");
        CasaPrisao prisao = new CasaPrisao(10, "PRISÃO");
        prisao.executarAcao(jogador);
        
        Baralho baralho = new Baralho(null);
        
        jogador.setCartaSaidaLivre(true);
        
        boolean saiu = prisao.usarCartaSaidaLivre(jogador, baralho);
        
        assertTrue(saiu);
        assertFalse(jogador.isPreso());
        assertFalse(jogador.isCartaSaidaLivre());
    }
    
    @Test
    public void testTresDuplasConsecutivas() {
        Jogador jogador = new Jogador("Teste");
        Tabuleiro tabuleiro = new Tabuleiro();
        
        int[] dupla1 = {1, 1};
        int[] dupla2 = {2, 2};
        int[] dupla3 = {3, 3};
        
        CasaVaPrisao.verificarTresDuplasConsecutivas(jogador, dupla1, tabuleiro);
        CasaVaPrisao.verificarTresDuplasConsecutivas(jogador, dupla2, tabuleiro);
        CasaVaPrisao.verificarTresDuplasConsecutivas(jogador, dupla3, tabuleiro);
        
        assertTrue(jogador.isPreso());
    }
}