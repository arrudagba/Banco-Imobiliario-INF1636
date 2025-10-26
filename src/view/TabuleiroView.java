package view;

import controller.GameController;
import model.Jogador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.util.List;


public class TabuleiroView extends Canvas implements Runnable, KeyListener {

    private final GameController controller;
    private final Frame frame;
    private final Image tabuleiro;
    private final Image[] pinos = new Image[6];
    private final Image[] dados = new Image[6];
    private final Color[] coresJogadores = {
            Color.RED, Color.BLUE, Color.ORANGE, Color.YELLOW, Color.MAGENTA, Color.GRAY
    };

    private Thread gameLoop;
    private boolean running = true;

    // layout
    private final int WIDTH = 1200;
    private final int HEIGHT = 700;
    private final int DIVIDER = 640;

    // notificações
    private final String[] notificacoes = new String[10];

    // componentes Swing permitidos
    private JButton botaoLancar;
    private JButton botaoSalvar;
    private JButton botaoEncerrar;
    private JComboBox<String> comboPropriedades;

    public TabuleiroView(GameController controller) {
        this.controller = controller;

        frame = new Frame("Banco Imobiliário");
        frame.setSize(WIDTH, HEIGHT);
        frame.setBackground(new Color(180, 240, 180));
        frame.setLayout(null); // layout absoluto
        frame.add(this);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                running = false;
                frame.dispose();
            }
        });

        setFocusable(true);
        addKeyListener(this);
        setBounds(0, 0, WIDTH, HEIGHT);

        // carregar imagens
        tabuleiro = Images.get("tabuleiro");
        for (int i = 0; i < 6; i++) {
            pinos[i] = Images.get("pin" + i);
            dados[i] = Images.get("dado" + (i + 1));
        }

        for (int i = 0; i < notificacoes.length; i++)
            notificacoes[i] = "-";

        criarComponentesSwing();

        frame.setVisible(true);

        // iniciar loop
        gameLoop = new Thread(this);
        gameLoop.start();
    }

    private void criarComponentesSwing() {
        // --- Botão "Lançar Dados" ---
        botaoLancar = new JButton("Lançar Dados");
        botaoLancar.setBounds(DIVIDER + 310, 150, 170, 30);
        botaoLancar.addActionListener(e -> {
            controller.rolarDados();
            int d1 = controller.getDado1();
            int d2 = controller.getDado2();
            addNotificacao("Jogador lançou: " + d1 + " + " + d2);
            controller.processarJogadaComValores(d1, d2);
        });
        frame.add(botaoLancar);

        // --- Botão "Salvar" ---
        botaoSalvar = new JButton("Salvar Partida");
        botaoSalvar.setBounds(DIVIDER + 310, HEIGHT - 125, 170, 30);
        botaoSalvar.addActionListener(e -> addNotificacao("Partida salva!"));
        frame.add(botaoSalvar);

        // --- Botão "Encerrar" ---
        botaoEncerrar = new JButton("Encerrar Partida");
        botaoEncerrar.setBounds(DIVIDER + 310, HEIGHT - 85, 170, 30);
        botaoEncerrar.addActionListener(e -> {
            addNotificacao("Partida encerrada.");
            running = false;
            frame.dispose();
        });
        frame.add(botaoEncerrar);

        // --- ComboBox de propriedades ---
        comboPropriedades = new JComboBox<>();
        comboPropriedades.setBounds(DIVIDER + 45, HEIGHT - 110, 200, 30);
        comboPropriedades.addItem("Selecione uma propriedade...");
        comboPropriedades.addActionListener(e -> {
            String selecionada = (String) comboPropriedades.getSelectedItem();
            if (selecionada != null && !selecionada.startsWith("Selecione")) {
                addNotificacao("Propriedade: " + selecionada);
            }
        });
        frame.add(comboPropriedades);
    }

    @Override
    public void run() {
        createBufferStrategy(2);
        BufferStrategy bs = getBufferStrategy();

        while (running) {
            Graphics2D g = (Graphics2D) bs.getDrawGraphics();
            render(g);
            g.dispose();
            bs.show();

            try {
                Thread.sleep(33);
            } catch (InterruptedException ignored) {}
        }
    }

    private void render(Graphics2D g) {
        // fundo geral
        g.setColor(new Color(180, 240, 180));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // tabuleiro
        g.drawImage(tabuleiro, 20, 40, 620, 620, this);

        // divisor
        g.setColor(Color.DARK_GRAY);
        g.fillRect(DIVIDER - 2, 0, 4, HEIGHT);

        List<Jogador> jogadores = controller.getJogadores();
        int jogadorDaVez = controller.getJogadores().indexOf(controller.getJogadorDaVez());
        Color corAtual = coresJogadores[jogadorDaVez % coresJogadores.length];

        // saldos
        int saldosX = DIVIDER + 40;
        int saldosY = 50;
        g.setColor(new Color(210, 230, 210));
        g.fillRect(saldosX - 10, saldosY + 10, 220, 35 * jogadores.size());
        g.setColor(Color.BLACK);
        g.drawRect(saldosX - 10, saldosY + 10, 220, 35 * jogadores.size());

        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Saldos:", saldosX, saldosY);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        int ySaldo = saldosY + 35;
        for (Jogador j : jogadores) {
            g.setColor(corAtual.equals(corJogador(j)) ? corAtual : Color.BLACK);
            g.drawString(j.getNome() + ": $" + j.getSaldo(), saldosX, ySaldo);
            ySaldo += 30;
        }

        // dados
        int dadosX = DIVIDER + 325;
        int dadosY = 70;
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Vez de: " + controller.getJogadorDaVez().getNome(), dadosX - 10, dadosY - 10);

        g.setColor(corAtual);
        g.fillRect(dadosX - 10, dadosY - 10, 180, 120);
        g.setColor(Color.BLACK);
        g.drawRect(dadosX - 10, dadosY - 10, 180, 120);

        int d1 = controller.getDado1();
        int d2 = controller.getDado2();
        if (dados[d1 - 1] != null) g.drawImage(dados[d1 - 1], dadosX, dadosY, 80, 80, this);
        if (dados[d2 - 1] != null) g.drawImage(dados[d2 - 1], dadosX + 90, dadosY, 80, 80, this);

        // notificações
        int notifX = DIVIDER + 280;
        int notifY = HEIGHT - 350;
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Notificações:", notifX, notifY - 5);

        g.setColor(new Color(210, 230, 210));
        g.fillRect(notifX, notifY, 250, 230);
        g.setColor(Color.BLACK);
        g.drawRect(notifX, notifY, 250, 230);

        int ny = notifY + 22;
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        for (int i = notificacoes.length - 1; i >= 0; i--) {
            g.drawString(notificacoes[i], notifX + 5, ny);
            ny += 22;
        }

        // peões no tabuleiro
        for (int i = 0; i < jogadores.size(); i++) {
            Jogador j = jogadores.get(i);
            int pos = j.getPosicao();
            int x = getCasaX(pos, i);
            int y = getCasaY(pos, i);
            g.drawImage(pinos[i], x, y, 15, 22, this);
        }
    }

    private Color corJogador(Jogador j) {
        int idx = controller.getJogadores().indexOf(j);
        return coresJogadores[idx % coresJogadores.length];
    }

    private void addNotificacao(String msg) {
        for (int i = notificacoes.length - 1; i > 0; i--)
            notificacoes[i] = notificacoes[i - 1];
        notificacoes[0] = "- " + msg;
    }

    // coordenadas simples
    private int getCasaX(int pos, int offset) {
        int[] X = {560,500,440,380,320,260,200,140,80,20,20,20,80,140,200,260,320,380,440,500,560};
        return 20 + X[pos % X.length] + offset * 10;
    }

    private int getCasaY(int pos, int offset) {
        int[] Y = {620,620,620,620,620,620,620,620,620,620,560,500,440,380,320,260,200,140,80,20,20};
        return 40 + Y[pos % Y.length];
    }

    // eventos de teclado (não usados)
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}
