package obstaculo;

import java.awt.Point;

import cenario.Civil;
import prof.jogos2D.image.*;

public class ZonaResgate extends ObstaculoDefault{
	private ComponenteAnimado imagemFinal;
	private boolean resgateOcupado = false;
	private int numAceites;
	private boolean receber = false;
	
	public ZonaResgate (int numCiv, ComponenteMultiAnimado vis, ComponenteAnimado imgFim) {
		super(vis);
		numAceites = numCiv;
		imagemFinal = imgFim;
	}
	
	@Override
	public void entrar(int pessoa){
		if( numAceites > 0 )
			numAceites--;
		if( numAceites == 0 ) 
			resgateOcupado = true;
		receber = true;
		visual.setAnim( 1 );
		visual.reset();
	}
	
	@Override
	public void atualizar() {
		if( receber) {
			if( visual.numCiclosFeitos() < 1 )
				return;

			if( !resgateOcupado ) {
				receber = false;
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
	public boolean ePassavel(int pessoa) {
		if (pessoa == CIVIL)
			return true;
		else 
			return false;
		
	}
}
