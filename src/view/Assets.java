package view;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class Assets {

    private static final String ROOT = "/imagens";
    private static final Map<String, Image> cache = new HashMap<>();

    private Assets() {}

    /* ----------------- util interno ----------------- */
    private static Image load(String path) {
        return cache.computeIfAbsent(path, p -> {
            URL url = Assets.class.getResource(p);
            if (url == null) {
                System.err.println("ERRO: imagem não encontrada em: " + p);
                return new ImageIcon().getImage();
            }
            return new ImageIcon(url).getImage();
        });
    }

    /* ----------------- básicos ----------------- */
    public static Image tabuleiro() {
        return load(ROOT + "/tabuleiro.png");
    }

    /* ----------------- dados ----------------- */
    // nomes: die_face_1.png ... die_face_6.png
    public static Image dadoFace(int valor1a6) {
        int v = Math.max(1, Math.min(6, valor1a6));
        return load(ROOT + "/dados/die_face_" + v + ".png");
    }

    /* ----------------- pinos ----------------- */
    // nomes: pin0.png .. pin5.png (6 pinos)
    public static Image pinoIndex(int idx0a5) {
        int i = Math.max(0, Math.min(5, idx0a5));
        return load(ROOT + "/pinos/pin" + i + ".png");
    }

    /* ----------------- sorte/revés ----------------- */
    // nomes: chance1.png .. chance30.png
    // use o idImagem da Carta igual a "chanceN"
    public static Image cartaSorteReves(String idImagem) {
        // idImagem esperado: "chance12", "chance3" etc.
        return load(ROOT + "/sorteReves/" + idImagem + ".png");
    }

    /* ----------------- companhias ----------------- */
    // nomes: company1.png .. company6.png
    public static Image companhiaPorIndice(int idx1a6) {
        int i = Math.max(1, Math.min(6, idx1a6));
        return load(ROOT + "/companhias/company" + i + ".png");
    }

    /* ----------------- territórios ----------------- */
    // Os arquivos têm nomes EXACTOS com espaços/acentos, ex: "Av. Paulista.png", "Copacabana.png"
    // Passe exatamente o nome do arquivo sem extensão como vem da sua modelagem.
    public static Image territorioPorNomeExato(String nomeArquivoSemPng) {
        return load(ROOT + "/territorios/" + nomeArquivoSemPng + ".png");
    }
}
