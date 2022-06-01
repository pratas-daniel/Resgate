package obstaculo;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import cenario.Cenario;
import cenario.Civil;
import cenario.Soldado;
import prof.jogos2D.image.ComponenteAnimado;
import prof.jogos2D.image.ComponenteMultiAnimado;
import prof.jogos2D.util.Vector2D;

/**
 * Classe que representa um obst�culo e define o comportamento b�sico de todos os obst�culo.
 */
public class Obstaculo {
	
	/** constantes para o tipo de obst�culo */
	public static final int RESGATE = 0;
	public static final int PAREDE = 1;
	public static final int MINA = 2;
	public static final int BARRICADA = 3;
	public static final int PORTA = 4;
	public static final int ALTERNAVEL = 5;
	public static final int EMPURRAVEL = 6;
	public static final int SOLDADO = 7;
	
	// TODO Esta vari�vel � m� programa��o e deve desaparecer!
	/** vari�vel que indica o tipo de obst�culo */
	private int tipo;
	
	/** a figura deve ter pelo menos, uma anima��o: a de estar parado,
	 * consoante o tipo de obst�culo pode ter mais que uma */
	private ComponenteMultiAnimado visual;
	
	/** para os obst�culos que podem terminar o n�vel, precisam de uma imagem final */
	private ComponenteAnimado imagemFinal;

	/** a coordenada onde est� no cen�rio */
	private Point posicao; 	
	
	/** constantes para o estado do obst�culo */
	protected static final int PARADO = 0;
	
	/** constantes para os estados da zona de resgate */
	private static final int RECEBER = 10;

	/** constantes para os estados dos altern�veis */
	private static final int ATIVO = 100;

	
	// estado inicial
	private int status = PARADO;
	
	// o cen�rio onde est� colocado
	private Cenario cenario;
	
	// vari�veis exclusivas da zona de resgate	
	private boolean resgateOcupado = false;  // indica��o se est� completo ou n�o
	private int numAceites;           // quantos civis devem ser aceites
	
	// vari�veis exclusivas dos altern�veis
	private int tempo, tempoOn, tempoOff;
	private boolean alternaOcupado;

	// vari�veis exclusivas dos soldados inimigos
	private ArrayList<Point> caminho = new ArrayList<Point>(); // caminho por onde anda
	private int velocidade = 5;   // velocidade de movimento
	private int posCaminho = 0;   // qual a posi��o do caminho em que est�
	private int dirCaminho = 1;   // a dire��o a percorrer o caminho
	private Vector2D direcao;     // a dire��o de movimento
	private Point destinoCentro, destino; // posi��o em pixeis e posi��o em quadriculas do destino
	private Point2D.Double posCentro;     // posi��o atual em pixeis

	// TODO este tipo nos construtores tem de desaparecer em todos
	/** construtor do obst�culo
	 * @param tipo o tipo de obst�culo
	 * @param visual o aspeto do obst�culo. Deve ter, pelo menos, uma anima��o: a de estar parado 
	 */
	public Obstaculo( int tipo, ComponenteMultiAnimado visual ){
		this( tipo, visual, null );
	}
	
	/** construtor do obst�culo
	 * @param tipo o tipo de obst�culo
	 * @param visual o aspeto do obst�culo. Deve ter, pelo menos, uma anima��o: a de estar parado
	 * @param imgFim imagem do final de n�vel, para os obst�culos que podem terminar um n�vel
	 */
	public Obstaculo( int tipo, ComponenteMultiAnimado visual, ComponenteAnimado imgFim ){	
		this.tipo = tipo;
		this.visual = visual;
		imagemFinal = imgFim;
		if( tipo == SOLDADO )
			visual.setAnim( 1 ); // o soldado nunca est� parado!
	}
	
