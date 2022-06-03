package obstaculo;

import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.Point2D;

import prof.jogos2D.image.*;
import prof.jogos2D.util.Vector2D;

public class SoldadoInimigo extends ObstaculoDefault{
	private ArrayList<Point> caminho = new ArrayList<Point>(); // caminho por onde anda
	private int velocidade = 5;   // velocidade de movimento
	private int posCaminho = 0;   // qual a posição do caminho em que está
	private int dirCaminho = 1;   // a direção a percorrer o caminho
	private Vector2D direcao;     // a direção de movimento
	private Point destinoCentro, destino; // posição em pixeis e posição em quadriculas do destino
	private Point2D.Double posCentro;     // posição atual em pixeis
	private ComponenteAnimado imagemFinal;
	
	public SoldadoInimigo(ComponenteMultiAnimado vis, ComponenteAnimado imgFim) {
		super(vis);
		imagemFinal = imgFim;
		getVisual().setAnim( 1 );
	}
	
	@Override
	public void atualizar() {
		mover();
		if( cenario.temPessoas( posicao, direcao ) ) {
			Point posTiro = (Point)posicao.clone();
			posTiro.translate( (int)direcao.x,  (int)direcao.y);
			imagemFinal.setAngulo( direcao.getAngulo() );
			imagemFinal.setPosicaoCentro( cenario.getEcran( posTiro ) );
			cenario.iniciaFimNivel( false, imagemFinal );
		}
	}
	
	@Override
	public boolean ePassavel() {
		return false;
	}
	
	@Override
	public boolean eTransparente() {
		return false;
	}
	
	@Override
	public void setPosicao( Point pos ){
    	posicao = pos;
    	Point pecran = cenario.getEcran(pos);
    	visual.setPosicaoCentro( pecran ); 
		direcao = new Vector2D(0,0);
    	destinoCentro = pecran;
    	posCentro = new Point2D.Double( pecran.x, pecran.y );
    }

	
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
	private void mover() {
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
