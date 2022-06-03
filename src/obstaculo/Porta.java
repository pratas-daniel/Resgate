package obstaculo;

import prof.jogos2D.image.ComponenteMultiAnimado;

public class Porta extends ObstaculoDefault {
	private boolean aberta;
	
	public Porta (ComponenteMultiAnimado vis, boolean ab) {
		super(vis);
		aberta = ab;
	}
	
	@Override
	public void ativar() {
		aberta = !aberta;
	}

	@Override
	public boolean ePassavel() {
		return aberta ? true : false;
	}

	@Override
	public boolean eTransparente() {
		return aberta ? true : false;
	}
}
