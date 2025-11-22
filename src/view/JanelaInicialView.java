package view;

import controller.GameController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class JanelaInicialView implements ActionListener {

    private final Frame frame;
    private final JButton botaoNovaPartida;
    private final JButton botaoCarregarPartida;
    private final JComboBox<String> comboQuantidadeJogadores;
    private final Color corPadrao = new Color(180, 240, 180);

    private int nJogadores = 3;
	private GameController controller;

    public JanelaInicialView(GameController controller) {
        this.controller = controller;

        frame = new Frame("Banco Imobiliário - Menu");
        frame.setSize(300, 300);
        frame.setBackground(corPadrao);
        frame.setLayout(null);
        frame.setResizable(false);

        // Centralizar na tela
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - frame.getWidth()) / 2;
        int y = (screen.height - frame.getHeight()) / 2;
        frame.setLocation(x, y);

        // Título 
        Label titulo = new Label("Bem-vindo ao Banco Imobiliário!", Label.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        titulo.setBounds(20, 40, 260, 30);
        frame.add(titulo);

        // Botão iniciar 
        botaoNovaPartida = new JButton("Nova Partida");
        botaoNovaPartida.setBounds(80, 100, 140, 30);
        botaoNovaPartida.addActionListener(e -> {
            frame.setVisible(false);
            new FrameNomesView(controller, nJogadores);
        });
        frame.add(botaoNovaPartida);
        
        // Botão carregar partida
        botaoCarregarPartida = new JButton("Carregar Partida");
        botaoCarregarPartida.setBounds(80, 140, 140, 30);
        botaoCarregarPartida.addActionListener(e -> {
            boolean sucesso = controller.carregarJogo();
            if (sucesso) {
                frame.setVisible(false);
                frame.dispose();
                controller.iniciarPartidaCarregada();
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Erro ao carregar partida!", 
                    "Erro", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        frame.add(botaoCarregarPartida);

        // ComboBox de quantidade de jogadores
        String[] opcoes = {"3 jogadores", "4 jogadores", "5 jogadores", "6 jogadores"};
        comboQuantidadeJogadores = new JComboBox<>(opcoes);
        comboQuantidadeJogadores.setBounds(80, 180, 140, 25);
        comboQuantidadeJogadores.addActionListener(this);
        frame.add(comboQuantidadeJogadores);

        // Listener para fechar corretamente
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                frame.dispose();
            }
        });

        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String selecionado = (String) comboQuantidadeJogadores.getSelectedItem();
        if (selecionado != null && !selecionado.isEmpty()) {
            nJogadores = Integer.parseInt(String.valueOf(selecionado.charAt(0)));
        }
    }
}
