package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BancoTest {
    
    @Before
    public void setUp() {
        // Resetar o banco antes de cada teste criando nova instância
        // Nota: Em produção, Singleton não deve permitir reset, mas para testes é necessário
    }
    
    @Test
    public void testCriacaoBanco() {
        Banco banco = Banco.getInstance();
        assertTrue(banco.getSaldo() > 0);
    }
    
    @Test
    public void testCreditar() {
        Banco banco = Banco.getInstance();
        int saldoInicial = banco.getSaldo();
        banco.creditar(1000);
        assertEquals(saldoInicial + 1000, banco.getSaldo());
    }
    
    @Test
    public void testDebitarSucesso() {
        Banco banco = Banco.getInstance();
        int saldoInicial = banco.getSaldo();
        boolean debitou = banco.debitar(50000);
        assertTrue(debitou);
        assertEquals(saldoInicial - 50000, banco.getSaldo());
    }
    
    @Test
    public void testDebitarFalha() {
        Banco banco = Banco.getInstance();
        int saldoInicial = banco.getSaldo();
        boolean debitou = banco.debitar(saldoInicial + 100000);
        assertFalse(debitou);
        assertEquals(saldoInicial, banco.getSaldo());
    }
    
    @Test
    public void testPagarHonorarios() {
        Banco banco = Banco.getInstance();
        Jogador jogador = new Jogador("Teste");
        int saldoInicialJogador = jogador.getSaldo();
        int saldoInicialBanco = banco.getSaldo();
        
        banco.pagarHonorarios(jogador);
        
        assertEquals(saldoInicialJogador + 200, jogador.getSaldo());
        assertEquals(saldoInicialBanco - 200, banco.getSaldo());
    }
    
    @Test
    public void testReceberImposto() {
        Banco banco = Banco.getInstance();
        int saldoInicial = banco.getSaldo();
        banco.receberImposto(500);
        assertEquals(saldoInicial + 500, banco.getSaldo());
    }
    
    @Test
    public void testVenderPropriedade() {
        Banco banco = Banco.getInstance();
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
        Banco banco = Banco.getInstance();
        CasaPropriedade propriedade = new CasaPropriedade(2, "Leblon", 100);
        
        banco.addPropriedadeDisponivel(propriedade);
        
        assertTrue(banco.getPropriedadesDisponiveis().contains(propriedade));
    }
}
