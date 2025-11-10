package view;

import controller.GameController;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.ModuleLayer.Controller;
import java.util.List;

public class TabuleiroView extends JPanel implements Runnable, KeyListener {

    private final GameController controller;
    private final JFrame frame;
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

    private final int WIDTH = 1200;
    private final int HEIGHT = 700;
    private final int DIVIDER = 640;

    private final String[] notificacoes = new String[10];

    private JButton botaoLancar;
    private JButton botaoSalvar;
    private JButton botaoEncerrar;
    private JComboBox<String> comboPropriedades;

    
    // Dados
    private JComboBox<Integer> comboDado1;
    private JComboBox<Integer> comboDado2;

    private String[] descricaoDaVez = {""};
    private Image imagemPropriedadeSelecionada = null;


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
        frame.setLayout(null);
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
    	
    	// Combos de dados (só aparecem se modo manual estiver ativo)
    	
    	if (controller.isModoManual()) {
    	    comboDado1 = new JComboBox<>();
    	    comboDado2 = new JComboBox<>();

    	    for (int i = 1; i <= 6; i++) {
    	        comboDado1.addItem(i);
    	        comboDado2.addItem(i);
    	    }

    	    comboDado1.setBounds(DIVIDER + 320, 240, 60, 30);
    	    comboDado2.setBounds(DIVIDER + 410, 240, 60, 30);

    	    this.add(comboDado1);
    	    this.add(comboDado2);
    	}
    	
    	// Dados
    	botaoLancar = new JButton("Lançar Dados");
        botaoLancar.setBounds(DIVIDER + 320 , 195, 150, 30);
        Jogador player = controller.getJogadorDaVez();
        botaoLancar.addActionListener(e -> {  
        	if (controller.isModoManual()) {
        		
        		int d1 = (Integer) comboDado1.getSelectedItem();
                int d2 = (Integer) comboDado2.getSelectedItem();
                int soma = controller.getSoma();
                addNotificacao("Jogador escolheu dados: " + soma);
                controller.processarJogadaComValores(d1, d2, soma);
                moverJogadorEProcessarCasa(player, soma);
                
        	}
        	else {
        		
        		controller.rolarDados();
                int d1 = controller.getDado1();
                int d2 = controller.getDado2();
                int soma = controller.getSoma();
                addNotificacao(player.getNome() + " lançou: " + soma);
                controller.processarJogadaComValores(d1, d2, soma);
                moverJogadorEProcessarCasa(player, soma);
                
        	}
        	
            preencherComboPropriedades();
            repaint();
        });

        // Salvar
        botaoSalvar = new JButton("Salvar Jogo");
        botaoSalvar.setBounds(DIVIDER + 230, HEIGHT - 85, 150, 30);
        botaoSalvar.addActionListener(e -> {
            var path = controller.salvarJogo();
            if (path != null) addNotificacao("Jogo salvo em: " + path.toString());
            else addNotificacao("Falha ao salvar jogo.");
        });

        // Encerrar
        botaoEncerrar = new JButton("Encerrar Jogo");
        botaoEncerrar.setBounds(DIVIDER + 385, HEIGHT - 85, 150, 30);
        botaoEncerrar.addActionListener(e -> {
            addNotificacao("Partida encerrada.");
            running = false;
            frame.dispose();
        });
        
        // Propriedades
        comboPropriedades = new JComboBox<>();
        comboPropriedades.setBounds(DIVIDER + 50, HEIGHT - 420, 200, 30);
        comboPropriedades.addItem("Selecione uma propriedade...");
        comboPropriedades.addActionListener(e -> {
            String selecionada = (String) comboPropriedades.getSelectedItem();
            if (selecionada != null && !selecionada.startsWith("(") && !selecionada.startsWith("Selecione")) {
                int pos = posicaoDaCasaPorNome(selecionada);
                if (pos >= 0) {
                    descricaoDaVez = controller.getDescricao(pos);      
                    imagemPropriedadeSelecionada = getImagemPropriedade(selecionada);
                    repaint();
                }
                
            }
        });

