package view;

import javax.swing.*;
import java.awt.*;
import controller.GameController;

public class JanelaTabuleiro extends JFrame {

    private final GameController controller;
    private final TabuleiroPanel painel;
    private final JComboBox<Integer> cbD1;
    private final JComboBox<Integer> cbD2;
    private final JButton btAplicar;

    public JanelaTabuleiro(GameController controller) {
        super("Banco Imobiliário - Partida");
        this.controller = controller;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // painel de desenho (tabuleiro, HUD lateral etc.)
        painel = new TabuleiroPanel(controller);
        add(painel, BorderLayout.CENTER);

        // barra superior de controle (permitido pelo professor)
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbD1 = new JComboBox<>(new Integer[]{1,2,3,4,5,6});
        cbD2 = new JComboBox<>(new Integer[]{1,2,3,4,5,6});
        btAplicar = new JButton("Aplicar dados");

        btAplicar.addActionListener(e -> {
            int d1 = (int) cbD1.getSelectedItem();
            int d2 = (int) cbD2.getSelectedItem();

            // atualiza o estado do jogo para este turno:
            // - registra os dados
            // - movimentação (futuro)
            // - compra carta se cair em Sorte/Revés e guarda em baralho.ultimaCarta
            controller.processarJogadaComValores(d1, d2);

            // redesenha imediatamente o painel com:
            // - imagens dos dados corretas
            // - pino do jogador da vez
            // - carta sorteada (se houve)
            painel.repaint();
        });

        barra.add(new JLabel("D1:"));
        barra.add(cbD1);
        barra.add(new JLabel("D2:"));
        barra.add(cbD2);
        barra.add(btAplicar);

        add(barra, BorderLayout.NORTH);
    }
}