	/** construtor do obst�culo, para quando � um do tipo resgate
	 * @param tipo tipo (deve ser RESGATE)
	 * @param numCiv n�mero de civis que devem ser resgatados
	 * @param visual o aspeto do obst�culo. Deve ter, pelo menos,
	 * duas anima��es:
	 * 0 = a de estar parado
	 * 1 = a de receber civis
	 * @param imgFim imagem final, quando a zona de resgate estiver completa
	 */
	public Obstaculo( int tipo, int numCiv, ComponenteMultiAnimado visual, ComponenteAnimado imgFim ){		
		this( tipo, visual, imgFim);
		numAceites = numCiv;
	}
	
	/** construtor do obst�culo, para quando � um do tipo altern�vel
	 * @param tipo tipo (deve ser ALTERNAVEL)
	 * @param on se est� ativo 
	 * @param tempoOn durante quantos ciclos fica ativo
	 * @param tempoOff durante quantos ciclos fica inativo
	 * @param visual visual o aspeto do obst�culo. Deve ter, pelo menos,
	 * duas anima��es:
	 * 0 = a de estar inativo
	 * 1 = a de estar ativo
	 * @param imgFim imagem final, se uma pessoa estiver no obst�culo quando est� ativo, perde o n�vel e passa a anima��o final
	 */
	public Obstaculo( int tipo, boolean on, int tempoOn, int tempoOff, ComponenteMultiAnimado visual, ComponenteAnimado imgFim ){		
		this( tipo, visual, imgFim);
		setStatusAtivo( on? ATIVO: PARADO );
		this.tempoOn = tempoOn;
		this.tempoOff = tempoOff;
		tempo = on? tempoOn: tempoOff;
		this.imagemFinal = imgFim;
	}
	
	/** Nos Altern�veis define o estado ativo ou desativo e
	 *  ajusta a anima��o de acordo
	 * @param status o novo status
	 */
	protected void setStatusAtivo(int status) {
		this.status = status;
		if( status == ATIVO ) {
			visual.setAnim( 1 );
			visual.reset();
			tempo = tempoOn;				
		}
		else {
			visual.setAnim( 0 );
			visual.reset();
			tempo = tempoOff;
		}		
	}

	/** processa um ciclo de atualiza��o
	 */
	public void atualizar() {
		// TODO um switch tipo? tem de desaparecer!
		switch( tipo ) {
		case RESGATE: atualizaResgate(); break;
		case ALTERNAVEL:
			tempo--;
			if( tempo <= 0 ) {
				setStatusAtivo( status == ATIVO? PARADO: ATIVO );						
			}
			if( status == ATIVO && alternaOcupado ) {
				imagemFinal.setPosicaoCentro( visual.getPosicaoCentro() );
				cenario.iniciaFimNivel( false, imagemFinal );
			}
			break;
		case SOLDADO:
			atualizarSoldado();
		}
	}
	
	/** processa um ciclo de atualiza��o de uma zona de resgate
	 */
	private void atualizaResgate() {
		if( status == RECEBER) {
			if( visual.numCiclosFeitos() < 1 )
				return;

			if( !resgateOcupado ) {
				status = PARADO;
				visual.setAnim( 0 );
				visual.reset();
			}
			else {
				imagemFinal.setPosicaoCentro( visual.getPosicaoCentro() );
				cenario.removerObstaculo( posicao );
				cenario.iniciaFimNivel( true, imagemFinal );
			}
		}
		
		// se tiver algum civil, tem de o remover
		cenario.removerCivil( posicao );
		
		// ver se j� os recebeu a todos
		if( numAceites == 0 )
			return;
		
		// ver se ainda h� civis para "puxar"
		Point []ondeVer = { new Point(posicao.x+1, posicao.y), new Point(posicao.x-1, posicao.y),
	                        new Point(posicao.x, posicao.y+1), new Point(posicao.x, posicao.y-1) };

		for( Point p : ondeVer ) {
			Civil c = cenario.getCivil( p );			
			if( c != null  ) {
				c.deslocar(posicao.x-p.x, posicao.y - p.y);
				break;
			}
		}
	}
	
