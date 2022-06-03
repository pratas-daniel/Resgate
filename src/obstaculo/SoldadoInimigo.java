package obstaculo;

import java.util.ArrayList;
import java.awt.*;
import java.awt.geom.Point2D;

import prof.jogos2D.image.*;
import prof.jogos2D.util.Vector2D;

public class SoldadoInimigo extends ObstaculoDefault{
	private ArrayList<Point> caminho = new ArrayList<Point>(); // caminho por onde anda
	private int velocidade = 5;   // velocidade de movimento
	private int posCaminho = 0;   // qual a posi��o do caminho em que est�
	private int dirCaminho = 1;   // a dire��o a percorrer o caminho
	private Vector2D direcao;     // a dire��o de movimento
	private Point destinoCentro, destino; // posi��o em pixeis e posi��o em quadriculas do destino
	private Point2D.Double posCentro;     // posi��o atual em pixeis
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
	private void mover() {
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
