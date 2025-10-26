package view;

public class CoordenadasCasa {
    // coordenadas aproximadas para 34 casas
    private static final int[] X = {
        560, 500, 440, 380, 320, 260, 200, 140, 80, 20,
        20, 20, 80, 140, 200, 260, 320, 380, 440, 500,
        560, 560, 560, 560, 560, 560, 560, 560, 560, 560,
        560, 560, 560, 560
    };
    private static final int[] Y = {
        620, 620, 620, 620, 620, 620, 620, 620, 620, 620,
        560, 500, 440, 380, 320, 260, 200, 140, 80, 20,
        20, 80, 140, 200, 260, 320, 380, 440, 500, 560,
        620, 620, 620, 620
    };

    public static int getX(int pos, int offset) {
        return X[pos % X.length] + offset * 10;
    }

    public static int getY(int pos, int offset) {
        return Y[pos % Y.length];
    }
}
