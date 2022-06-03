package obstaculo;


import java.awt.*;
import cenario.Cenario;

public interface Obstaculo {
	void atualizar();
	boolean ePassavel();
	boolean eTransparente();	
	boolean podeOcupar();
	void ativar();
	void entrar();
	void sair();
	Cenario getCenario();
	void setCenario(Cenario arm);
	void desenhar(Graphics2D g);
	Point getPosicao();
	void setPosicao(Point pos);
}
