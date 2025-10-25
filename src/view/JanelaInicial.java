package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import controller.GameController;

public class JanelaInicial extends JFrame {

    private JComboBox<Integer> comboQtdJogadores;
    private JButton botaoIniciar;

    public JanelaInicial(GameController controller) {
        super("Banco Imobiliário - Nova Partida");

        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centraliza

        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Selecione o número de jogadores (3 a 6):", SwingConstants.CENTER);
        add(titulo, BorderLayout.NORTH);

        comboQtdJogadores = new JComboBox<>(new Integer[]{3,4,5,6});
        JPanel centro = new JPanel();
        centro.add(comboQtdJogadores);
        add(centro, BorderLayout.CENTER);

        botaoIniciar = new JButton("Iniciar Partida");
        add(botaoIniciar, BorderLayout.SOUTH);

        botaoIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int qtd = (int) comboQtdJogadores.getSelectedItem();
                controller.iniciarNovaPartida(qtd);
                dispose(); // fecha a janela inicial
            }
        });
    }
}
