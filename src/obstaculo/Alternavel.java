package obstaculo;

import prof.jogos2D.image.*;

public class Alternavel extends ObstaculoDefault{
	private ComponenteAnimado imagemFinal;
	private int tempo, tempoOn, tempoOff;
	private boolean alternaOcupadoSoldado = false;
	private boolean alternaOcupadoCivil = false;
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
	public void entrar(int pessoa) {
		if (pessoa == SOLDADO)
			alternaOcupadoSoldado = true;
		else if (pessoa == CIVIL)
			alternaOcupadoCivil = true;
	}
	
	@Override
	public void sair(int pessoa) {
		if (pessoa == SOLDADO)
			alternaOcupadoSoldado = false;
		else if (pessoa == CIVIL)
			alternaOcupadoCivil = false;
	}
	
	@Override
	public void atualizar() {
		tempo--;
		if( tempo <= 0 ) {
			setStatusAtivo( !ativo );						
		}
		if( ativo && (alternaOcupadoSoldado || alternaOcupadoCivil)) {
			imagemFinal.setPosicaoCentro( visual.getPosicaoCentro() );
			cenario.iniciaFimNivel( false, imagemFinal );
		}
	}
	
	public boolean ePassavel(int pessoa) {
		return true;
	}
	
	public boolean eTransparente() {
		return !ativo;
	}
}
