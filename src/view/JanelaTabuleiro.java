package view;

import controller.GameController;
import model.Jogador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class JanelaTabuleiro extends JFrame implements ActionListener {

    private final GameController controller;
    private final TabuleiroPanel painelTabuleiro;

    private final JButton botaoLancarDados;
    private final JLabel labelJogador;
    private final JLabel labelDados;
    private final JLabel labelUltimaCarta;
    private final JTextArea areaNotificacoes;
    private final JPanel painelSaldos;

    public JanelaTabuleiro(GameController controller) {
        this.controller = controller;

        setTitle("Banco Imobiliário");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1200, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(51, 185, 222));

        painelTabuleiro = new TabuleiroPanel(controller);
        add(painelTabuleiro, BorderLayout.CENTER);

        JPanel painelDireito = new JPanel();
        painelDireito.setPreferredSize(new Dimension(400, 700));
        painelDireito.setBackground(new Color(51, 185, 222));
        painelDireito.setLayout(new BoxLayout(painelDireito, BoxLayout.Y_AXIS));

        labelJogador = new JLabel("Vez de: " + controller.getJogadorDaVez().getNome(), SwingConstants.CENTER);
        labelJogador.setFont(new Font("SansSerif", Font.BOLD, 20));
        labelJogador.setAlignmentX(Component.CENTER_ALIGNMENT);

        botaoLancarDados = new JButton("Lançar Dados");
        botaoLancarDados.setAlignmentX(Component.CENTER_ALIGNMENT);
        botaoLancarDados.addActionListener(this);

        labelDados = new JLabel();
        labelDados.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelUltimaCarta = new JLabel("", SwingConstants.CENTER);
        labelUltimaCarta.setAlignmentX(Component.CENTER_ALIGNMENT);

        areaNotificacoes = new JTextArea(10, 25);
        areaNotificacoes.setEditable(false);
        areaNotificacoes.setBackground(new Color(223, 239, 245));

        painelSaldos = new JPanel();
        painelSaldos.setBackground(new Color(223, 239, 245));
        atualizarSaldos();

        painelDireito.add(Box.createVerticalStrut(10));
        painelDireito.add(labelJogador);
        painelDireito.add(Box.createVerticalStrut(15));
        painelDireito.add(botaoLancarDados);
        painelDireito.add(Box.createVerticalStrut(15));
        painelDireito.add(labelDados);
        painelDireito.add(Box.createVerticalStrut(15));
        painelDireito.add(new JLabel("Notificações:"));
        painelDireito.add(new JScrollPane(areaNotificacoes));
        painelDireito.add(Box.createVerticalStrut(15));
        painelDireito.add(new JLabel("Saldos:"));
        painelDireito.add(painelSaldos);
        painelDireito.add(Box.createVerticalStrut(15));
        painelDireito.add(labelUltimaCarta);

        add(painelDireito, BorderLayout.EAST);
    }

    private void atualizarSaldos() {
        painelSaldos.removeAll();
        painelSaldos.setLayout(new GridLayout(0, 1));
        List<Jogador> jogadores = controller.getJogadores();
        for (Jogador j : jogadores) {
            JLabel lbl = new JLabel(j.getNome() + ": $" + j.getSaldo());
            lbl.setForeground(Color.BLACK);
            painelSaldos.add(lbl);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botaoLancarDados) {
            controller.rolarDados();
            painelTabuleiro.repaint();

            int d1 = controller.getDado1();
            int d2 = controller.getDado2();
            labelDados.setIcon(new ImageIcon(Images.get("die_face_" + d1)));
            areaNotificacoes.append("Dados: " + d1 + " + " + d2 + "\n");

            controller.processarJogadaComValores(d1, d2);
            atualizarSaldos();
            labelJogador.setText("Vez de: " + controller.getJogadorDaVez().getNome());
        }
    }
}