	/** processa um ciclo de atualiza��o de uma zona de resgate
	 */
	private void atualizarSoldado() {
		moverSoldado();
		if( cenario.temPessoas( posicao, direcao ) ) {
			Point posTiro = (Point)posicao.clone();
			posTiro.translate( (int)direcao.x,  (int)direcao.y);
			imagemFinal.setAngulo( direcao.getAngulo() );
			imagemFinal.setPosicaoCentro( cenario.getEcran( posTiro ) );
			cenario.iniciaFimNivel( false, imagemFinal );
		}
	}
	
	/**
	 * indica se o soldado pode ocupar este obst�culo
	 * @param s o soldado
	 * @return true, se o soldado pode ocupar o obst�culo
	 */
	public boolean ePassavel( Soldado s ){
		// TODO agora s�o v�rios tipos? Desaparecem todos
		if( tipo == RESGATE || tipo == ALTERNAVEL ) return true;
		return false;
	}

	/**
	 * indica se se pode ver atrav�s do obst�culo
	 * @return true, se se puder ver atrav�s do obst�culo
	 */
	public boolean eTransparente() {
		// TODO mais tipos? 
		if( tipo == PAREDE || tipo == EMPURRAVEL ) return false;
		//os altern�veis n�o s�o transparentes se estiverem ativos
		if( tipo == ALTERNAVEL )
			return status != ATIVO;
		return true;
	}
	
	/** ativa o obst�culo */	
	public void ativar() {
		// TODO mais um tipo que vai desaparecer
		if( tipo == EMPURRAVEL ) {
			Vector2D dir = cenario.getSoldado().getDirecao();
			int xd = (int)(posicao.x + dir.x);
			int yd = (int)(posicao.y + dir.y);
			
			Point dest = new Point(xd, yd);
			if( !cenario.estaOcupado( dest ) ) {
				cenario.moverObstaculo( this, dest);
			}
		}
	}
	
	/**
	 * indica se o civil pode ocupar o obst�culo
	 * @param c o civil a verificar
	 * @return se o civil pode ocupar o obst�culo
	 */
	public boolean podeOcupar( Civil c ){
		// TODO e outro tipo a desaparecer
		if( tipo == PAREDE ) return false;
		
		return c != null && cenario.getCivil( posicao ) == null;
	}
	
	/**
	 * Coloca o soldado no obst�culo <br>
	 * @param s o soldado
	 */
	public void entrar( Soldado op ){
		// TODO n�o h� alternativa poss�vel! Este tipo tem de desaparecer 
		if( tipo == ALTERNAVEL )
			alternaOcupado = true;		
	}

	/**
	 * Coloca o civil no obst�culo. <br>
	 * @param c o civil a colocar
	 */
	public void entrar( Civil c ){
		// TODO mais um switch tipo (j� se referiu que estes switchs t�m de desaparecer? � por t�m mesmo)
		switch( tipo ) {
		case RESGATE:
			if( numAceites > 0 )
				numAceites--;
			if( numAceites == 0 ) 
				resgateOcupado = true;
			status = RECEBER;
			visual.setAnim( 1 );
			visual.reset();
			break;
		case ALTERNAVEL:
			alternaOcupado = true;
			break;
		}
	}

	/**
	 * Remover o soldado do obst�culo. <br>
	 * @param op o soldado a remover
	 */
	public void sair( Soldado op ){
		// TODO eliminar o tipo
		if( tipo == ALTERNAVEL )
			alternaOcupado = false;		
	}
	
	/**
	 * Remove o civil do obst�culo.
	 * @param c o civil a remover
	 */
	public void sair( Civil c ){
		// TODO eliminar o tipo
		if( tipo == ALTERNAVEL )
			alternaOcupado = false;		
	}
	
	/**
	 * devolve o cen�rio associado ao obst�culo
	 * @return o cen�rio associado ao obst�culo
	 */
	public Cenario getCenario() {
		return cenario;
	}
	
	/**
     * define qual o cen�rio associado a este obst�culo.
     * S� deve ser chamado pelo pr�prio cen�rio
     * @param arm o cen�rio
     */
	public void setCenario( Cenario arm ){
		cenario = arm;
	}
	
