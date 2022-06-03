package obstaculo;

import java.awt.Point;

import cenario.Civil;
import prof.jogos2D.image.*;

public class ZonaResgate extends ObstaculoDefault{
	private static final int RECEBER = 10;
	private ComponenteAnimado imagemFinal;
	private boolean resgateOcupado = false;
	private int numAceites;
	
	public ZonaResgate (int numCiv, ComponenteMultiAnimado vis, ComponenteAnimado imgFim) {
		super(vis);
		numAceites = numCiv;
	}
	
	@Override
	public void entrar(){
		if( numAceites > 0 )
			numAceites--;
		if( numAceites == 0 ) 
			resgateOcupado = true;
		status = RECEBER;
		visual.setAnim( 1 );
		visual.reset();
	}
	
	@Override
	public void atualizar() {
		if( status == RECEBER) {
			if( visual.numCiclosFeitos() < 1 )
				return;

			if( !resgateOcupado ) {
				status = PARADO;
				visual.setAnim( 0 );
				visual.reset();
			}
			else {
				imagemFinal.setPosicaoCentro( visual.getPosicaoCentro() );
				cenario.removerObstaculo( posicao );
				cenario.iniciaFimNivel( true, imagemFinal );
			}
		}
		
		// se tiver algum civil, tem de o remover
		cenario.removerCivil( posicao );
		
		// ver se já os recebeu a todos
		if( numAceites == 0 )
			return;
		
		// ver se ainda há civis para "puxar"
		Point []ondeVer = { new Point(posicao.x+1, posicao.y), new Point(posicao.x-1, posicao.y),
	                        new Point(posicao.x, posicao.y+1), new Point(posicao.x, posicao.y-1) };

		for( Point p : ondeVer ) {
			Civil c = cenario.getCivil( p );			
			if( c != null  ) {
				c.deslocar(posicao.x-p.x, posicao.y - p.y);
				break;
			}
		}
	}
	
	@Override
	public boolean eTransparente() {
		return false;
	}
	
	@Override
	public boolean ePassavel() {
		return false;
	}
	
}