        this.add(botaoLancar);
        this.add(botaoSalvar);
        this.add(botaoEncerrar);
        this.add(comboPropriedades);
    }



    private void passarVez(boolean repetir) {
        if (!repetir) {
            List<Jogador> jogadores = controller.getJogadores();
            int atual = jogadores.indexOf(controller.getJogadorDaVez());
            int proximo = (atual + 1) % jogadores.size();
            // O ModelFacade controla internamente, então usamos o método dele
            controller.getTabuleiro(); // Mantém sincronia
        }
    }

    private void moverJogadorEProcessarCasa(Jogador jogador, int casas) {
        int posAtual = jogador.getPosicao();
        int novaPos = (posAtual + casas) % controller.getTabuleiro().getTamanho();
        jogador.setPosicao(novaPos);
        
        // Verifica se passou pelo início
        if (novaPos < posAtual) {
            jogador.creditar(200);
            addNotificacao(jogador.getNome() + " passou pelo início! +$200");
        }
        
        Casa casa = controller.getTabuleiro().getCasa(novaPos);
        addNotificacao("Parou em: " + casa.getNome());
        
        processarCasa(jogador, casa);
    }

    private void processarCasa(Jogador jogador, Casa casa) {
        TipoCasa tipo = casa.getTipo();
        
        switch (tipo) {
            case PROPRIEDADE:
                processarCasaPropriedade(jogador, (CasaPropriedade) casa);
                break;
                
            case COMPANHIA:
                processarCasaCompanhia(jogador, (CasaCompanhia) casa);
                break;
                
            case SORTE_REVES:
                processarCasaSorteReves(jogador);
                break;
                
            case VA_PARA_PRISAO:
                jogador.setPreso(true);
                jogador.setPosicao(10); // Casa 10 = prisão
                jogador.setDuplasConsecutivas(0);
                addNotificacao(jogador.getNome() + " foi para a prisão!");
                break;
                
            case IMPOSTO:
                CasaImposto imposto = (CasaImposto) casa;
                jogador.debitar(200); // Valor fixo do imposto
                addNotificacao("Pagou imposto: $200");
                break;
                
            case RECEBIMENTO:
                jogador.creditar(200); // Lucros e dividendos
                addNotificacao("Recebeu: $200");
                break;
                
            case INICIO:
                // Já foi tratado ao passar pelo início
                break;
                
            default:
                break;
        }
    }

    private void processarCasaPropriedade(Jogador jogador, CasaPropriedade prop) {
        if (prop.getProprietario() == null) {
            // Propriedade sem dono - oferecer compra
            int resposta = JOptionPane.showConfirmDialog(
                frame,
                "Deseja comprar " + prop.getNome() + " por $" + prop.getPreco() + "?",
                "Propriedade à venda",
                JOptionPane.YES_NO_OPTION
            );
            
            if (resposta == JOptionPane.YES_OPTION) {
                if (jogador.getSaldo() >= prop.getPreco()) {
                    jogador.debitar(prop.getPreco());
                    prop.setProprietario(jogador);
                    jogador.addPropriedade(prop);
                    addNotificacao("Comprou: " + prop.getNome());
                } else {
                    addNotificacao("Saldo insuficiente!");
                }
            }
        } else if (prop.getProprietario() == jogador) {
            // É dono da propriedade
            addNotificacao("Você é o dono desta propriedade");
            
            // Pode oferecer opção de construir casa/hotel
            if (jogador.getSaldo() >= prop.getPreco()) {
                String[] opcoes = {"Não fazer nada", "Construir casa", "Construir hotel"};
                int resp = JOptionPane.showOptionDialog(
                    frame,
                    "O que deseja fazer com " + prop.getNome() + "?",
                    "Sua propriedade",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoes,
                    opcoes[0]
                );
                
                if (resp == 1) { // Construir casa
                    if (prop.construirCasa()) {
                        jogador.debitar(prop.getPreco());
                        addNotificacao("Construiu 1 casa em " + prop.getNome());
                    } else {
                        addNotificacao("Não pode construir mais casas");
                    }
                } else if (resp == 2) { // Construir hotel
                    if (prop.construirHotel()) {
                        jogador.debitar(prop.getPreco());
                        addNotificacao("Construiu 1 hotel em " + prop.getNome());
                    } else {
                        addNotificacao("Precisa de pelo menos 1 casa para construir hotel");
                    }
                }
            }
        } else {
            // Pagar aluguel
            int aluguel = calcularAluguelPropriedade(prop);
            if (jogador.getSaldo() >= aluguel) {
                jogador.debitar(aluguel);
                prop.getProprietario().creditar(aluguel);
                addNotificacao("Pagou aluguel: $" + aluguel + " para " + prop.getProprietario().getNome());
            } else {
                addNotificacao("Saldo insuficiente para pagar aluguel!");
                // Venda forçada
                venderPropriedadesForcado(jogador, aluguel, prop.getProprietario());
            }
        }
    }

    private int calcularAluguelPropriedade(CasaPropriedade prop) {
        int aluguelBase = prop.getPreco() / 10;
        if (prop.isTemHotel()) {
            return aluguelBase * 5;
        } else if (prop.getNumCasas() > 0) {
            return aluguelBase * (1 + prop.getNumCasas());
        }
        return aluguelBase;
    }

    private void processarCasaCompanhia(Jogador jogador, CasaCompanhia comp) {
        if (comp.getProprietario() == null) {
            int resposta = JOptionPane.showConfirmDialog(
                frame,
                "Deseja comprar " + comp.getNome() + " por $" + comp.getPreco() + "?",
                "Companhia à venda",
                JOptionPane.YES_NO_OPTION
            );
            
            if (resposta == JOptionPane.YES_OPTION) {
                if (jogador.getSaldo() >= comp.getPreco()) {
                    jogador.debitar(comp.getPreco());
                    comp.setProprietario(jogador);
                    addNotificacao("Comprou: " + comp.getNome());
                } else {
                    addNotificacao("Saldo insuficiente!");
                }
            }
        } else if (comp.getProprietario() != jogador) {
            int soma = controller.getSoma();
            int aluguel = soma * 10; // Aluguel = soma dos dados * 10
            if (jogador.getSaldo() >= aluguel) {
                jogador.debitar(aluguel);
                comp.getProprietario().creditar(aluguel);
                addNotificacao("Pagou taxa: $" + aluguel);
            } else {
                addNotificacao("Saldo insuficiente!");
                venderPropriedadesForcado(jogador, aluguel, comp.getProprietario());
            }
        }
    }

    private void venderPropriedadesForcado(Jogador jogador, int divida, Jogador credor) {
        if (jogador.getPropriedades().isEmpty()) {
            addNotificacao(jogador.getNome() + " faliu!");
            return;
        }
        
        String[] propriedades = jogador.getPropriedades().stream()
            .map(CasaPropriedade::getNome)
            .toArray(String[]::new);
        
        JComboBox<String> combo = new JComboBox<>(propriedades);
        int result = JOptionPane.showConfirmDialog(
            frame,
            combo,
            jogador.getNome() + " - Venda forçada para pagar dívida",
            JOptionPane.OK_CANCEL_OPTION
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String escolhida = (String) combo.getSelectedItem();
            for (CasaPropriedade p : jogador.getPropriedades()) {
                if (p.getNome().equals(escolhida)) {
                    int valorVenda = (int)(p.getPreco() * 0.5); // 50% do valor
                    jogador.creditar(valorVenda);
                    p.setProprietario(null);
                    jogador.getPropriedades().remove(p);
                    addNotificacao("Vendeu " + escolhida + " por $" + valorVenda);
                    
                    // Tenta pagar novamente
                    if (jogador.getSaldo() >= divida) {
                        jogador.debitar(divida);
                        credor.creditar(divida);
                        addNotificacao("Dívida paga!");
                    } else {
                        venderPropriedadesForcado(jogador, divida, credor);
                    }
                    break;
                }
            }
        }
    }

    private void processarCasaSorteReves(Jogador jogador) {
        Carta carta = controller.getUltimaCartaComprada();
        if (carta != null) {
            JOptionPane.showMessageDialog(
                frame,
                carta.getDescricao(),
                "Sorte ou Revés",
                JOptionPane.INFORMATION_MESSAGE
            );
            addNotificacao("Carta: " + carta.getDescricao());
        } else {
            addNotificacao("Sorte ou Revés!");
        }
    }

    private void preencherComboPropriedades() {
        comboPropriedades.removeAllItems();
        String[] propriedades = controller.getNomesTodasPropriedades();
        if (propriedades.length == 0) {
            comboPropriedades.addItem("(sem propriedades)");
        } else {
            comboPropriedades.addItem("Selecione uma propriedade...");
            for (String p : propriedades) {
                comboPropriedades.addItem(p);
            }
        }
    }

    private Image getImagemPropriedade(String nomePropriedade) {
        // Tenta carregar como território
        Image img = Images.get("territorio_" + nomePropriedade);
        if (img != null) return img;
        
        // Tenta como companhia
        for (int i = 1; i <= 6; i++) {
            img = Images.get("companhia" + i);
            if (img != null) return img;
        }
        
        return null;
    }

    private int posicaoDaCasaPorNome(String nome) {
        int n = controller.getTabuleiro().getTamanho();
        for (int pos = 0; pos < n; pos++) {
            if (controller.getTabuleiro().getCasa(pos).getNome().equals(nome)) return pos;
        }
        return -1;
    }

    private void render(Graphics2D g) {
        g.setColor(new Color(180, 240, 180));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.drawImage(tabuleiro, 20, 40, 620, 620, this);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(DIVIDER + 15, 0, 4, HEIGHT);

        List<Jogador> jogadores = controller.getJogadores();
        int jogadorDaVez = controller.getJogadores().indexOf(controller.getJogadorDaVez());
        Color corAtual = coresJogadores[jogadorDaVez % coresJogadores.length];

        // Saldos
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
            String status = j.isPreso() ? " [PRESO]" : "";
            g.drawString(j.getNome() + ": $" + j.getSaldo() + status, saldosX, ySaldo);
            ySaldo += 30;
        }

        // Dados
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

        // Notificações
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

        // Peões no tabuleiro
        for (int i = 0; i < jogadores.size(); i++) {
            Jogador j = jogadores.get(i);
            int pos = j.getPosicao();
            int x = getCasaX(pos, i);
            int y = getCasaY(pos, i);
            g.drawImage(pinos[i], x, y, 15, 22, this);
        }

        // Imagem da propriedade selecionada
        if (imagemPropriedadeSelecionada != null) {
            g.drawImage(imagemPropriedadeSelecionada, 715, 420, 132, 150, this);
        }

        // Descrição da propriedade
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        for (int i = 0; i < descricaoDaVez.length; i++) {
            if (i == 1 && !descricaoDaVez[i].equals("Titular: sem titular")) {
                for (int j = 0; j < nJogadores; j++) {
                    if (("Titular: " + nomes[j]).equals(descricaoDaVez[i])) {
                        g.setColor(coresJogadores[j]);
                        break;
                    }
                }
            }
            g.drawString(descricaoDaVez[i], 740, 350 + i * 20);
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

    private int getCasaX(int pos, int offset) {
        int[] X = {
            // INFERIOR (0–9): direita -> esquerda
            560, 500, 440, 380, 320, 260, 200, 140, 80, 20,
            // ESQUERDA (10–19): baixo -> cima
            20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
            // SUPERIOR (20–29): esquerda -> direita
            80, 140, 200, 260, 320, 380, 440, 500, 560, 600,
            // DIREITA (30–39): cima -> baixo
            600, 600, 600, 600, 600, 600, 600, 600, 600, 600
        };
        pos = pos % X.length;
        return 40 + X[pos] + (offset % 3) * 15;
    }

    private int getCasaY(int pos, int offset) {
        int[] Y = {
            // INFERIOR (0–9)
            620, 620, 620, 620, 620, 620, 620, 620, 620, 620,
            // ESQUERDA (10–19)
            560, 500, 440, 380, 320, 260, 200, 140, 80, 20,
            // SUPERIOR (20–29)
            20, 20, 20, 20, 20, 20, 20, 20, 20, 20,
            // DIREITA (30–39)
            80, 140, 200, 260, 320, 380, 440, 500, 560, 620
        };
        pos = pos % Y.length;
        return 60 + Y[pos] + (offset / 3) * 8;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        render(g);
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}
}