package view;

import controller.GameController;
import model.Jogador;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TabuleiroPanel extends JPanel {

    private final GameController controller;
    private final Image tabuleiroImg;
    private final Image[] pinos;

    public TabuleiroPanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(800, 700));

        tabuleiroImg = Images.get("tabuleiro");
        pinos = new Image[6];
        for (int i = 0; i < 6; i++) {
            pinos[i] = Images.get("pin" + i);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(tabuleiroImg, 0, 0, 650, 650, this);

        List<Jogador> jogadores = controller.getJogadores();
        for (int i = 0; i < jogadores.size(); i++) {
            Jogador j = jogadores.get(i);
            int pos = j.getPosicao();
            int x = CoordenadasCasa.getX(pos, i);
            int y = CoordenadasCasa.getY(pos, i);
            g.drawImage(pinos[i], x, y, 20, 25, this);
        }
    }
}
