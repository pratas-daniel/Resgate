package obstaculo;

import prof.jogos2D.image.ComponenteMultiAnimado;

public class Porta extends ObstaculoDefault {
	private boolean aberta;
	private boolean startTimer = false;
	private int animTimer = 10;
	
	public Porta (ComponenteMultiAnimado vis, boolean ab) {
		super(vis);
		aberta = ab;
	}
	
	@Override
	public void ativar() {
		if (aberta) {
			visual.setAnim( 3 );
			visual.reset();
		} else {
			visual.setAnim( 1 );
			visual.reset();
		}
		startTimer = true;
		aberta = !aberta;
	}
	
	@Override
	public void atualizar() {
		if (startTimer) {
			animTimer--;
			if (animTimer == 0) {
				if (aberta) {
					visual.setAnim( 2 );
					visual.reset();
				} else {
					visual.setAnim( 0 );
					visual.reset();
				}
				startTimer = false;
				animTimer = 10;
			}
		}
	}

	@Override
	public boolean ePassavel(int pessoa) {
		return aberta ? true : false;
	}

	@Override
	public boolean eTransparente() {
		return aberta ? true : false;
	}
}
