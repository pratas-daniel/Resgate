package cenario;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

import obstaculo.Obstaculo;
import prof.jogos2D.image.ComponenteMultiAnimado;
import prof.jogos2D.util.Vector2D;

/**
 * Classe que representa o Soldado que opera num cen�rio 
 */
public class Soldado {
	
	/** constantes para definir o que est� a fazer,
	 * que tamb�m s�o as anima��es a fazer */
	private static final int PARADO = 0;
	private static final int ANDAR = 1;
	private static final int ATIVAR = 2;	
	
	/** estado atual do soldado */
	private int status = PARADO;

	/** A imagem do soldado Deve ter 3 anima��es:
	 * zero = anima��o do soldado parado
	 * um   = anima��o do soldado a andar
	 * dois = anima��o do saldado a ativar
	 */
	private ComponenteMultiAnimado figura;

	/** posi��o atual e, caso esteja a mover, a posi��o de destino */
	private Point posicao, destino; 
	
	/** as velocidades do soldado */
	private static final int VELOC_NORMAL = 6;
	private static final int VELOC_PUXAR = 4;
	
	/** velocidade atual do soldado */
	private float velocidade = VELOC_NORMAL;
	/** dire��o do movimento */
	private Vector2D direcao = new Vector2D(1,0);
	/** posi��o do centro da imagem e posi��o de destino */
	private Point2D.Double posicaoCentro, destinoCentro;
	
	/** o cen�rio onde ele se opera */
	private Cenario cenario;
	
	/** indica se o soldado est� a puxar civis */
	private boolean puxar;
	
	/**
	 * Construtor do Soldado
	 * @param fig a imagem do soldado. Deve ter 3 anima��es:<br>
	 * 0 = anima��o do soldado parado
	 * 1 = anima��o do soldado a andar
	 * 2 = anima��o do saldado a ativar
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
		// escolhe a anima��o adequado ao estado
		figura.setAnim( status );
		figura.setAngulo( direcao.getAngulo() );		
	}
	
	/** faz um ciclo de atualiza��o
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
			// se j� realizou a anima��o de ativar, ent�o passa para outro estado
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
	 * @return se se pode deslocar, ou n�o
	 */
	public boolean podeDeslocar(int dx, int dy) {
		Point dest = (Point)getPosicao().clone();
		dest.translate(dx,dy);

		direcao = new Vector2D(dx,dy);
		figura.setAngulo( direcao.getAngulo() );
		
		if( !cenario.eCoordenadaValida(dest) )
			return false;

		// se tem obst�culo no destino, ete tem de ser pass�vel
		Obstaculo o = cenario.getObstaculo( dest );
		if( o != null  && !o.ePassavel() )
			return false;
		
		// n�o pode ter um civil no destino
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
		
		// ver se pode deslocar, sen�o n�o se desloca
	    if( !podeDeslocar( dx, dy ) )
	        return false;

	    // deterinar a dire��o de movimento e o destino
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

	/** ativa qualquer coisa no cen�rio
	 */
	public void ativar() {
		if( status != PARADO )
			return;

		setStatus( ATIVAR );
		
		// ver se h� obst�culo para ativar
		Point dest = new Point( (int)(posicao.x + direcao.x), (int)(posicao.y + direcao.y) );
		Obstaculo o = cenario.getObstaculo( dest );
		if( o != null ) {
			o.ativar();
		}
	}
	
	/**
	 * devolve a posi��o do soldado
	 * @return a posi��o do soldado
	 */
	public Point getPosicao() {
		return posicao;
	}

	/**
	 * define a posi��o do soldado
	 * @param pos a nova posi��o 
	 */
    public void setPosicao( Point pos ){
    	posicao = pos;
    	// atualizar a posi��o da imagem
    	Point pecran = getCenario().getEcran(pos);
    	figura.setPosicaoCentro( pecran );
    	posicaoCentro = new Point2D.Double( pecran.x, pecran.y );
    }
	
    /**
     * indica em que cen�rio o soldado opera
     * @return o cen�rio em que o soldado opera
     */
	public Cenario getCenario() {
		return cenario;
	}
	
	/**
	 * define o cen�rio onde o soldado opera.
	 * S� deve ser chamado pelo pr�prio cen�rio.
	 * @param c o cen�rio onde o soldado vai operar
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

	/** define se o soldado est� a puxar civis ou n�o
	 * @param puxar se � para puxar
	 */
	public void setPuxar(boolean puxar) {
		this.puxar = puxar;		
		velocidade = puxar? VELOC_PUXAR: VELOC_NORMAL;
	}

	/** retorna a dire��o do movimento
	 * @return a dire��o do movimento
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
