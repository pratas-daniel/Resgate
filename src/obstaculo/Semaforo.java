package obstaculo;

import prof.jogos2D.image.*;

public class Semaforo extends ObstaculoDefault{
	private ComponenteAnimado imagemFinal;
	private boolean ativo = false;
	private boolean semaforoOcupadoSoldado = false;
	private boolean semaforoOcupadoCivil = false;
	private int passTimer = 80;
	
	public Semaforo(ComponenteMultiAnimado vis, ComponenteAnimado imgFim) {
		super(vis);
		imagemFinal = imgFim;
	}
	
	// ativa o semaforo
	@Override
	public void ativar() {
		if (!ativo) {
			ativo = true;
			visual.setAnim(2);
			visual.reset();
		}
	}
	
	// verifica se o semaforo está ativo e controla as luzes conforme a situação
	// se um soldado ou civil estiver no semaforo quando o tempo acaba perde o nível
	@Override
	public void atualizar() {
		if (ativo) {
			passTimer--;
			if (passTimer == 40) {
				visual.setAnim(1);
				visual.reset();
			}
			if (passTimer == 0) {
				ativo = false;
				if (semaforoOcupadoSoldado || semaforoOcupadoCivil) {
					imagemFinal.setPosicaoCentro( visual.getPosicaoCentro() );
					cenario.iniciaFimNivel( false, imagemFinal );
				}
				visual.setAnim(0);
				visual.reset();
				passTimer = 80;
			}
		}
	}
	
	// quando um soldado ou civil entra no semaforo ele fica ocupado
	@Override
	public void entrar(int pessoa) {
		if (pessoa == SOLDADO)
			semaforoOcupadoSoldado = true;
		else if (pessoa == CIVIL)
			semaforoOcupadoCivil = true;
	}
	
	// quando um soldado ou civil sai do semaforo ele fica livre
	@Override
	public void sair(int pessoa) {
		if (pessoa == SOLDADO)
			semaforoOcupadoSoldado = false;
		else if (pessoa == CIVIL)
			semaforoOcupadoCivil = false;
	}

	public boolean ePassavel(int pessoa) {
		return ativo;
	}

	public boolean eTransparente() {
		return true;
	}
}
