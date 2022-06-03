package app;


import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import cenario.Cenario;
import cenario.Soldado;
import prof.jogos2D.util.SKeyboard;

/**
 * Jogo Resgate
 */
public class Resgate extends JFrame implements ActionListener {

	/** versão */
	private static final long serialVersionUID = 1L;

	// as dimensões do armazém são de 40 por 40
    private static int DIMCASA = 40;

    // o cenário e soldado a serem usados no jogo
	private Cenario cenario;
	private Soldado soldado;
	
	// leitor de teclado
	private SKeyboard keyboard = new SKeyboard( );

	private Timer temporizador;
	
	// qual o nível em que se está a jogar
	private int nivel = 1;
	
	// a zona de jogo onde se vão desenhar os elementos
	private PainelDesenho zonaJogo;
	
	// fonte e cores a usar no texto de informação do jogo
	private Font fontInfo;
	private Color corInfo = new Color(200,67,4);
	private Color corInfoSombra = new Color(248,209,108);
	
	// teclas para controlar o soldado
	private static int SUBIR  = KeyEvent.VK_UP;
	private static int DESCER = KeyEvent.VK_DOWN;
	private static int ESQUERDA = KeyEvent.VK_LEFT;
	private static int DIREITA = KeyEvent.VK_RIGHT;
	private static int ATIVAR = KeyEvent.VK_SPACE;
	private static int PUXAR  = KeyEvent.VK_SHIFT;
	private static int REFUGIADO = KeyEvent.VK_Z;
	
	/**
	 * Construtor por defeito
	 */
	public Resgate() {
		super( "RESGATE" );
		setupMenus();		
		setupZonaJogo();	
		setupFont();

		pack();
		zonaJogo.requestFocusInWindow();
		iniciarJogo();
	}

	/**
	 * método que inicia o jogo
	 */
	public void iniciarJogo(){
	    lerNivel( nivel );
		// iniciar a execução do método que vai mandar redesenhar
		temporizador = new Timer( 33, new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				atualizar();					
			}			
		});
		temporizador.start();	

	}
	
	/**
	 * método que lê o ficheiro associado a um dado nível
	 * @param nivel nível a carregar
	 */
	private void lerNivel( int nivel ){
		String ficheiro = "niveis/nivel"+nivel+".txt";
		cenario = new Cenario( new Point(0,0), 40);
		try {
			LeitorFicheirosNivel.lerFicheiro( ficheiro, cenario );
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog( null, "Erro na leitura do ficheiro " + ficheiro, "ERRO", JOptionPane.ERROR_MESSAGE );
			System.exit( 1 );			
		}
		soldado = cenario.getSoldado();
	}
	
	/**
	 * método que processa os eventos do jogo e actualiza o nível
	 */
	private void atualizar(){

		soldado.setPuxar( keyboard.estaPremida( PUXAR ) );
		
		if( keyboard.estaPremida( SUBIR ) ){
			soldado.deslocar( 0, -1 );
		}
		else if( keyboard.estaPremida( DESCER ) ){
			soldado.deslocar( 0, 1 );
		}
		else if( keyboard.estaPremida( ESQUERDA ) ){
			soldado.deslocar( -1, 0 );
		}
		else if( keyboard.estaPremida( DIREITA ) ){
			 soldado.deslocar( 1, 0 );
		}
		if( keyboard.estaPremida( ATIVAR ) ){
			soldado.ativar();
		}
		if( keyboard.estaPremida( REFUGIADO ) ){
			cenario.proximoCivil( );
		}

		cenario.atualizar();
		
		// mandar desenhar todos elementos do jogo
		zonaJogo.repaint();
	}
	
	/** método que desenha todos os elementos do jogo
	 * @param g onde desenhar
	 */
	private void desenharJogo(Graphics g) {
		cenario.desenhar( (Graphics2D) g );
		int left = getBounds().width;
		g.setColor( corInfo );
		g.setFont( fontInfo );
		g.drawString("Nivel", left-108, 42);   // 652, 42);
		g.drawString(""+nivel, left - 88, 82); // 672, 82);
		
		g.setColor( corInfoSombra );
		g.drawString("Nivel", left-108, 40);   // 652, 42);
		g.drawString(""+nivel, left - 88, 80); // 672, 82);
		
		if( cenario.ganhou() )
			ganhouJogo();
		
		if( cenario.perdeu() )
			perdeuJogo();
	}
	
	/** Método chamado quando se ganha um jogo
	 */
	private void ganhouJogo() {
		temporizador.stop();
		JOptionPane.showMessageDialog(null, "Parabéns, terminou o nível " + nivel );
		if( nivel == -1 ) {
			dispose();
			return;
		}
		nivel++;
		iniciarJogo();		
	}

	/** Método chamado quando se perde um jogo
	 */
	private void perdeuJogo() {
		temporizador.stop();
		JOptionPane.showMessageDialog(null, "PERDEU!!!" );
		iniciarJogo();
	}
	
	/** zona de jogo
	 */
	private class PainelDesenho extends JPanel {
		private static final long serialVersionUID = 1L;

		public PainelDesenho() {
			Dimension size = new Dimension(DIMCASA*25+120,DIMCASA*16);
			setSize( size );
			setPreferredSize( size );
			setMinimumSize( size );
		}
		
		public void paintComponent( Graphics g ){			
			super.paintComponent( g );
			desenharJogo(g);
		}
	}
		
	/** resposta aos menus
	 */
	public void actionPerformed( ActionEvent e){
		String cmd = e.getActionCommand();
		if( cmd.equals( "sair" ) ){
			System.exit( 0 );
		}
		else if( cmd.equals("nivel" )){
			iniciarJogo();
		}
		else if( cmd.equals("jogo" )){
			nivel = 1;
			iniciarJogo();
		}
	}	
	
	/** faz o setup da zona de jogo
	 */
	private void setupZonaJogo() {
		zonaJogo = new PainelDesenho( );
		zonaJogo.setBackground( Color.BLUE );		
		getContentPane().add( zonaJogo, BorderLayout.CENTER );
	}	

	private void setupFont() {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont( Font.createFont(Font.TRUETYPE_FONT, new File("art/font.ttf") ) ); 
			fontInfo = new Font( "Kelt Caps Freehand", Font.BOLD, 30);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Eror na leitura da fonte", "Fonte desconhecida", JOptionPane.ERROR_MESSAGE );
			System.exit( 1 );
		}
	}

	/**
	 * faz o setup dos menus
	 */
	private void setupMenus() {
		JMenuBar barra =  new JMenuBar( );
		
		// menu jogo
		JMenu jogoMenu = new JMenu( "Jogo" );
		JMenuItem reinicarMenu = new JMenuItem( "Reiniciar Nível" );
		reinicarMenu.setActionCommand( "nivel" );
		reinicarMenu.addActionListener( this );
		jogoMenu.add( reinicarMenu );

		JMenuItem novoMenu = new JMenuItem( "Reiniciar Jogo" );
		novoMenu.setActionCommand( "jogo" );
		novoMenu.addActionListener( this );
		jogoMenu.add( novoMenu );
		
		JMenuItem sairMenu = new JMenuItem( "Sair" );
		sairMenu.setActionCommand( "sair" );
		sairMenu.addActionListener( this );
		jogoMenu.add( sairMenu );
		barra.add( jogoMenu );
		setJMenuBar( barra );
	}
	
	/** arrancar com este jogo
	 */
	public static void main(String[] args) {		
		Resgate est = new Resgate( );
		est.setVisible( true );
		est.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	}
}
