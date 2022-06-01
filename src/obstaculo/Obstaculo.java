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
 * Classe que representa um obstáculo e define o comportamento básico de todos os obstáculo.
 */
public class Obstaculo {
	
	/** constantes para o tipo de obstáculo */
	public static final int RESGATE = 0;
	public static final int PAREDE = 1;
	public static final int MINA = 2;
	public static final int BARRICADA = 3;
	public static final int PORTA = 4;
	public static final int ALTERNAVEL = 5;
	public static final int EMPURRAVEL = 6;
	public static final int SOLDADO = 7;
	
	// TODO Esta variável é má programação e deve desaparecer!
	/** variável que indica o tipo de obstáculo */
	private int tipo;
	
	/** a figura deve ter pelo menos, uma animação: a de estar parado,
	 * consoante o tipo de obstáculo pode ter mais que uma */
	private ComponenteMultiAnimado visual;
	
	/** para os obstáculos que podem terminar o nível, precisam de uma imagem final */
	private ComponenteAnimado imagemFinal;

	/** a coordenada onde está no cenário */
	private Point posicao; 	
	
	/** constantes para o estado do obstáculo */
	protected static final int PARADO = 0;
	
	/** constantes para os estados da zona de resgate */
	private static final int RECEBER = 10;

	/** constantes para os estados dos alternáveis */
	private static final int ATIVO = 100;

	
	// estado inicial
	private int status = PARADO;
	
	// o cenário onde está colocado
	private Cenario cenario;
	
	// variáveis exclusivas da zona de resgate	
	private boolean resgateOcupado = false;  // indicação se está completo ou não
	private int numAceites;           // quantos civis devem ser aceites
	
	// variáveis exclusivas dos alternáveis
	private int tempo, tempoOn, tempoOff;
	private boolean alternaOcupado;

	// variáveis exclusivas dos soldados inimigos
	private ArrayList<Point> caminho = new ArrayList<Point>(); // caminho por onde anda
	private int velocidade = 5;   // velocidade de movimento
	private int posCaminho = 0;   // qual a posição do caminho em que está
	private int dirCaminho = 1;   // a direção a percorrer o caminho
	private Vector2D direcao;     // a direção de movimento
	private Point destinoCentro, destino; // posição em pixeis e posição em quadriculas do destino
	private Point2D.Double posCentro;     // posição atual em pixeis

	// TODO este tipo nos construtores tem de desaparecer em todos
	/** construtor do obstáculo
	 * @param tipo o tipo de obstáculo
	 * @param visual o aspeto do obstáculo. Deve ter, pelo menos, uma animação: a de estar parado 
	 */
	public Obstaculo( int tipo, ComponenteMultiAnimado visual ){
		this( tipo, visual, null );
	}
	
	/** construtor do obstáculo
	 * @param tipo o tipo de obstáculo
	 * @param visual o aspeto do obstáculo. Deve ter, pelo menos, uma animação: a de estar parado
	 * @param imgFim imagem do final de nível, para os obstáculos que podem terminar um nível
	 */
	public Obstaculo( int tipo, ComponenteMultiAnimado visual, ComponenteAnimado imgFim ){	
		this.tipo = tipo;
		this.visual = visual;
		imagemFinal = imgFim;
		if( tipo == SOLDADO )
			visual.setAnim( 1 ); // o soldado nunca está parado!
	}
	
	/** construtor do obstáculo, para quando é um do tipo resgate
	 * @param tipo tipo (deve ser RESGATE)
	 * @param numCiv número de civis que devem ser resgatados
	 * @param visual o aspeto do obstáculo. Deve ter, pelo menos,
	 * duas animações:
	 * 0 = a de estar parado
	 * 1 = a de receber civis
	 * @param imgFim imagem final, quando a zona de resgate estiver completa
	 */
	public Obstaculo( int tipo, int numCiv, ComponenteMultiAnimado visual, ComponenteAnimado imgFim ){		
		this( tipo, visual, imgFim);
		numAceites = numCiv;
	}
	
	/** construtor do obstáculo, para quando é um do tipo alternável
	 * @param tipo tipo (deve ser ALTERNAVEL)
	 * @param on se está ativo 
	 * @param tempoOn durante quantos ciclos fica ativo
	 * @param tempoOff durante quantos ciclos fica inativo
	 * @param visual visual o aspeto do obstáculo. Deve ter, pelo menos,
	 * duas animações:
	 * 0 = a de estar inativo
	 * 1 = a de estar ativo
	 * @param imgFim imagem final, se uma pessoa estiver no obstáculo quando está ativo, perde o nível e passa a animação final
	 */
	public Obstaculo( int tipo, boolean on, int tempoOn, int tempoOff, ComponenteMultiAnimado visual, ComponenteAnimado imgFim ){		
		this( tipo, visual, imgFim);
		setStatusAtivo( on? ATIVO: PARADO );
		this.tempoOn = tempoOn;
		this.tempoOff = tempoOff;
		tempo = on? tempoOn: tempoOff;
		this.imagemFinal = imgFim;
	}
	
