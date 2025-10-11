package model;

import org.junit.Test;
import static org.junit.Assert.*;

public class JogadorTest {
    
    @Test
    public void testComprarPropriedade() {
        Jogador jogador = new Jogador("Teste");
        CasaPropriedade propriedade = new CasaPropriedade(1, "Leblon", 100);
        
        boolean comprou = jogador.comprarPropriedade(propriedade);
        
        assertTrue(comprou);
        assertEquals(jogador, propriedade.getProprietario());
        assertTrue(jogador.getPropriedades().contains(propriedade));
        assertEquals(3900, jogador.getSaldo());
    }
    
    @Test
    public void testConstruirCasa() {
        Jogador jogador = new Jogador("Teste");
        CasaPropriedade propriedade = new CasaPropriedade(1, "Leblon", 100);
        
        jogador.comprarPropriedade(propriedade);
        boolean construiu = jogador.construirCasa(propriedade);
        
        assertTrue(construiu);
        assertEquals(1, propriedade.getNumCasas());
        assertEquals(3800, jogador.getSaldo());
    }
    
    @Test
    public void testPagarAluguel() {
    	
        Jogador proprietario = new Jogador("Proprietario");
        Jogador jogador = new Jogador("Jogador");
        CasaPropriedade propriedade = new CasaPropriedade(1, "Leblon", 100);
        
        proprietario.comprarPropriedade(propriedade);
        propriedade.construirCasa();
        
        int saldoInicialJogador = jogador.getSaldo();
        int saldoInicialProprietario = proprietario.getSaldo();
        
        boolean pagou = jogador.pagarAluguel(propriedade);
        
        assertTrue(pagou);
        assertEquals(saldoInicialJogador - 100, jogador.getSaldo());
        assertEquals(saldoInicialProprietario + 100, proprietario.getSaldo());
        
    }
    
    @Test
    public void testFalencia() {
        Jogador jogador = new Jogador("Teste");
        jogador.debitar(5000);
        
        assertTrue(jogador.estaFalido());
    }
}