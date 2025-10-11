package model;

import org.junit.Test;
import static org.junit.Assert.*;

public class CasaPropriedadeTest {
    
    @Test
    public void testCalcularAluguel() {
        CasaPropriedade propriedade = new CasaPropriedade(1, "Leblon", 100, 50, 10);
        
        assertEquals(10, propriedade.calcularAluguel());
        
        propriedade.construirCasa();
        assertEquals(20, propriedade.calcularAluguel());
        
        propriedade.construirHotel();
        assertEquals(100, propriedade.calcularAluguel());
    }
    
    @Test
    public void testExecutarAcaoSemProprietario() {
        Jogador jogador = new Jogador("Teste");
        CasaPropriedade propriedade = new CasaPropriedade(1, "Leblon", 100, 50, 10);
        
        propriedade.executarAcao(jogador);
        
        assertEquals(jogador, propriedade.getProprietario());
        assertTrue(jogador.getPropriedades().contains(propriedade));
    }
}