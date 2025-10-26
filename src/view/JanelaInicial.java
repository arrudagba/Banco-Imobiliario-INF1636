package view;

import controller.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JanelaInicial extends JFrame implements ActionListener {

    private static final int ALTURA = 250;
    private static final int COMPRIMENTO = 250;
    private final Color corPadrao = new Color(51, 185, 222);

    private final JButton botaoNovaPartida;
    private final JComboBox<String> comboQuantidadeJogadores;

    private int nJogadores = 2;

    public JanelaInicial(GameController controller) {
        setTitle("Banco Imobiliário - Menu");
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screen = tk.getScreenSize();
        int x = (screen.width - COMPRIMENTO) / 2;
        int y = (screen.height - ALTURA) / 2;
        setBounds(x, y, COMPRIMENTO, ALTURA);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(corPadrao);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Bem-vindo ao Banco Imobiliário!", SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        botaoNovaPartida = new JButton("Nova Partida");
        botaoNovaPartida.setAlignmentX(Component.CENTER_ALIGNMENT);
        botaoNovaPartida.addActionListener(e -> {
            controller.iniciarNovaPartida(nJogadores);
            dispose();
        });

        String[] opcoes = {"2 jogadores", "3 jogadores", "4 jogadores", "5 jogadores", "6 jogadores"};
        comboQuantidadeJogadores = new JComboBox<>(opcoes);
        comboQuantidadeJogadores.setAlignmentX(Component.CENTER_ALIGNMENT);
        comboQuantidadeJogadores.addActionListener(this);

        add(Box.createVerticalStrut(25));
        add(titulo);
        add(Box.createVerticalStrut(25));
        add(botaoNovaPartida);
        add(comboQuantidadeJogadores);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String selecionado = (String) comboQuantidadeJogadores.getSelectedItem();
        if (selecionado != null && !selecionado.isEmpty()) {
            nJogadores = Integer.parseInt(String.valueOf(selecionado.charAt(0)));
        }
    }
}
