package obstaculo;

import prof.jogos2D.image.ComponenteMultiAnimado;

public class Barricada extends ObstaculoDefault {
	private int nivel;
	private int animTimer = 10;
	
	public Barricada (ComponenteMultiAnimado vis, int nvl) {
		super(vis);
		nivel = nvl;
	}
	
	@Override
	public void ativar() {
		nivel--;
		if (nivel == 1) {
			visual.setAnim( 1 );
			visual.reset();
		} else if (nivel == 0) {
			visual.setAnim( 3 );
			visual.reset();
		}
	}
	
	@Override
	public void atualizar() {
		if (nivel == 1) {
			animTimer--;
			if (animTimer == 0) {
				visual.setAnim( 2 );
				visual.reset();
				animTimer = 10;
			}
		} else if (nivel == 0) {
			animTimer--;
			if (animTimer == 0) {
				visual.setPausa(true);
				visual.reset();
			}
		}
	}

	@Override
	public boolean ePassavel(int pessoa) {
		return nivel > 0 ? false : true;
	}

	@Override
	public boolean eTransparente() {
		return nivel > 0 ? false : true;
	}
}
