package controller;

import model.Tabuleiro;
import model.Jogador;
import model.Baralho;
import model.Carta;
import model.TipoCartas;
import view.JanelaInicial;
import view.JanelaTabuleiro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Controlador principal do jogo.
 *
 * Requisitos que ele cobre na 2ª iteração:
 *  - Singleton (uma única instância)
 *  - cria jogadores e sorteia a ordem
 *  - mantém o valor atual dos dados
 *  - mantém o baralho de Sorte/Revés e registra a última carta comprada
 *  - expõe tudo isso para a View desenhar (TabuleiroPanel)
 *  - abre JanelaInicial e JanelaTabuleiro
 */
public class GameController {

    /* ---------- Singleton ---------- */
    private static GameController instancia;
    public static GameController getInstancia() {
        if (instancia == null) instancia = new GameController();
        return instancia;
    }

    /* ---------- Estado do jogo ---------- */
    private final Random rng = new Random();
    private final List<Jogador> jogadores = new ArrayList<>();
    private int idxDaVez = 0;

    private Tabuleiro tabuleiro;
    private Baralho baralho;

    // valores exibidos graficamente na HUD de dados
    private int dado1 = 1;
    private int dado2 = 1;

    /* ---------- Construtor privado ---------- */
    private GameController() {
        this.tabuleiro = new Tabuleiro();
    }

    /* ---------- Fluxo de telas ---------- */
    public void start() {
        new JanelaInicial(this).setVisible(true);
    }

    public void iniciarNovaPartida(int qtdJogadores) {
        criarJogadores(qtdJogadores);
        sortearOrdem();
        inicializarBaralhoChance();
        new JanelaTabuleiro(this).setVisible(true);
    }

    /* ---------- Jogadores ---------- */
    private void criarJogadores(int qtd) {
        jogadores.clear();
        for (int i = 1; i <= qtd; i++) {
            // ajuste se seu Jogador tiver construtor diferente
            jogadores.add(new Jogador("J" + i));
        }
        idxDaVez = 0;
    }

