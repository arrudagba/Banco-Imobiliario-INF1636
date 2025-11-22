package model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Random;

public class DadoTest {
    
    @Test
    public void testLancarDados() {
        Random rng = new Random(123);
        int[] dados = Dado.lancarDados(rng);
        
        assertNotNull(dados);
        assertEquals(3, dados.length);
        assertTrue(dados[0] >= 1 && dados[0] <= 6);
        assertTrue(dados[1] >= 1 && dados[1] <= 6);
        assertEquals(dados[0] + dados[1], dados[2]);
    }
    
    @Test
    public void testEhDupla() {
        int[] dupla = {3, 3};
        int[] naoDupla = {3, 4};
        
        assertTrue(Dado.ehDupla(dupla));
        assertFalse(Dado.ehDupla(naoDupla));
    }
}