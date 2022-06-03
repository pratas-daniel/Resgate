package obstaculo;

import java.awt.Graphics2D;
import java.awt.Point;

import cenario.*;
import prof.jogos2D.image.*;

public abstract class ObstaculoDefault implements Obstaculo {
	protected ComponenteMultiAnimado visual;
	protected Point posicao;
	protected Cenario cenario;
	
	public ObstaculoDefault (ComponenteMultiAnimado v) {
		visual = v;
	}
	
	public void atualizar() {}
	
	// quando o jogador interage com o obstáculo
	public void ativar() {}
	
	// quando uma pessoa (soldado ou civil) entra num obstáculo
	public void entrar(int pessoa) {}

	// quando uma pessoa (soldado ou civil) sai dum obstáculo
	public void sair(int pessoa) {}
	
	public Cenario getCenario() {
		return cenario;
	}
	
	public void setCenario( Cenario arm ){
		cenario = arm;
	}
	
	public void desenhar(Graphics2D g) {
		visual.desenhar( g );		
	}
	
	public Point getPosicao() {
		return posicao;
	}
	
	public void setPosicao( Point pos ){
    	posicao = pos;
    	Point pecran = cenario.getEcran(pos);
    	visual.setPosicaoCentro( pecran );
	}

	// pergunta se é passável e indica a pessoa que está a tentar passar (soldado ou civl)
	public abstract boolean ePassavel(int pessoa);
	
	public abstract boolean eTransparente();

}
