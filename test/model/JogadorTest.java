package model;

import org.junit.Test;
import static org.junit.Assert.*;

public class JogadorTest {
    
    @Test
    public void testComprarPropriedade() {
        Jogador jogador = new Jogador("Teste");
        CasaPropriedade propriedade = new CasaPropriedade(2, "Leblon", 100);
        
        boolean comprou = jogador.comprarPropriedade(propriedade);
        
        assertTrue(comprou);
        assertEquals(jogador, propriedade.getProprietario());
        assertTrue(jogador.getPropriedades().contains(propriedade));
        assertEquals(3900, jogador.getSaldo());
    }
    
    @Test
    public void testConstruirCasa() {
        Jogador jogador = new Jogador("Teste");
        CasaPropriedade propriedade = new CasaPropriedade(2, "Leblon", 100);
        
        jogador.comprarPropriedade(propriedade);
        boolean construiu = jogador.construirCasa(propriedade);
        
        assertTrue(construiu);
        assertEquals(1, propriedade.getNumCasas());
        // Custo casa = 50% do valor = $50
        assertEquals(3850, jogador.getSaldo()); // 3900 - 50
    }
    
    @Test
    public void testPagarAluguel() {
        Jogador proprietario = new Jogador("Proprietario");
        Jogador jogador = new Jogador("Jogador");
        CasaPropriedade propriedade = new CasaPropriedade(2, "Leblon", 100);
        
        proprietario.comprarPropriedade(propriedade);
        propriedade.construirCasa();
        
        int saldoInicialJogador = jogador.getSaldo();
        int saldoInicialProprietario = proprietario.getSaldo();
        
        boolean pagou = jogador.pagarAluguel(propriedade);
        
        // Aluguel = Vb (10%) + Vc*1 (15%) = $10 + $15 = $25
        int aluguel = propriedade.calcularAluguel();
        
        assertTrue(pagou);
        assertEquals(saldoInicialJogador - aluguel, jogador.getSaldo());
        assertEquals(saldoInicialProprietario + aluguel, proprietario.getSaldo());
    }
    
    @Test
    public void testPagarImposto() {
        Banco banco = Banco.getInstance();
        Jogador jogador = new Jogador("Teste");
        CasaImposto imposto = new CasaImposto(21, "Imposto de Renda", 200);
        
        int saldoInicialJogador = jogador.getSaldo();
        int saldoInicialBanco = banco.getSaldo();
        
        imposto.executarAcao(jogador);
        
        assertEquals(saldoInicialJogador - 200, jogador.getSaldo());
        assertEquals(saldoInicialBanco + 200, banco.getSaldo());
    }
    
    @Test
    public void testVenderPropriedadeParaBanco() {
        Banco banco = Banco.getInstance();
        Jogador jogador = new Jogador("Teste");
        CasaPropriedade propriedade = new CasaPropriedade(2, "Leblon", 100);
        
        jogador.comprarPropriedade(propriedade);
        int saldoInicial = jogador.getSaldo();
        
        boolean vendeu = jogador.venderPropriedadeParaBanco(propriedade, banco);
        
        assertTrue(vendeu);
        assertEquals(saldoInicial + 90, jogador.getSaldo()); // 90% de 100
        assertFalse(jogador.getPropriedades().contains(propriedade));
        assertTrue(banco.getPropriedadesDisponiveis().contains(propriedade));
    }
    
    @Test
    public void testTratarFalencia() {
        Banco banco = Banco.getInstance();
        Jogador jogador = new Jogador("Teste");
        CasaPropriedade propriedade = new CasaPropriedade(2, "Leblon", 100);
        
        jogador.comprarPropriedade(propriedade);
        jogador.debitar(3990); // fica com saldo negativo (-90)
        
        boolean tratou = jogador.tratarFalencia(banco);
        
        assertTrue(tratou);
        assertFalse(jogador.estaFalido());
        assertEquals(0, jogador.getSaldo()); // -90 + 90 da venda = 0
    }
    
    @Test
    public void testFalencia() {
        Jogador jogador = new Jogador("Teste");
        jogador.debitar(5000);
        
        assertTrue(jogador.estaFalido());
    }
    
}
