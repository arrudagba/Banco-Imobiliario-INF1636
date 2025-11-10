package view;

import controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


public class FrameNomesView implements ActionListener {

    private final Frame frame;
    private final int nJogadores;
    private final java.util.List<JTextField> camposTexto = new ArrayList<>();
    private final JButton botaoIniciarPartida;
    private final JCheckBox godModeBox;
    private final JLabel nameError;
    private final JLabel idError;

    private boolean godMode = false;
    private final GameController controller;

    private final Color[] cores = {Color.RED, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.MAGENTA, Color.GRAY};
    private final Color corPadrao = new Color(180, 240, 180);
    private final Color corErro = new Color(189, 68, 28);

    public FrameNomesView(GameController controller, int nJogadores) {
        this.controller = controller;
        this.nJogadores = nJogadores;

        frame = new Frame("Banco Imobiliário - Jogadores");
        frame.setBackground(corPadrao);
        frame.setLayout(null);
        frame.setResizable(false);

        int altura = 100 + nJogadores * 50;
        frame.setSize(350, altura + 150);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - frame.getWidth()) / 2;
        int y = (screen.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);

        // Campos de nome
        int yAtual = 50;
        for (int i = 0; i < nJogadores; i++) {
            JTextField campo = new JTextField("Player" + (i + 1));
            campo.setBackground(cores[i % cores.length]);
            campo.setBounds(60, yAtual, 220, 30);
            camposTexto.add(campo);
            frame.add(campo);
            yAtual += 45;
        }

        // Caixa god mode
        godModeBox = new JCheckBox("Manipular dados do jogo");
        godModeBox.setBackground(corPadrao);
        godModeBox.setBounds(80, yAtual, 200, 25);
        godModeBox.addActionListener(this);
        frame.add(godModeBox);
        yAtual += 40;

        // Botão iniciar
        botaoIniciarPartida = new JButton("Iniciar Partida");
        botaoIniciarPartida.setBounds(100, yAtual, 140, 30);
        botaoIniciarPartida.addActionListener(this);
        frame.add(botaoIniciarPartida);
        yAtual += 50;

        // Mensagens de erro
        nameError = new JLabel("Nomes devem conter 1 a 8 caracteres alfanuméricos", SwingConstants.CENTER);
        nameError.setForeground(corErro);
        nameError.setBounds(15, yAtual, 320, 25);
        nameError.setVisible(false);
        frame.add(nameError);

        idError = new JLabel("Nomes dos jogadores devem ser únicos", SwingConstants.CENTER);
        idError.setForeground(corErro);
        idError.setBounds(15, yAtual + 25, 320, 25);
        idError.setVisible(false);
        frame.add(idError);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                frame.dispose();
            }
        });

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();

        if (obj.equals(botaoIniciarPartida)) {
            nameError.setVisible(false);
            idError.setVisible(false);

            Set<String> setNomes = new HashSet<>();
            String[] nomes = new String[nJogadores];

            for (int i = 0; i < nJogadores; i++) {
                String nome = camposTexto.get(i).getText().trim();

                // Validação de nome
                if (nome.length() < 1 || nome.length() > 8 || !nome.matches("^[a-zA-Z0-9]*$")) {
                    nameError.setVisible(true);
                    return;
                }

                // Verifica nomes duplicados
                if (setNomes.contains(nome)) {
                    idError.setVisible(true);
                    return;
                }

                setNomes.add(nome);
                nomes[i] = nome;
            }
            
            controller.setModoManual(godModeBox.isSelected());

            controller.iniciarNovaPartida(nomes);
            frame.dispose();
        } else if (obj.equals(godModeBox)) {
            godMode = !godMode;
        }
    }
}
