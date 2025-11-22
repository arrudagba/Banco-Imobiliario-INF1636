package model;

import org.junit.Test;
import static org.junit.Assert.*;

public class CasaCompanhiaTest {
    
    @Test
    public void testCriacaoCompanhia() {
        CasaCompanhia companhia = new CasaCompanhia(5, "Companhia Ferroviária", 200);
        assertEquals(5, companhia.getPosicao());
        assertEquals("Companhia Ferroviária", companhia.getNome());
        assertEquals(200, companhia.getPreco());
        assertNull(companhia.getProprietario());
        assertFalse(companhia.temProprietario());
    }
    
    @Test
    public void testComprarCompanhia() {
        Jogador jogador = new Jogador("Teste");
        CasaCompanhia companhia = new CasaCompanhia(5, "Companhia Ferroviária", 200);
        
        boolean comprou = jogador.comprarPropriedade(companhia);
        
        assertTrue(comprou);
        assertEquals(jogador, companhia.getProprietario());
        assertTrue(companhia.temProprietario());
        assertEquals(3800, jogador.getSaldo());
    }
    
    @Test
    public void testCalcularAluguel() {
        CasaCompanhia companhia = new CasaCompanhia(5, "Companhia Ferroviária", 200);
        int[] dados = {3, 4, 7}; // soma = 7
        
        int aluguel = companhia.calcularAluguel(dados[2]);
        
        assertEquals(70, aluguel); // 7 * 10
    }
    
    @Test
    public void testCalcularAluguelDupla() {
        CasaCompanhia companhia = new CasaCompanhia(5, "Companhia Ferroviária", 200);
        int[] dados = {5, 5, 10}; // soma = 10
        
        int aluguel = companhia.calcularAluguel(dados[2]);
        
        assertEquals(100, aluguel); // 10 * 10
    }
    
    
    @Test
    public void testNaoPodeComprarCompanhiaComProprietario() {
        Jogador proprietario = new Jogador("Proprietario");
        Jogador jogador = new Jogador("Jogador");
        CasaCompanhia companhia = new CasaCompanhia(5, "Companhia Ferroviária", 200);
        
        proprietario.comprarPropriedade(companhia);
        boolean comprou = jogador.comprarPropriedade(companhia);
        
        assertFalse(comprou);
        assertEquals(proprietario, companhia.getProprietario());
    }
}