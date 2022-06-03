package obstaculo;

import prof.jogos2D.image.ComponenteMultiAnimado;

public class Parede extends ObstaculoDefault {
	
	public Parede (ComponenteMultiAnimado vis) {
		super(vis);
	}

	public boolean ePassavel(int pessoa) {
		return false;
	}
	
	public boolean eTransparente() {
		return false;
	}
	
	
}