	/** retorna a imagem do obst�culo
	 * @return a imagem do obst�culo
	 */
	public ComponenteMultiAnimado getVisual() {
		return visual;
	}

	/** define a imagem do obst�culo
	 * @param cv a nova imagem do obst�culo
	 */
	public void setVisual(ComponenteMultiAnimado cv) {
		visual = cv;
    	visual.setPosicaoCentro( cenario.getEcran( posicao ) );
	}
	
	/** desenha o obst�culo
	 * @param g onde desenhar
	 */
	public void desenhar(Graphics2D g) {
		visual.desenhar( g );		
	}

    /**
     * devolve a posi��o do obst�culo
     * @return a posi��o do obst�culo
     */
	public Point getPosicao() {
		return posicao;
	}

	/**
	 * define a posi��o do obst�culo. Deve ser chamada apenas pelo cen�rio
	 * @param pos a nova posi��o 
	 */
   public void setPosicao( Point pos ){
    	posicao = pos;
    	Point pecran = cenario.getEcran(pos);
    	visual.setPosicaoCentro( pecran );
    	
    	// TODO mais um que tem ordem para desaparecer
    	// se o tipo � um soldado, tem de atualizar a dire��o e as posi��es 
    	if( tipo == SOLDADO ) {
    		direcao = new Vector2D(0,0);
        	destinoCentro = pecran;
        	posCentro = new Point2D.Double( pecran.x, pecran.y );
    	}
    }
	
	// m�todos para o soldado inimigo
	/** adiciona um ponto ao camnho que o soldado percorre 
	 * @param p o ponto a adiocionar ao caminho
	 */
	public void addPontoCaminho( Point p ) {
		caminho.add( p );
	}

	/** remove um ponto do caminho que o soldado percorre
	 * @param p o ponto a remover
	 */
	public void removePontoCaminho( Point p ) {
		caminho.remove( p );
	}
	
	/** remove um ponto do caminho que o soldado percorre
	 * @param idx o �ndice do ponto a remover
	 */
	public void removePontoCaminho( int idx ) {
		caminho.remove( idx );
	}
	
	/** devolve a posi��o do inicio do caminho
	 * @return a posi��o do inicio do caminho
	 */
	public Point getPosicaoInicio() {
		return caminho.get( 0 );  
	}

	/** calcula qual o �ndice da pr�xima posi��o no caminho
	 * @return o �ndice do pr�ximo ponto do caminho
	 */
	private int proximoIndice() {
		int nextIdx = posCaminho + dirCaminho;
		// ver se chegou a um dos extremos e volta ao in�cio
		if( dirCaminho > 0 && nextIdx >= caminho.size() )
			nextIdx = 0;
		else if( dirCaminho < 0 && nextIdx < 0 )
			nextIdx = caminho.size()-1;
		return nextIdx;
	}

	/** move o soldado */
	private void moverSoldado() {
		posCentro.x += velocidade * direcao.x;
		posCentro.y += velocidade * direcao.y;
		visual.setPosicaoCentro( new Point( (int)posCentro.x, (int) posCentro.y ) );

		if( posCentro.distanceSq( destinoCentro.x, destinoCentro.y ) < 4 ) {
			cenario.moverObstaculo( this, destino );
						
			int nextIdx = proximoIndice();
			Point dest = caminho.get( nextIdx );
			// se tem obst�culo no local para onde quer ir
			// volta para tr�s e mant�m-se na mesma posi��o
			if( cenario.getObstaculo( dest ) != null ) {
				dirCaminho = -dirCaminho;
				dest = posicao;
			}
			else {
				posCaminho = nextIdx;
			}
			destino = dest;
			destinoCentro = cenario.getEcran( dest );
			direcao = new Vector2D( dest.x - posicao.x, dest.y - posicao.y );
			direcao.normalizar();
			visual.setAngulo( direcao.getAngulo() );
			visual.setPosicaoCentro( new Point( (int)posCentro.x, (int) posCentro.y ) );
		}
	}
}
