package view;

import controller.GameController;
import model.Jogador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


public class TabuleiroView extends JPanel implements Runnable, KeyListener {

    private final GameController controller;
    private final JFrame frame;
    private final int BOTTOM_BAR_H = 80; 
    private final Image tabuleiro;
    private String[] nomes;
    private int nJogadores;
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
    String[] descricaoDaVez = {""};

    public TabuleiroView(GameController controller) {
    	
        this.controller = controller;
        this.nJogadores = controller.getJogadores().size();
        this.nomes = controller.getJogadores().stream()
                .map(Jogador::getNome)
                .toArray(String[]::new);
		
        frame = new JFrame("Banco Imobiliário");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setBackground(new Color(180, 240, 180));
        frame.setLayout(null); // layout absoluto
        frame.getContentPane().add(this);
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
        setLayout(null);
        setDoubleBuffered(true);

        // carregar imagens
        tabuleiro = Images.get("tabuleiro");
        for (int i = 0; i < 6; i++) {
            pinos[i] = Images.get("pin" + i);
            dados[i] = Images.get("dado" + (i + 1));
        }

        for (int i = 0; i < notificacoes.length; i++)
            notificacoes[i] = "-";

        criarComponentesSwing();
        preencherComboPropriedades();
        frame.setVisible(true);

        // iniciar loop
        gameLoop = new Thread(this);
        gameLoop.start();
    }

    @Override
    public void run() {
        while (running) {
            repaint();
            try { Thread.sleep(33); } catch (InterruptedException ignored) {}
        }
    }

    public void criarComponentesSwing(){
    	
    	// Dados
    	botaoLancar = new JButton("Lançar Dados");
        botaoLancar.setBounds(DIVIDER + 320 , 195, 150, 30);
        botaoLancar.addActionListener(e -> {  
        	controller.rolarDados();
            int d1 = controller.getDado1();
            int d2 = controller.getDado2();
            int soma = controller.getSoma();
            addNotificacao("Jogador lançou: " + soma);
            controller.processarJogadaComValores(d1, d2, soma);
        });
           
    	// Salvar Jogo
        botaoSalvar = new JButton("Salvar Jogo");
        botaoSalvar.setBounds(DIVIDER + 230, HEIGHT - 85, 150, 30);
        botaoSalvar.addActionListener(e -> addNotificacao("Jogo salvo."));
  

        // Encerrar Jogo
        botaoEncerrar = new JButton("Encerrar Jogo");
        botaoEncerrar.setBounds(DIVIDER + 385, HEIGHT - 85, 150, 30); // 10px de espaçamento
        botaoEncerrar.addActionListener(e -> {
            addNotificacao("Partida encerrada.");
            running = false;
            frame.dispose();
        });
        
        // Popriedades
        comboPropriedades = new JComboBox<>();
        comboPropriedades.setBounds(DIVIDER + 45, HEIGHT - 110, 200, 30);
        comboPropriedades.addItem("Selecione uma propriedade...");
        comboPropriedades.addActionListener(e -> {
        	String selecionada = (String) comboPropriedades.getSelectedItem();
            if (selecionada != null && !selecionada.startsWith("(")) {
                descricaoDaVez = new String[]{
                    "Preço: $" + controller.getPrecoPropJogadorDaVez(selecionada),
                    "Titular: " + controller.getTitularPropJogadorDaVez(selecionada),
                    "Casas: " + controller.getCasasPropJogadorDaVez(selecionada),
                    "Hoteis: " + controller.getHoteisPropJogadorDaVez(selecionada)
                };
                addNotificacao("Propriedade selecionada: " + selecionada);
                repaint();
            }
        });
        
        
        // Adicionando botoes
        this.add(botaoLancar);
        this.add(botaoSalvar);
        this.add(botaoEncerrar);
        this.add(comboPropriedades);
    }
    
    private void preencherComboPropriedades() {
        comboPropriedades.removeAllItems();
        String[] propriedades = controller.getNomesPropriedadesJogadorDaVez();
        if (propriedades.length == 0) {
            comboPropriedades.addItem("(sem propriedades)");
        } else {
            for (String p : propriedades) comboPropriedades.addItem(p);
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
        g.fillRect(DIVIDER + 15, 0, 4, HEIGHT);

        List<Jogador> jogadores = controller.getJogadores();
        int jogadorDaVez = controller.getJogadores().indexOf(controller.getJogadorDaVez());
        Color corAtual = coresJogadores[jogadorDaVez % coresJogadores.length];

        // saldos
        int saldosX = DIVIDER + 40;
        int saldosY = 60;
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
        int dadosX = DIVIDER + 295;
        int dadosY = 65;
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Vez de: " + controller.getJogadorDaVez().getNome(), dadosX + 10, dadosY - 5);

        g.setColor(corAtual);
        g.fillRect(dadosX, dadosY, 200, 120);
        g.setColor(Color.BLACK);
        g.drawRect(dadosX, dadosY, 200, 120);

        int d1 = controller.getDado1();
        int d2 = controller.getDado2();
        if (dados[d1 - 1] != null) g.drawImage(dados[d1 - 1], dadosX + 15, dadosY + 20, 80, 80, this);
        if (dados[d2 - 1] != null) g.drawImage(dados[d2 - 1], dadosX + 100, dadosY + 20, 80, 80, this);
        
        

        
        // notificações
        int notifX = DIVIDER + 295;
        int notifY = HEIGHT - 400;
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Notificações:", notifX, notifY - 5);

        g.setColor(new Color(210, 230, 210));
        g.fillRect(notifX, notifY, 200, 230);
        g.setColor(Color.BLACK);
        g.drawRect(notifX, notifY, 200, 230);

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
        
        for (int i=0; i< descricaoDaVez.length; i++) {
			if (i==1 && ! descricaoDaVez[i].equals("Titular: sem titular")) {  // mudar para a cor do jogador titular
				for (int j=0; j< nJogadores; j++) {
					if (("Titular: " + nomes[j]).equals(descricaoDaVez[i])) {
						g.setColor(coresJogadores[j]);
						break;
					}
				}
			}
			g.drawString(descricaoDaVez[i], 735, 590 + i*20);
			g.setColor(Color.BLACK);
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
    
    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        render(g); // reaproveita seu método de desenho atual
    }

    // eventos de teclado (não usados)
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}
