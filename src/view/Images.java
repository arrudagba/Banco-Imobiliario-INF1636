package view;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

public class Images {
    private static final HashMap<String, Image> hash = new HashMap<>();
    private static final String path = "src/Imagens/";

    public static void carregar() {
        try {
            File base = new File(path);
            if (!base.exists()) {
                System.err.println("❌ Pasta de imagens não encontrada: " + base.getAbsolutePath());
                return;
            }

            // tabuleiro
            hash.put("tabuleiro", ImageIO.read(new File(base, "tabuleiro.png")));

            // pinos
            for (int i = 0; i <= 5; i++) {
                hash.put("pin" + i, ImageIO.read(new File(base, "pinos/pin" + i + ".png")));
            }

            // dados
            for (int i = 1; i <= 6; i++) {
                hash.put("dado" + i, ImageIO.read(new File(base, "dados/die_face_" + i + ".png")));
            }

            // companhias
            for (int i = 1; i <= 6; i++) {
                hash.put("companhia" + i, ImageIO.read(new File(base, "companhias/company" + i + ".png")));
            }

            // sorte/revés (30 cartas)
            for (int i = 1; i <= 30; i++) {
                File chanceFile = new File(base, "sorteReves/chance" + i + ".png");
                if (chanceFile.exists()) {
                    hash.put("chance" + i, ImageIO.read(chanceFile));
                }
            }

            // territórios
            File territDir = new File(base, "territorios/");
            if (territDir.exists()) {
                for (File t : territDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"))) {
                    String nome = t.getName().replace(".png", "");
                    hash.put("territorio_" + nome, ImageIO.read(t));
                }
            }

            System.out.println("✅ " + hash.size() + " imagens carregadas com sucesso.");

        } catch (IOException e) {
            System.err.println("Erro ao carregar imagens do Banco Imobiliário:");
            e.printStackTrace();
        }
    }

    public static Image get(String key) {
        Image img = hash.get(key);
        if (img == null) {
            System.err.println("⚠️ Imagem não encontrada no cache: " + key);
        }
        return img;
    }
}
