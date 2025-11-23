package view;

import controller.GameController;
import controller.observer.ObservadorApi;
import model.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.lang.ModuleLayer.Controller;
import java.util.List;

public class TabuleiroView extends JPanel implements Runnable, KeyListener, ObservadorApi {

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

    private final int WIDTH = 1280;
    private final int HEIGHT = 750;
    private final int DIVIDER = 720;

    private final String[] notificacoes = new String[10];

    private JButton botaoLancar;
    private JButton botaoSalvar;
    private JButton botaoEncerrar;
    private JComboBox<String> comboPropriedades;
    private JTextArea areaNotificacoes;
    private JScrollPane scrollNotificacoes;

    
    // Dados
    private JComboBox<Integer> comboDado1;
    private JComboBox<Integer> comboDado2;

    private String[] descricaoDaVez = {""};
    private Image imagemPropriedadeSelecionada = null;
    private Color corDadosAtual;


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

        // Inicializar cor dos dados com a cor do primeiro jogador
        corDadosAtual = coresJogadores[0];
        
        criarComponentesSwing();
        preencherComboPropriedades();
        registrarObservadores();
        frame.setVisible(true);

        gameLoop = new Thread(this);
        gameLoop.start();
    }
    
    /** Registra esta View como observadora dos eventos do Controller */
    private void registrarObservadores() {
        controller.registraObservador("novoValorDados", this);
        controller.registraObservador("novaPosJogador", this);
        controller.registraObservador("novaCarta", this);
        controller.registraObservador("passouVez", this);
        controller.registraObservador("estadoAtualizado", this);
        controller.registraObservador("oferecerCartaSaidaLivre", this);
        controller.registraObservador("usouSaidaLivre", this);
        controller.registraObservador("saiuPrisaoDupla", this);
        controller.registraObservador("saiuPrisao3Tentativas", this);
        controller.registraObservador("tentativaPrisaoFalhou", this);
        controller.registraObservador("dupla", this);
        controller.registraObservador("prisao", this);
        controller.registraObservador("passouInicio", this);
        controller.registraObservador("cairamPropriedade", this);
        controller.registraObservador("cairamCompanhia", this);
        controller.registraObservador("pagouImposto", this);
        controller.registraObservador("recebeuDividendos", this);
        controller.registraObservador("cartaPrisao", this);
        controller.registraObservador("cartaInicio", this);
        controller.registraObservador("cartaSaidaLivre", this);
        controller.registraObservador("cartaMonetaria", this);
        controller.registraObservador("fimDoJogo", this);
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
    	    
    	    // Listener para atualizar visual do dado 1 quando selecionado
    	    comboDado1.addActionListener(e -> {
    	        int valorSelecionado = (Integer) comboDado1.getSelectedItem();
    	        controller.setDadosManuais(valorSelecionado, controller.getDado2());
    	        repaint();
    	    });
    	    
    	    // Listener para atualizar visual do dado 2 quando selecionado
    	    comboDado2.addActionListener(e -> {
    	        int valorSelecionado = (Integer) comboDado2.getSelectedItem();
    	        controller.setDadosManuais(controller.getDado1(), valorSelecionado);
    	        repaint();
    	    });

    	    this.add(comboDado1);
    	    this.add(comboDado2);
    	}
    	
    	// Dados
    	botaoLancar = new JButton("Lançar Dados");
        botaoLancar.setBounds(DIVIDER + 320 , 195, 150, 30);
        botaoLancar.addActionListener(e -> {
            // Desabilitar botão salvar quando jogador começa a jogar
            botaoSalvar.setEnabled(false);
            
            // Define dados se modo manual
            if (controller.isModoManual()) {
                int d1 = (Integer) comboDado1.getSelectedItem();
                int d2 = (Integer) comboDado2.getSelectedItem();
                controller.setDadosManuais(d1, d2);
            }
            
            // Controller processa toda a lógica
            controller.processarJogada();
            
            preencherComboPropriedades();
            repaint();
        });

        // Salvar
        botaoSalvar = new JButton("Salvar Jogo");
        botaoSalvar.setBounds(DIVIDER + 230, HEIGHT - 85, 150, 30);
        botaoSalvar.setEnabled(false); // Desabilitado no início - só habilita após primeira jogada
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
        comboPropriedades.setBounds(DIVIDER + 50, 320, 200, 30);
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
        
        // Área de notificações (selecionável e copiável)
        areaNotificacoes = new JTextArea();
        areaNotificacoes.setEditable(false);
        areaNotificacoes.setFont(new Font("SansSerif", Font.PLAIN, 10));
        areaNotificacoes.setBackground(new Color(210, 230, 210));
        areaNotificacoes.setLineWrap(true);
        areaNotificacoes.setWrapStyleWord(true);
        
        scrollNotificacoes = new JScrollPane(areaNotificacoes);
        scrollNotificacoes.setBounds(DIVIDER + 295, 300, 200, 230);
        scrollNotificacoes.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        this.add(botaoLancar);
        this.add(botaoSalvar);
        this.add(botaoEncerrar);
        this.add(comboPropriedades);
        this.add(scrollNotificacoes);
    }
    
    /** Implementação do padrão Observer */
    @Override
    public void atualiza(String evento) {
        Jogador jogadorAtual = controller.getJogadorDaVez();
        
        switch (evento) {
            case "novoValorDados":
                // Dados já foram atualizados no controller
                addNotificacao(jogadorAtual.getNome() + " lançou: " + controller.getSoma());
                break;
                
            case "novaPosJogador":
                Casa casa = controller.getTabuleiro().getCasa(jogadorAtual.getPosicao());
                addNotificacao("Parou em: " + casa.getNome());
                break;
                
            case "novaCarta":
                Carta carta = controller.getUltimaCarta();
                if (carta != null) {
                    exibirCarta(carta);
                    addNotificacao("Carta: " + carta.getDescricao());
                }
                break;
                
            case "passouVez":
                Jogador proximo = controller.getJogadorDaVez();
                addNotificacao("Vez de: " + proximo.getNome());
                // Atualizar cor dos dados para o novo jogador
                int novoJogadorIdx = controller.getJogadores().indexOf(proximo);
                corDadosAtual = coresJogadores[novoJogadorIdx % coresJogadores.length];
                // Reabilitar botão salvar (próximo jogador pode salvar antes de jogar)
                botaoSalvar.setEnabled(true);
                break;
                
            case "oferecerCartaSaidaLivre":
                int resp = JOptionPane.showConfirmDialog(
                    frame,
                    "Você tem uma carta de Saída Livre. Deseja usá-la?",
                    "Prisão",
                    JOptionPane.YES_NO_OPTION
                );
                if (resp == JOptionPane.YES_OPTION) {
                    controller.usarCartaSaidaLivre();
                }
                break;
                
            case "usouSaidaLivre":
                addNotificacao(jogadorAtual.getNome() + " usou carta de Saída Livre!");
                break;
                
            case "saiuPrisaoDupla":
                addNotificacao("Tirou dupla! Saiu da prisão!");
                break;
                
            case "saiuPrisao3Tentativas":
                addNotificacao("3 tentativas! Saiu da prisão e deve mover.");
                break;
                
            case "tentativaPrisaoFalhou":
                addNotificacao("Não tirou dupla. Tentativa " + jogadorAtual.getTentativasPrisao() + "/3");
                break;
                
            case "dupla":
                addNotificacao("Dupla! Joga novamente.");
                break;
                
            case "prisao":
                addNotificacao(jogadorAtual.getNome() + " foi para a prisão!");
                break;
                
            case "passouInicio":
                addNotificacao(jogadorAtual.getNome() + " passou pelo início! +$200");
                break;
                
            case "cairamPropriedade":
                processarCasaPropriedade(jogadorAtual, (CasaPropriedade) controller.getTabuleiro().getCasa(jogadorAtual.getPosicao()));
                break;
                
            case "cairamCompanhia":
                processarCasaCompanhia(jogadorAtual, (CasaCompanhia) controller.getTabuleiro().getCasa(jogadorAtual.getPosicao()));
                break;
                
            case "pagouImposto":
                addNotificacao("Pagou imposto: $200");
                break;
                
            case "recebeuDividendos":
                addNotificacao("Recebeu: $200");
                break;
                
            case "cartaPrisao":
                addNotificacao(jogadorAtual.getNome() + " foi para a prisão!");
                break;
                
            case "cartaInicio":
                addNotificacao(jogadorAtual.getNome() + " voltou ao início!");
                break;
                
            case "cartaSaidaLivre":
                addNotificacao(jogadorAtual.getNome() + " ganhou carta de Saída Livre!");
                break;
                
            case "cartaMonetaria":
                // Já foi processada, apenas notifica
                break;
                
            case "fimDoJogo":
                encerrarJogo();
                break;
                
            case "estadoAtualizado":
                preencherComboPropriedades();
                break;
                
            default:
                break;
        }
        
        repaint();
    }
    
    /** Exibe a carta de Sorte/Revés */
    private void exibirCarta(Carta carta) {
        Image imgCarta = Images.get(carta.getIdImagem());
        ImageIcon icon = null;
        if (imgCarta != null) {
            icon = new ImageIcon(imgCarta.getScaledInstance(200, 250, Image.SCALE_SMOOTH));
        }
        
        if (icon != null) {
            JOptionPane.showMessageDialog(
                frame,
                carta.getDescricao(),
                "Sorte ou Revés",
                JOptionPane.INFORMATION_MESSAGE,
                icon
            );
        } else {
            JOptionPane.showMessageDialog(
                frame,
                carta.getDescricao(),
                "Sorte ou Revés",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    /** Encerra o jogo e exibe vencedor */
    private void encerrarJogo() {
        int[] patrimonios = controller.calcularPatrimonios();
        List<Jogador> jogadores = controller.getJogadores();
        
        int maxPatrimonio = -1;
        int vencedor = 0;
        
        StringBuilder msg = new StringBuilder("Patrimônios finais:\n\n");
        for (int i = 0; i < jogadores.size(); i++) {
            msg.append(jogadores.get(i).getNome()).append(": $").append(patrimonios[i]).append("\n");
            if (patrimonios[i] > maxPatrimonio) {
                maxPatrimonio = patrimonios[i];
                vencedor = i;
            }
        }
        
        msg.append("\nVencedor: ").append(jogadores.get(vencedor).getNome()).append("!");
        
        JOptionPane.showMessageDialog(
            frame,
            msg.toString(),
            "Fim de Jogo",
            JOptionPane.INFORMATION_MESSAGE
        );
        
        running = false;
        frame.dispose();
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
        // Mapeamento de companhias para suas imagens
        if (nomePropriedade.contains("Companhia")) {
            if (nomePropriedade.contains("Ferroviária") || nomePropriedade.contains("Ferrovi")) {
                return Images.get("companhia1");
            } else if (nomePropriedade.contains("Viação")) {
                return Images.get("companhia2");
            } else if (nomePropriedade.contains("Táxi") && !nomePropriedade.contains("Aéreo")) {
                return Images.get("companhia3");
            } else if (nomePropriedade.contains("Navegação")) {
                return Images.get("companhia4");
            } else if (nomePropriedade.contains("Aviação")) {
                return Images.get("companhia5");
            } else if (nomePropriedade.contains("Aéreo")) {
                return Images.get("companhia6");
            }
        }
        
        // Mapeamento direto de nomes do tabuleiro para nomes dos arquivos PNG
        // Baseado nos arquivos reais em src/Imagens/territorios/
        String mapeamento = nomePropriedade;
        
        // Mapeamentos específicos
        if (nomePropriedade.equals("Av. Nossa Sra. De Copacabana")) {
            mapeamento = "Av. Nossa S. de Copacabana";
        } else if (nomePropriedade.equals("Av. Pacaembú")) {
            mapeamento = "Av. Pacaemb£";
        } else if (nomePropriedade.equals("Av. Atlântica")) {
            mapeamento = "Av. AtlÉntica";
        } else if (nomePropriedade.equals("Av. Rebouças")) {
            mapeamento = "Av. Rebouáas";
        } else if (nomePropriedade.equals("Av. Brigadeiro Faria Lima")) {
            mapeamento = "Av. Brigadero Faria Lima";
        }
        
        Image img = Images.get("territorio_" + mapeamento);
        if (img != null) return img;
        
        // Tenta nome original
        img = Images.get("territorio_" + nomePropriedade);
        if (img != null) return img;
        
        // Tenta sem o prefixo "Av."
        if (nomePropriedade.startsWith("Av.")) {
            String nomeSemAv = nomePropriedade.substring(4).trim();
            img = Images.get("territorio_" + nomeSemAv);
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

        // Desenhar tabuleiro 700x700 - centralizado no quadrante esquerdo
        g.drawImage(tabuleiro, 10, 20, 700, 700, this);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(DIVIDER + 15, 0, 4, HEIGHT);

        List<Jogador> jogadores = controller.getJogadores();

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
            g.setColor(corDadosAtual.equals(corJogador(j)) ? corDadosAtual : Color.BLACK);
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

        g.setColor(corDadosAtual);
        g.fillRect(dadosX, dadosY, 200, 120);
        g.setColor(Color.BLACK);
        g.drawRect(dadosX, dadosY, 200, 120);

        int d1 = controller.getDado1();
        int d2 = controller.getDado2();
        if (dados[d1 - 1] != null) g.drawImage(dados[d1 - 1], dadosX + 15, dadosY + 20, 80, 80, this);
        if (dados[d2 - 1] != null) g.drawImage(dados[d2 - 1], dadosX + 100, dadosY + 20, 80, 80, this);

        // Título Notificações (o JTextArea é um componente Swing separado)
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Notificações:", DIVIDER + 295, 290);

        // Peões no tabuleiro
        for (int i = 0; i < jogadores.size(); i++) {
            Jogador j = jogadores.get(i);
            int pos = j.getPosicao();
            int x = getCasaX(pos, i);
            int y = getCasaY(pos, i);
            g.drawImage(pinos[i], x, y, 15, 22, this);
        }

        // Descrição da propriedade (acima da imagem)
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
            g.drawString(descricaoDaVez[i], DIVIDER + 50, 370 + i * 20);
            g.setColor(Color.BLACK);
        }
        
        // Imagem da propriedade selecionada (abaixo da descrição, alinhada à esquerda)
        if (imagemPropriedadeSelecionada != null) {
            int imgY = 370 + (descricaoDaVez.length * 20) + 10;
            g.drawImage(imagemPropriedadeSelecionada, DIVIDER + 50, imgY, 132, 150, this);
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
        
        // Atualizar JTextArea
        if (areaNotificacoes != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = notificacoes.length - 1; i >= 0; i--) {
                if (notificacoes[i] != null && !notificacoes[i].equals("-")) {
                    sb.append(notificacoes[i]).append("\n");
                }
            }
            areaNotificacoes.setText(sb.toString());
            // Scroll para o final
            areaNotificacoes.setCaretPosition(areaNotificacoes.getDocument().getLength());
        }
    }

    private int getCasaX(int pos, int offset) {
        // Tabuleiro 700x700: casas grandes nos cantos 93x93, casas comuns 56x92
        // Margem: 10px à esquerda, 20px no topo
        int baseX = 10;
        
        int[] X = new int[40];
        
        // Casa 0: canto inferior direito (PARTIDA) - 93x93
        X[0] = baseX + 607;
        
        // Casas 1-9: parte inferior (direita para esquerda) - 56 de largura
        for (int i = 1; i <= 9; i++) {
            X[i] = baseX + 607 - (i * 56);
        }
        
        // Casa 10: canto inferior esquerdo (PRISÃO) - 93x93
        X[10] = baseX + 0;
        
        // Casas 11-19: lateral esquerda (baixo para cima) - todas x=0
        for (int i = 11; i <= 19; i++) {
            X[i] = baseX + 0;
        }
        
        // Casa 20: canto superior esquerdo (PARADA LIVRE) - 93x93
        X[20] = baseX + 0;
        
        // Casas 21-29: parte superior (esquerda para direita) - 56 de largura
        for (int i = 21; i <= 29; i++) {
            X[i] = baseX + 93 + ((i - 21) * 56);
        }
        
        // Casa 30: canto superior direito (VÁ PARA PRISÃO) - 93x93
        X[30] = baseX + 607;
        
        // Casas 31-39: lateral direita (cima para baixo) - todas x=607
        for (int i = 31; i <= 39; i++) {
            X[i] = baseX + 607;
        }
        
        pos = pos % 40;
        return X[pos] + (offset % 3) * 10;
    }

    private int getCasaY(int pos, int offset) {
        // Margem: 20px no topo
        int baseY = 20;
        
        int[] Y = new int[40];
        
        // Casa 0: canto inferior direito (PARTIDA) - 93x93
        Y[0] = baseY + 607;
        
        // Casas 1-9: parte inferior (todas na mesma linha y=607)
        for (int i = 1; i <= 9; i++) {
            Y[i] = baseY + 607;
        }
        
        // Casa 10: canto inferior esquerdo (PRISÃO) - 93x93
        Y[10] = baseY + 607;
        
        // Casas 11-19: lateral esquerda (baixo para cima) - 56 de altura
        for (int i = 11; i <= 19; i++) {
            Y[i] = baseY + 607 - ((i - 10) * 56);
        }
        
        // Casa 20: canto superior esquerdo (PARADA LIVRE) - 93x93
        Y[20] = baseY + 0;
        
        // Casas 21-29: parte superior (todas na mesma linha y=0)
        for (int i = 21; i <= 29; i++) {
            Y[i] = baseY + 0;
        }
        
        // Casa 30: canto superior direito (VÁ PARA PRISÃO) - 93x93
        Y[30] = baseY + 0;
        
        // Casas 31-39: lateral direita (cima para baixo) - 56 de altura
        for (int i = 31; i <= 39; i++) {
            Y[i] = baseY + 93 + ((i - 31) * 56);
        }
        
        pos = pos % 40;
        return Y[pos] + (offset / 3) * 8;
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