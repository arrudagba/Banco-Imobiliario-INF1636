package model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
    DadoTest.class, 
    JogadorTest.class,
    CasaPropriedadeTest.class,
    CasaPrisaoTest.class,
    CasaCompanhiaTest.class,
    BancoTest.class
    })

public class AllTests { }