package obstaculo;


import java.awt.*;
import cenario.Cenario;

public interface Obstaculo {
	// constantes para indicar a pessoa que est� a interagir com o obst�culo (soldado ou civil)
	int SOLDADO = 0;
	int CIVIL = 1;
	
	void atualizar();
	boolean ePassavel(int pessoa);
	boolean eTransparente();	
	void ativar();
	void entrar(int pessoa);
	void sair(int pessoa);
	Cenario getCenario();
	void setCenario(Cenario arm);
	void desenhar(Graphics2D g);
	Point getPosicao();
	void setPosicao(Point pos);
}