    private void sortearOrdem() {
        Collections.shuffle(jogadores, rng);
        idxDaVez = 0;
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public Jogador getJogadorDaVez() {
        if (jogadores.isEmpty()) return null;
        return jogadores.get(idxDaVez);
    }

    public int getIndicePinoJogadorDaVez() {
        if (jogadores.isEmpty()) return 0;
        // por enquanto mapeamos jogador da vez -> pinoX (0..5)
        return idxDaVez % 6;
    }

    public void passarVez() {
        if (jogadores.isEmpty()) return;
        idxDaVez = (idxDaVez + 1) % jogadores.size();
    }

    /* ---------- Tabuleiro ---------- */
    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    /* ---------- Baralho / Cartas ---------- */
    public Carta getUltimaCartaComprada() {
        return (baralho != null) ? baralho.getUltimaCarta() : null;
    }

    /**
     * Monta todas as 30 cartas do professor (SORTE e REVÉS),
     * já conectadas ao nome do arquivo chanceN.png.
     */
    private void inicializarBaralhoChance() {
        List<Carta> cartas = new ArrayList<>();

        // === SORTE ===
        cartas.add(new Carta("chance1",  "A prefeitura mandou abrir uma nova avenida, desapropriando prédios. Seu terreno valorizou. Receba 25.", TipoCartas.SORTE));
        cartas.add(new Carta("chance2",  "Houve um assalto à sua loja, mas você estava segurado. Receba 150.", TipoCartas.SORTE));
        cartas.add(new Carta("chance3",  "Um amigo que lhe devia dinheiro resolveu pagar a dívida. Receba 80.", TipoCartas.SORTE));
        cartas.add(new Carta("chance4",  "Suas ações na Bolsa de Valores subiram. Receba 200.", TipoCartas.SORTE));
        cartas.add(new Carta("chance5",  "Você trocou seu carro usado com um amigo e ainda saiu lucrando. Receba 50.", TipoCartas.SORTE));
        cartas.add(new Carta("chance6",  "Você recebeu uma parcela do 13º salário. Receba 50.", TipoCartas.SORTE));
        cartas.add(new Carta("chance7",  "Você tirou o primeiro lugar no Torneio de Tênis do clube. Receba 100.", TipoCartas.SORTE));
        cartas.add(new Carta("chance8",  "Seu cachorro policial tirou o 1º prêmio na exposição do Kennel Club. Receba 100.", TipoCartas.SORTE));
        cartas.add(new Carta("chance9",  "Saída livre da prisão. (Guarde este cartão para quando precisar.)", TipoCartas.SORTE));
        cartas.add(new Carta("chance10", "Avance até o ponto de partida e receba 200.", TipoCartas.SORTE));
        cartas.add(new Carta("chance11", "Você apostou com os parceiros deste jogo e ganhou. Receba 50 de cada um.", TipoCartas.SORTE));
        cartas.add(new Carta("chance12", "Você saiu de férias e ficou na casa de um amigo. Economizou hotel. Receba 45.", TipoCartas.SORTE));
        cartas.add(new Carta("chance13", "Você recebeu uma herança que já estava esquecida. Receba 100.", TipoCartas.SORTE));
        cartas.add(new Carta("chance14", "Você foi promovido a diretor da sua empresa. Receba 100.", TipoCartas.SORTE));
        cartas.add(new Carta("chance15", "Você jogou na Loteria Esportiva com um grupo de amigos e ganharam! Receba 20.", TipoCartas.SORTE));

        // === REVÉS ===
        cartas.add(new Carta("chance16", "Um amigo pediu-lhe um empréstimo. Você não pode recusar. Pague 15.", TipoCartas.REVES));
        cartas.add(new Carta("chance17", "Você vai casar e está comprando um apartamento novo. Pague 25.", TipoCartas.REVES));
        cartas.add(new Carta("chance18", "O médico recomendou repouso num bom hotel de montanha. Pague 45.", TipoCartas.REVES));
        cartas.add(new Carta("chance19", "Você quis assistir à estreia da temporada de ballet. Compre os ingressos. Pague 30.", TipoCartas.REVES));
        cartas.add(new Carta("chance20", "Você convidou seus amigos para festejar o aniversário. Pague 100.", TipoCartas.REVES));
        cartas.add(new Carta("chance21", "Você é papai outra vez! Despesas de maternidade. Pague 100.", TipoCartas.REVES));
        cartas.add(new Carta("chance22", "Os livros do ano passado não servem mais. Preciso de livros novos. Pague 40.", TipoCartas.REVES));
        cartas.add(new Carta("chance23", "Vá para a prisão sem receber nada.", TipoCartas.REVES));
        cartas.add(new Carta("chance24", "Você estacionou em lugar proibido e entrou na contramão. Pague 30.", TipoCartas.REVES));
        cartas.add(new Carta("chance25", "Você recebeu a comunicação do Imposto de Renda. Pague 50.", TipoCartas.REVES));
        cartas.add(new Carta("chance26", "Seu clube está ampliando as piscinas. Os sócios devem contribuir. Pague 25.", TipoCartas.REVES));
        cartas.add(new Carta("chance27", "Renove a tempo a licença do seu automóvel. Pague 30.", TipoCartas.REVES));
        cartas.add(new Carta("chance28", "Parentes do interior vieram passar 'férias' na sua casa. Pague 45.", TipoCartas.REVES));
        cartas.add(new Carta("chance29", "Seus filhos já vão para a escola. Pague a primeira mensalidade. Pague 50.", TipoCartas.REVES));
        cartas.add(new Carta("chance30", "A geada prejudicou sua safra de café. Pague 50.", TipoCartas.REVES));

        this.baralho = new Baralho(cartas);
    }

    /* ---------- Dados ---------- */
    public int getDado1() { return dado1; }
    public int getDado2() { return dado2; }

    public void definirDados(int d1, int d2) {
        this.dado1 = clampDado(d1);
        this.dado2 = clampDado(d2);
    }

    public void rolarDados() {
        this.dado1 = rng.nextInt(6) + 1;
        this.dado2 = rng.nextInt(6) + 1;
    }

    private int clampDado(int v) {
        if (v < 1) return 1;
        if (v > 6) return 6;
        return v;
    }

    /**
     * Chamado pelo botão "Aplicar dados" da JanelaTabuleiro.
     * Aqui é o "turno".
     *
     * Nesta iteração:
     *  - atualiza os valores dos dados
     *  - (no futuro) move o pião no Tabuleiro
     *  - compra carta (pra poder desenhar a imagem da carta na HUD)
     */
    public void processarJogadaComValores(int d1, int d2) {
        definirDados(d1, d2);

        // (futuro) mover o jogador da vez d1+d2 casas:
        // tabuleiro.deslocarPiao(getJogadorDaVez(), d1 + d2);

        // (futuro) descobrir casa em que caiu e reagir
        // se a casa for Sorte/Revés, comprar carta:
        if (baralho != null) {
            baralho.comprarCarta(); // Baralho deve atualizar ultimaCarta internamente
        }

        // opcional: já passar a vez para o próximo jogador
        // passarVez();
    }
}
