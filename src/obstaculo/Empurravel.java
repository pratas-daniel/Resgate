package obstaculo;

import prof.jogos2D.image.ComponenteMultiAnimado;
import prof.jogos2D.util.Vector2D;
import java.awt.*;

public class Empurravel extends ObstaculoDefault{
	
	public Empurravel(ComponenteMultiAnimado vis) {
		super(vis);
	}
	
	@Override
	public void ativar() {
		Vector2D dir = cenario.getSoldado().getDirecao();
		int xd = (int)(posicao.x + dir.x);
		int yd = (int)(posicao.y + dir.y);
		
		Point dest = new Point(xd, yd);
		if( !cenario.estaOcupado( dest ) ) {
			cenario.moverObstaculo( this, dest);
		}
	}

	@Override
	public boolean ePassavel(int pessoa) {
		return false;
	}

	@Override
	public boolean eTransparente() {
		return false;
	}
}
