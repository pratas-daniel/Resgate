package cenario;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

import obstaculo.Obstaculo;
import prof.jogos2D.image.ComponenteMultiAnimado;
import prof.jogos2D.util.Vector2D;

/**
 * Classe que representa o Soldado que opera num cenário 
 */
public class Soldado {
	
	/** constantes para definir o que está a fazer,
	 * que também são as animações a fazer */
	private static final int PARADO = 0;
	private static final int ANDAR = 1;
	private static final int ATIVAR = 2;	
	
	/** estado atual do soldado */
	private int status = PARADO;

	/** A imagem do soldado Deve ter 3 animações:
	 * zero = animação do soldado parado
	 * um   = animação do soldado a andar
	 * dois = animação do saldado a ativar
	 */
	private ComponenteMultiAnimado figura;

	/** posição atual e, caso esteja a mover, a posição de destino */
	private Point posicao, destino; 
	
	/** as velocidades do soldado */
	private static final int VELOC_NORMAL = 6;
	private static final int VELOC_PUXAR = 4;
	
	/** velocidade atual do soldado */
	private float velocidade = VELOC_NORMAL;
	/** direção do movimento */
	private Vector2D direcao = new Vector2D(1,0);
	/** posição do centro da imagem e posição de destino */
	private Point2D.Double posicaoCentro, destinoCentro;
	
	/** o cenário onde ele se opera */
	private Cenario cenario;
	
	/** indica se o soldado está a puxar civis */
	private boolean puxar;
	
	/**
	 * Construtor do Soldado
	 * @param fig a imagem do soldado. Deve ter 3 animações:<br>
	 * 0 = animação do soldado parado
	 * 1 = animação do soldado a andar
	 * 2 = animação do saldado a ativar
	 */
	public Soldado( ComponenteMultiAnimado figura ){
		this.figura = figura;
		setStatus( PARADO );
	}
	
	/** define o estado do soldado
	 * @param stat o novo estado
	 */
	private void setStatus(int stat) {
		status = stat;
		// escolhe a animação adequado ao estado
		figura.setAnim( status );
		figura.setAngulo( direcao.getAngulo() );		
	}
	
	/** faz um ciclo de atualização
	 */
	public void atualizar() {
		if( status == PARADO )
			return;
		
		if( status == ANDAR ) {
			posicaoCentro.x += velocidade * direcao.x;
			posicaoCentro.y += velocidade * direcao.y;
			figura.setPosicaoCentro( new Point( (int)posicaoCentro.x, (int) posicaoCentro.y ) );
	
			if( posicaoCentro.distance( destinoCentro ) < velocidade ) {
				setStatus( PARADO );
			    cenario.colocarSoldado( destino, this );
			}
		}
		else if( status == ATIVAR ) {
			// se já realizou a animação de ativar, então passa para outro estado
			if( figura.numCiclosFeitos() >= 1) {
				figura.reset();
				setStatus( PARADO );
			}
		} 
	}

	/**
	 * indica se o soldado se pode deslocar
	 * @param dx o deslocamento em x
	 * @param dy o deslocamento em y
	 * @return se se pode deslocar, ou não
	 */
	public boolean podeDeslocar(int dx, int dy) {
		Point dest = (Point)getPosicao().clone();
		dest.translate(dx,dy);

		direcao = new Vector2D(dx,dy);
		figura.setAngulo( direcao.getAngulo() );
		
		if( !cenario.eCoordenadaValida(dest) )
			return false;

		// se tem obstáculo no destino, ete tem de ser passável
		Obstaculo o = cenario.getObstaculo( dest );
		if( o != null  && !o.ePassavel() )
			return false;
		
		// não pode ter um civil no destino
		return cenario.getCivil( dest ) == null;
	}

	/**
	 * desloca o soldado
	 * @param dx o deslocamento em x
	 * @param dy o deslocamento em y
	 * @return true, se o soldado se deslocou
	 */
	public boolean deslocar( int dx, int dy ) {
		if( status != PARADO )
			return false;
		
		// ver se pode deslocar, senão não se desloca
	    if( !podeDeslocar( dx, dy ) )
	        return false;

	    // deterinar a direção de movimento e o destino
	    direcao = new Vector2D( dx,  dy );
		destino = (Point)getPosicao().clone();
		destino.translate(dx,dy);
	    Point d = cenario.getEcran( destino );
		destinoCentro = new Point2D.Double( d.x, d.y );
	    setStatus( ANDAR );
			
	    // ver se tem algum civil para puxar
		if( !puxar )
			return true;
		Point []ondeVer = { new Point(posicao.x+1, posicao.y), new Point(posicao.x-1, posicao.y),
				            new Point(posicao.x, posicao.y+1), new Point(posicao.x, posicao.y-1) };

		for( Point p : ondeVer ) {
			if( p.equals(destino ) ) 
				continue;
			Civil c = cenario.getCivil( p );			
			if( c != null  ) {
				c.deslocar(posicao.x-p.x, posicao.y - p.y);
				break;
			}
		}
	    return true;
	}	

	/** ativa qualquer coisa no cenário
	 */
	public void ativar() {
		if( status != PARADO )
			return;

		setStatus( ATIVAR );
		
		// ver se há obstáculo para ativar
		Point dest = new Point( (int)(posicao.x + direcao.x), (int)(posicao.y + direcao.y) );
		Obstaculo o = cenario.getObstaculo( dest );
		if( o != null ) {
			o.ativar();
		}
	}
	
	/**
	 * devolve a posição do soldado
	 * @return a posição do soldado
	 */
	public Point getPosicao() {
		return posicao;
	}

	/**
	 * define a posição do soldado
	 * @param pos a nova posição 
	 */
    public void setPosicao( Point pos ){
    	posicao = pos;
    	// atualizar a posição da imagem
    	Point pecran = getCenario().getEcran(pos);
    	figura.setPosicaoCentro( pecran );
    	posicaoCentro = new Point2D.Double( pecran.x, pecran.y );
    }
	
    /**
     * indica em que cenário o soldado opera
     * @return o cenário em que o soldado opera
     */
	public Cenario getCenario() {
		return cenario;
	}
	
	/**
	 * define o cenário onde o soldado opera.
	 * Só deve ser chamado pelo próprio cenário.
	 * @param c o cenário onde o soldado vai operar
	 */
	public void setCenario( Cenario c ){
		cenario = c;
	}    

	/** desenha o soldado
	 * @param g onde desenhar
	 */
	public void desenhar( Graphics2D g) {
		figura.desenhar( g );
	}

	/** define se o soldado está a puxar civis ou não
	 * @param puxar se é para puxar
	 */
	public void setPuxar(boolean puxar) {
		this.puxar = puxar;		
		velocidade = puxar? VELOC_PUXAR: VELOC_NORMAL;
	}

	/** retorna a direção do movimento
	 * @return a direção do movimento
	 */
	public Vector2D getDirecao() {
		return direcao;		
	}
	
	/** retorna a velocidade atual
	 * @return a velocidade atual
	 */
	public float getVelocidade() {
		return velocidade;
	}
}
