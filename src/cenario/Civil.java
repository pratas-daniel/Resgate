package cenario;

import java.awt.*;
import java.awt.geom.Point2D;

import obstaculo.Obstaculo;
import prof.jogos2D.image.ComponenteMultiAnimado;
import prof.jogos2D.image.ComponenteVisual;
import prof.jogos2D.util.Vector2D;

/**
 * Classe que representa um Civil. 
 */
public class Civil {

	/** imagem do civil. Deve ter duas animações:
	 * zero = animação do civil parado
	 * um   = animação do civil a andar
	 */
	private ComponenteMultiAnimado figura;

	/** posição do civil */
	private Point posicao; 
	
	// em que cenário está colocado
	protected Cenario cenario;

	/** indica se o civil já se mexeu num ciclo */
	protected boolean moveu = false;
	
	/** variáveis para o estado do civil */
	private static final int PARADO = 0; 
	private static final int ANDAR = 1;
	/** estado atual do civil */
	private int status;
	
	/** direção de movimento */
	private Vector2D direcao = new Vector2D(1,0);
	/** posição do centro da imagem e posição para onde quer ir */ 
	private Point2D.Double posicaoCentro, destinoCentro;

	
	/**
	 * construtor do Civil
	 * @param fig imagem representativa do Civil. Esta Imagem
	 * deve ter, pelo menos, duas animações:<br>
	 *  0 = animação do civil parado
	 *  1 = animação do civil a andar 
	 */
	public Civil( ComponenteMultiAnimado fig ){
		figura = fig;
		setStatus( PARADO );
	}
	
	/** altera o estado do Civil
	 * @param s o novo estado
	 */
	private void setStatus(int s ) {
		status = s;
		// escolhe a animação adequado ao estado
		figura.setAnim( status);	
		figura.setAngulo( direcao.getAngulo() );
	}

	/** faz um ciclo de atualização
	 */
	public void atualizar() {
		if( status == PARADO )
			return;
		
		if( status == ANDAR ) {
			float veloc = getCenario().getSoldado().getVelocidade();
			posicaoCentro.x += veloc * direcao.x;
			posicaoCentro.y += veloc * direcao.y;
			figura.setPosicaoCentro( new Point( (int)posicaoCentro.x, (int) posicaoCentro.y ) );
	
			if( posicaoCentro.distance( destinoCentro ) < veloc ) {
				setStatus( PARADO );
				moveu = false;
			}
		}
	}	

	
	/**
	 * indica se se pode deslocar de uma dada distância
	 * @param dx deslocamento em x
	 * @param dy deslocamento em y
	 * @return true se se pode deslocar
	 */
    public boolean podeDeslocar( int dx, int dy ){
    	// ver o ponto de destino
		Point dest = (Point)getPosicao().clone();
		dest.translate(dx,dy);
				
		//ver se o destino é no cenário
		if( !cenario.eCoordenadaValida(dest) )
			return false;
				
		// ver se no destino tem outro civil
		if( cenario.getCivil( dest ) != null )
			return false;
			
		// ver se há obstáculo no destino e se pode ir para lá
		Obstaculo a = cenario.getObstaculo( dest );
		if( a != null  && !a.podeOcupar() )
			return false;
		
		return true;
    }
    
    /**
     * desloca o civil
     * @param dx deslocamento em x
     * @param dy deslocamento em y
     * @return se se deslocou ou não
     */
	public boolean deslocar( int dx, int dy ) {
		// ver se pode mover, senão não move
	    if( !podeDeslocar( dx, dy ) || status == ANDAR )
	        return false;
	    
	    Point pecran = getCenario().getEcran( getPosicao() );
    	figura.setPosicaoCentro( pecran );
    	posicaoCentro = new Point2D.Double( pecran.x, pecran.y );
	    
	    direcao = new Vector2D(dx,dy);
	    Point pos = (Point)posicao.clone();
	    setStatus( ANDAR );
	    Point destino = new Point( posicao.x + dx, posicao.y + dy);
	    Point d = getCenario().getEcran( destino );
		destinoCentro = new Point2D.Double( d.x, d.y );
	    
	    // o deslocar é remover da origem e colocar no destino
	    cenario.removerCivil( posicao );
	    cenario.colocarCivil( destino, this );
		moveu = true;
	    
	    // verificar se há outros civis para serem puxados
		Point []ondeVer = { new Point(pos.x+1, pos.y), new Point(pos.x-1, pos.y),
        	                new Point(pos.x, pos.y+1), new Point(pos.x, pos.y-1) };
		for( Point p : ondeVer ) {
			if( p.equals( posicao ) ) 
				continue;
			Civil c = cenario.getCivil( p );
			// se há civis é preciso puxá-los para a posição onde este estava
			if( c != null  && c != this && !c.jaMoveu()) {
				c.deslocar(pos.x-p.x, pos.y - p.y);
				break;
			}
		}

	    return true;
	}	

	/** indica se o civil já se mexeu num ciclo de processamento
	 * @return true, se ele já se moveu
	 */
	public boolean jaMoveu() {
		return moveu;
	}
	
	/** desenhar o civil
	 * @param g onde desenhar
	 */
	public void desenhar( Graphics2D g ){
		figura.desenhar( g );
	}
	
	/**
	 * devolve a posição do civil
	 * @return a posição do civil
	 */
	public Point getPosicao() {
		return posicao;
	}

	/**
	 * define a posição do civil. Deve ser chamado apenas pelo Cenário
	 * @param pos a nova posição a ocupar pelo civil
	 */
    public void setPosicao( Point pos ){
    	posicao = pos;
    	figura.setPosicaoCentro( getCenario().getEcran( pos ));
    }

    /**
     * devolve o cenário onde este civil está colocado
     * @return o cenário onde este civil está colocado
     */
	public Cenario getCenario() {
		return cenario;
	}
	
	/**
	 * define qual o cenário onde o civil está colocado.
	 * Só deve ser chamado pelo próprio Civil, dai ter nível package 
	 * @param c o cenário
	 */
	void setCenario( Cenario c ){
		cenario = c;
	}    
	
	/**
	 * devolve a figura que representa o civil
	 * @return a figura 
	 */
	public ComponenteVisual getFigura( ) {
		return figura;		
	}
	
	/**
	 * define a figura do civil. Esta figura
	 * deve ter, pelo menos, duas animações:<br>
	 *  0 = animação do civil parado
	 *  1 = animação do civil a andar 
	 * @param fig a nova figura do civil
	 */
	public void setFigura(ComponenteMultiAnimado fig) {
		figura = fig;
	}

}
