package obstaculo;

import java.awt.Graphics2D;
import java.awt.Point;

import cenario.*;
import prof.jogos2D.image.*;

public abstract class ObstaculoDefault implements Obstaculo {
	protected ComponenteMultiAnimado visual;
	protected Point posicao;
	protected int status = PARADO;
	protected Cenario cenario;
	
	public ObstaculoDefault (ComponenteMultiAnimado v) {
		setVisual(v);
	}
	
	public void atualizar() {}
	
	public void ativar() {}
	
	public void entrar() {}
	
	public void entrar( Civil c ) {}
	
	public void sair() {}
	
	public Cenario getCenario() {
		return cenario;
	}
	
	public void setCenario( Cenario arm ){
		cenario = arm;
	}
	
	public ComponenteMultiAnimado getVisual() {
		return visual;
	}
	
	public void setVisual(ComponenteMultiAnimado cv) {
		visual = cv;
    	visual.setPosicaoCentro( cenario.getEcran( posicao ) );
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
}
