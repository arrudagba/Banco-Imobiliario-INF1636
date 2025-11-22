package model;

import java.util.Random;

public class Dado {

    private static int dados[] = new int[3];  

    public static int[] lancarDados(Random rng) {
        int d1 = rng.nextInt(6) + 1;  
        int d2 = rng.nextInt(6) + 1;  
        dados[0] = d1;  
        dados[1] = d2;  
        dados[2] = d1 + d2;
        
        return dados;
    }

    public static boolean ehDupla(int[] dados) {
        return dados[0] == dados[1];  
    }
}
