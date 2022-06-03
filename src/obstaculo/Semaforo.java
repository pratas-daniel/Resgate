package obstaculo;

import prof.jogos2D.image.*;

public class Semaforo extends ObstaculoDefault{
	private ComponenteAnimado imagemFinal;
	private boolean ativo = false;
	private boolean semaforoOcupadoSoldado = false;
	private boolean semaforoOcupadoCivil = false;
	private int passTimer = 50;
	
	public Semaforo(ComponenteMultiAnimado vis, ComponenteAnimado imgFim) {
		super(vis);
		imagemFinal = imgFim;
	}
	
	@Override
	public void ativar() {
		if (!ativo) {
			ativo = true;
			visual.setAnim(1);
			visual.reset();
		}
	}
	
	@Override
	public void atualizar() {
		if (ativo) {
			passTimer--;
			if (passTimer == 0) {
				if (semaforoOcupadoSoldado || semaforoOcupadoCivil) {
					imagemFinal.setPosicaoCentro( visual.getPosicaoCentro() );
					cenario.iniciaFimNivel( false, imagemFinal );
				}
				ativo = false;
				visual.setAnim(0);
				visual.reset();
				passTimer = 50;
			}
		}
	}
	
	@Override
	public void entrar(int pessoa) {
		if (pessoa == SOLDADO)
			semaforoOcupadoSoldado = true;
		else if (pessoa == CIVIL)
			semaforoOcupadoCivil = true;
	}
	
	@Override
	public void sair(int pessoa) {
		if (pessoa == SOLDADO)
			semaforoOcupadoSoldado = false;
		else if (pessoa == CIVIL)
			semaforoOcupadoCivil = false;
	}

	@Override
	public boolean ePassavel(int pessoa) {
		return ativo;
	}

	@Override
	public boolean eTransparente() {
		return true;
	}
}
