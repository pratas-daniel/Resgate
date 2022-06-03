package obstaculo;

import prof.jogos2D.image.ComponenteMultiAnimado;

public class Parede extends ObstaculoDefault {
	
	public Parede (ComponenteMultiAnimado vis) {
		super(vis);
	}

	@Override
	public boolean ePassavel() {
		return false;
	}
	
	@Override
	public boolean podeOcupar() {
		return false;
	}

	@Override
	public boolean eTransparente() {
		return false;
	}
	
	
}
