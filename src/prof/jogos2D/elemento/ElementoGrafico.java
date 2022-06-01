package prof.jogos2D.elemento;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import prof.jogos2D.image.ComponenteVisual;

/**
 * Interface que define o comportamento que todos os elementos gráficos terão de implememtar
 * Assume-se que cada elemento gráfico terá uma imagem que o representa e uma posição no écran
 * 
 * @author F. Sérgio Barbosa
  */
public interface ElementoGrafico {

	/** desenha o elemento gráfico 
	 * @param g ambiente gráfico onde desenhar o elemento gráfico
	 */
	public void desenhar( Graphics2D g );
	
	/** atualiza o elemento gráfico, pois este poderá ter algum comportamento
	 */
	public void atualizar( );

	/** retorna o elemento visual associado ao elemento gráfico
	 * @return o elemento visual associado ao elemento gráfico
	 */
	public ComponenteVisual getImage();
	
	/** altera a imagem associada ao elemento gráfico
	 * @param image a nova imagem a associar ao elemento gráfico
	 */
	public void setImage( ComponenteVisual image );
	
	/** define a posição do elemento gráfico
	 * @param p a nova posição do elemento gráfico
	 */
	public void setPosicao( Point p );
	
	/** retorna a posição onde está 
	 * @return a posição onde está
	 */
	public Point getPosicao( );
	
	/** retorna a área que este elemento gráfico ocupa
	 * @return a área que este elemento gráfico ocupa
	 */
	public Rectangle getBounds();
}
