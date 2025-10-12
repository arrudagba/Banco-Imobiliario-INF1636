package model;

import org.junit.Test;
import static org.junit.Assert.*;

public class BancoTest {
    
    @Test
    public void testCriacaoBanco() {
        Banco banco = new Banco();
        assertEquals(200000, banco.getSaldo());
    }
    
    @Test
    public void testCreditar() {
        Banco banco = new Banco();
        banco.creditar(1000);
        assertEquals(201000, banco.getSaldo());
    }
    
    @Test
    public void testDebitarSucesso() {
        Banco banco = new Banco();
        boolean debitou = banco.debitar(50000);
        assertTrue(debitou);
        assertEquals(150000, banco.getSaldo());
    }
    
    @Test
    public void testDebitarFalha() {
        Banco banco = new Banco();
        boolean debitou = banco.debitar(300000);
        assertFalse(debitou);
        assertEquals(200000, banco.getSaldo());
    }
    
    @Test
    public void testPagarHonorarios() {
        Banco banco = new Banco();
        Jogador jogador = new Jogador("Teste");
        int saldoInicialJogador = jogador.getSaldo();
        
        banco.pagarHonorarios(jogador);
        
        assertEquals(saldoInicialJogador + 200, jogador.getSaldo());
        assertEquals(199800, banco.getSaldo());
    }
    
    @Test
    public void testReceberImposto() {
        Banco banco = new Banco();
        banco.receberImposto(500);
        assertEquals(200500, banco.getSaldo());
    }
    
    @Test
    public void testVenderPropriedade() {
        Banco banco = new Banco();
        Jogador jogador = new Jogador("Teste");
        CasaPropriedade propriedade = new CasaPropriedade(2, "Leblon", 100);
        
        jogador.comprarPropriedade(propriedade);
        int saldoInicial = jogador.getSaldo();
        
        banco.venderPropriedade(jogador, propriedade);
        
        assertEquals(saldoInicial + 90, jogador.getSaldo()); // 90% de 100
        assertNull(propriedade.getProprietario());
        assertTrue(banco.getPropriedadesDisponiveis().contains(propriedade));
    }
    
    @Test
    public void testAddPropriedadeDisponivel() {
        Banco banco = new Banco();
        CasaPropriedade propriedade = new CasaPropriedade(2, "Leblon", 100);
        
        banco.addPropriedadeDisponivel(propriedade);
        
        assertTrue(banco.getPropriedadesDisponiveis().contains(propriedade));
    }
}