	/** Nos Alternáveis define o estado ativo ou desativo e
	 *  ajusta a animação de acordo
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

	/** processa um ciclo de atualização
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
	
	/** processa um ciclo de atualização de uma zona de resgate
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
		
		// ver se já os recebeu a todos
		if( numAceites == 0 )
			return;
		
		// ver se ainda há civis para "puxar"
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
	
	/** processa um ciclo de atualização de uma zona de resgate
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
	 * indica se o soldado pode ocupar este obstáculo
	 * @param s o soldado
	 * @return true, se o soldado pode ocupar o obstáculo
	 */
	public boolean ePassavel( Soldado s ){
		// TODO agora são vários tipos? Desaparecem todos
		if( tipo == RESGATE || tipo == ALTERNAVEL ) return true;
		return false;
	}

	/**
	 * indica se se pode ver através do obstáculo
	 * @return true, se se puder ver através do obstáculo
	 */
	public boolean eTransparente() {
		// TODO mais tipos? 
		if( tipo == PAREDE || tipo == EMPURRAVEL ) return false;
		//os alternáveis não são transparentes se estiverem ativos
		if( tipo == ALTERNAVEL )
			return status != ATIVO;
		return true;
	}
	
	/** ativa o obstáculo */	
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
	 * indica se o civil pode ocupar o obstáculo
	 * @param c o civil a verificar
	 * @return se o civil pode ocupar o obstáculo
	 */
	public boolean podeOcupar( Civil c ){
		// TODO e outro tipo a desaparecer
		if( tipo == PAREDE ) return false;
		
		return c != null && cenario.getCivil( posicao ) == null;
	}
	
	/**
	 * Coloca o soldado no obstáculo <br>
	 * @param s o soldado
	 */
	public void entrar( Soldado op ){
		// TODO não há alternativa possível! Este tipo tem de desaparecer 
		if( tipo == ALTERNAVEL )
			alternaOcupado = true;		
	}

	/**
	 * Coloca o civil no obstáculo. <br>
	 * @param c o civil a colocar
	 */
	public void entrar( Civil c ){
		// TODO mais um switch tipo (já se referiu que estes switchs têm de desaparecer? É por têm mesmo)
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
	 * Remover o soldado do obstáculo. <br>
	 * @param op o soldado a remover
	 */
	public void sair( Soldado op ){
		// TODO eliminar o tipo
		if( tipo == ALTERNAVEL )
			alternaOcupado = false;		
	}
	
	/**
	 * Remove o civil do obstáculo.
	 * @param c o civil a remover
	 */
	public void sair( Civil c ){
		// TODO eliminar o tipo
		if( tipo == ALTERNAVEL )
			alternaOcupado = false;		
	}
	
	/**
	 * devolve o cenário associado ao obstáculo
	 * @return o cenário associado ao obstáculo
	 */
	public Cenario getCenario() {
		return cenario;
	}
	
	/**
     * define qual o cenário associado a este obstáculo.
     * Só deve ser chamado pelo próprio cenário
     * @param arm o cenário
     */
	public void setCenario( Cenario arm ){
		cenario = arm;
	}
	
	/** retorna a imagem do obstáculo
	 * @return a imagem do obstáculo
	 */
	public ComponenteMultiAnimado getVisual() {
		return visual;
	}

	/** define a imagem do obstáculo
	 * @param cv a nova imagem do obstáculo
	 */
	public void setVisual(ComponenteMultiAnimado cv) {
		visual = cv;
    	visual.setPosicaoCentro( cenario.getEcran( posicao ) );
	}
	
	/** desenha o obstáculo
	 * @param g onde desenhar
	 */
	public void desenhar(Graphics2D g) {
		visual.desenhar( g );		
	}

    /**
     * devolve a posição do obstáculo
     * @return a posição do obstáculo
     */
	public Point getPosicao() {
		return posicao;
	}

	/**
	 * define a posição do obstáculo. Deve ser chamada apenas pelo cenário
	 * @param pos a nova posição 
	 */
   public void setPosicao( Point pos ){
    	posicao = pos;
    	Point pecran = cenario.getEcran(pos);
    	visual.setPosicaoCentro( pecran );
    	
    	// TODO mais um que tem ordem para desaparecer
    	// se o tipo é um soldado, tem de atualizar a direção e as posições 
    	if( tipo == SOLDADO ) {
    		direcao = new Vector2D(0,0);
        	destinoCentro = pecran;
        	posCentro = new Point2D.Double( pecran.x, pecran.y );
    	}
    }
	
	// métodos para o soldado inimigo
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
	 * @param idx o índice do ponto a remover
	 */
	public void removePontoCaminho( int idx ) {
		caminho.remove( idx );
	}
	
	/** devolve a posição do inicio do caminho
	 * @return a posição do inicio do caminho
	 */
	public Point getPosicaoInicio() {
		return caminho.get( 0 );  
	}

	/** calcula qual o índice da próxima posição no caminho
	 * @return o índice do próximo ponto do caminho
	 */
	private int proximoIndice() {
		int nextIdx = posCaminho + dirCaminho;
		// ver se chegou a um dos extremos e volta ao início
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
			// se tem obstáculo no local para onde quer ir
			// volta para trás e mantém-se na mesma posição
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
