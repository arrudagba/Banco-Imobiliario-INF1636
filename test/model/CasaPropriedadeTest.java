package model;

import static org.junit.Assert.*;
import org.junit.Test;

public class CasaPropriedadeTest {

    @Test
    public void testComprarPropriedade() {
        Jogador jogador = new Jogador("Teste");
        CasaPropriedade casa = new CasaPropriedade(1, "Leblon", 100);

        boolean comprou = jogador.comprarPropriedade(casa);

        assertTrue(comprou);
        assertEquals(jogador, casa.getProprietario());
        assertTrue(jogador.getSaldo() < 4000);
    }

    @Test
    public void testConstruirCasa() {
        CasaPropriedade casa = new CasaPropriedade(1, "Leblon", 100);
        boolean construiu = casa.construirCasa();
        assertTrue(construiu);
        assertEquals(1, casa.getNumCasas());
    }

    @Test
    public void testConstruirHotel() {
        CasaPropriedade casa = new CasaPropriedade(1, "Leblon", 100);
        casa.construirCasa(); // precisa ter pelo menos uma casa
        boolean construiuHotel = casa.construirHotel();
        assertTrue(construiuHotel);
        assertTrue(casa.isTemHotel());
        assertEquals(1, casa.getNumCasas());
    }
}
