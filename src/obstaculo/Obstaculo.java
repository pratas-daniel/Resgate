package obstaculo;


import java.awt.*;
import cenario.Cenario;
import prof.jogos2D.image.ComponenteMultiAnimado;

public interface Obstaculo {
	
	int PARADO = 0;
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
