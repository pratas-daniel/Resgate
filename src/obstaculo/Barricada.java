package obstaculo;

import prof.jogos2D.image.ComponenteMultiAnimado;

public class Barricada extends ObstaculoDefault {
	private int nivel;
	
	public Barricada (ComponenteMultiAnimado vis, int nvl) {
		super(vis);
		nivel = nvl;
	}
	
	@Override
	public void ativar() {
		nivel--;
	}

	@Override
	public boolean ePassavel() {
		return nivel > 0 ? false : true;
	}

	@Override
	public boolean eTransparente() {
		return nivel > 0 ? false : true;
	}
}
