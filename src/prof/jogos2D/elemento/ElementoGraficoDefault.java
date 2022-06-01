package prof.jogos2D.elemento;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import prof.jogos2D.image.ComponenteVisual;

/**
 * Classe que implementa o comportamento básico de um elemento gráfico
 * 
 * @author F. Sérgio Barbosa
 */
public abstract class ElementoGraficoDefault implements ElementoGrafico {

	private ComponenteVisual imagem;
	
	/** Cria e inicializa o elemento gráfico
	 * 
	 * @param image imagem associado ao elemento gráfico
	 */
	public ElementoGraficoDefault( ComponenteVisual image) {
		setImage( image );
	}
	
	@Override
	public void desenhar(Graphics2D g) {
		imagem.desenhar( g );
	}

	@Override
	public ComponenteVisual getImage() {
		return imagem;
	}

	@Override
	public void setImage(ComponenteVisual image) {
		imagem = image;
	}

	@Override
	public void setPosicao(Point p) {
		imagem.setPosicao( p );
	}

	@Override
	public Point getPosicao() {
		return imagem.getPosicao();
	}
	
	@Override
	public Rectangle getBounds() {
		return imagem.getBounds();
	}
}
