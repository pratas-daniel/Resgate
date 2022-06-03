package obstaculo;

import prof.jogos2D.image.*;

public class Mina extends ObstaculoDefault {
	private ComponenteAnimado imagemFinal;
	private boolean minaOcupada = false;
	
	public Mina(ComponenteMultiAnimado vis, ComponenteAnimado imgFim) {
		super(vis);
		imagemFinal = imgFim;
	}
	
	// se alguém entra na mina ela fica ocupada
	@Override
	public void entrar(int pessoa) {
		minaOcupada = true;
	}
	
	// se está ocupada perde o nível
	@Override
	public void atualizar() {
		if (minaOcupada) {
			imagemFinal.setPosicaoCentro( visual.getPosicaoCentro() );
			cenario.iniciaFimNivel( false, imagemFinal );
		}
	}

	public boolean ePassavel(int pessoa) {
		return true;
	}

	public boolean eTransparente() {
		return true;
	}
}
