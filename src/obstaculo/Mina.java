package obstaculo;

import prof.jogos2D.image.*;

public class Mina extends ObstaculoDefault {
	private ComponenteAnimado imagemFinal;
	private boolean minaOcupada = false;
	
	public Mina(ComponenteMultiAnimado vis, ComponenteAnimado imgFim) {
		super(vis);
		imagemFinal = imgFim;
	}
	
	@Override
	public void entrar() {
		minaOcupada = true;
	}
	
	@Override
	public void atualizar() {
		if (minaOcupada) {
			imagemFinal.setPosicaoCentro( visual.getPosicaoCentro() );
			cenario.iniciaFimNivel( false, imagemFinal );
		}
	}

	@Override
	public boolean ePassavel() {
		return true;
	}
	
	@Override
	public boolean podeOcupar() {
		return true;
	}

	@Override
	public boolean eTransparente() {
		return true;
	}
}
