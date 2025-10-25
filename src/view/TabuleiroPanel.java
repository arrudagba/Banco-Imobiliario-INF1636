package view;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;

import controller.GameController;
import model.Carta;
import model.Jogador;

/**
 * Painel responsável por desenhar:
 * - Tabuleiro (imagem)
 * - HUD lateral com:
 *   - dados atuais
 *   - jogador da vez
 *   - carta de Sorte/Revés comprada
 * - Pino do jogador da vez no tabuleiro
 *
 * Tudo via Java2D (paintComponent), sem colocar componentes Swing.
 */
public class TabuleiroPanel extends JPanel {

    private static final int LARGURA = 1280;
    private static final int ALTURA  = 800;

    private final GameController controller;

    public TabuleiroPanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(LARGURA, ALTURA));
        setBackground(new Color(30, 30, 30));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2 = (Graphics2D) g;

        // dimensões onde vamos desenhar o tabuleiro original
        final int boardX = 0;
        final int boardY = 0;
        final int boardW = 800;
        final int boardH = 800;

        // desenha o tabuleiro
        Image imgTabuleiro = Assets.tabuleiro();
        g2.drawImage(imgTabuleiro, boardX, boardY, boardW, boardH, this);

        // área HUD lateral (status do turno, dados, carta, etc.)
        final int hudX = 820;
        final int hudY = 20;
        final int hudW = 440;
        final int hudH = 760;

        // fundo do HUD
        g2.setColor(new Color(18, 18, 18));
        g2.fillRoundRect(hudX - 10, hudY - 10, hudW + 20, hudH + 20, 18, 18);

        // faixa superior destacando jogador da vez
        g2.setColor(new Color(200, 50, 50)); // pode mais tarde ser a cor real do jogador
        g2.fillRoundRect(hudX, hudY, hudW, 120, 16, 16);

        // info de jogador da vez e dados atuais
        int d1 = controller.getDado1();
        int d2 = controller.getDado2();
        Jogador atual = controller.getJogadorDaVez();

        Image imgD1 = Assets.dadoFace(d1);
        Image imgD2 = Assets.dadoFace(d2);

        int dadoSize = 96;
        int dadoGap  = 20;
        int dadosY   = hudY + 12;

        // desenha os dados
        g2.drawImage(imgD1, hudX + 20, dadosY + 12, dadoSize, dadoSize, this);
        g2.drawImage(imgD2, hudX + 20 + dadoSize + dadoGap, dadosY + 12, dadoSize, dadoSize, this);

        // escreve textos sobre jogador e dados
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));

        String nomeJogador = (atual != null ? atual.getNome() : "(sem jogador)");
        g2.drawString("Jogador da vez: " + nomeJogador, hudX + 20, hudY + 130);
        g2.drawString("Dados: " + d1 + " + " + d2,         hudX + 20, hudY + 150);

        // desenha carta sorte/revés mais recente (se houver)
        Carta ultima = controller.getUltimaCartaComprada();
        if (ultima != null && ultima.getIdImagem() != null) {
            Image cartaImg = Assets.cartaSorteReves(ultima.getIdImagem());
            if (cartaImg != null) {
                int cartaW = 220;
                int cartaH = 330;
                int cartaX = hudX + 20;
                int cartaY = hudY + 180;

                g2.drawImage(cartaImg, cartaX, cartaY, cartaW, cartaH, this);

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g2.drawString("Carta: " + ultima.getIdImagem(), cartaX, cartaY + cartaH + 16);
            }
        }

        // desenha o pino do jogador atual no tabuleiro
        // OBS: por enquanto, vamos só posicionar o pino fixo num canto,
        // porque o mapeamento de casa->coordenada ainda não foi implementado.
        int idxPino = controller.getIndicePinoJogadorDaVez();
        Image pinoImg = Assets.pinoIndex(idxPino);
        if (pinoImg != null) {
            int pinoW = 28;
            int pinoH = 28;

            // posição demonstrativa: canto inferior direito do tabuleiro
            // depois você troca por coordenadas reais baseadas na casa do jogador.
            int pinoX = boardX + boardW - 50;
            int pinoY = boardY + boardH - 50;

            g2.drawImage(pinoImg, pinoX, pinoY, pinoW, pinoH, this);
        }

        // (Opcional para esta iteração)
        // Se você quiser mostrar a carta da propriedade em que o jogador caiu,
        // você cria no controller algo como getNomePropriedadeAtual() que retorna
        // exatamente o nome do arquivo sem .png, tipo "Copacabana" ou "Av. Paulista".
        //
        // String propriedade = controller.getNomePropriedadeAtual();
        // if (propriedade != null) {
        //     Image cartaProp = Assets.territorioPorNomeExato(propriedade);
        //     if (cartaProp != null) {
        //         int propW = 180;
        //         int propH = 260;
        //         int propX = hudX + 260;
        //         int propY = hudY + 180;
        //         g2.drawImage(cartaProp, propX, propY, propW, propH, this);
        //     }
        // }
    }
}
