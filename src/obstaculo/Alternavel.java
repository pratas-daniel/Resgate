package obstaculo;

import prof.jogos2D.image.*;

public class Alternavel extends ObstaculoDefault{
	private ComponenteAnimado imagemFinal;
	private int tempo, tempoOn, tempoOff;
	private boolean alternaOcupado;
	private boolean ativo = false;
	
	public Alternavel(boolean on, int tempoOn, int tempoOff, ComponenteMultiAnimado vis, ComponenteAnimado imgFim) {
		super(vis);
		setStatusAtivo( on );
		this.tempoOn = tempoOn;
		this.tempoOff = tempoOff;
		tempo = on? tempoOn: tempoOff;
		this.imagemFinal = imgFim;
	}
	
	private void setStatusAtivo(boolean on) {
		ativo = on;
		if( ativo ) {
			visual.setAnim( 1 );
			visual.reset();
			tempo = tempoOn;				
		}
		else {
			visual.setAnim( 0 );
			visual.reset();
			tempo = tempoOff;
		}		
	}
	
	@Override
	public void entrar() {
		alternaOcupado = true;
	}
	
	@Override
	public void sair() {
		alternaOcupado = false;
	}
	
	@Override
	public void atualizar() {
		tempo--;
		if( tempo <= 0 ) {
			setStatusAtivo( !ativo );						
		}
		if( ativo && alternaOcupado ) {
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
		return !ativo;
	}
}
