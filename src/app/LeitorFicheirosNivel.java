package app;

import java.awt.Point;
import java.io.*;

import cenario.Cenario;
import cenario.Civil;
import cenario.Soldado;
import obstaculo.*;
import prof.jogos2D.image.ComponenteAnimado;
import prof.jogos2D.image.ComponenteMultiAnimado;
import prof.jogos2D.image.ComponenteSimples;

/***
 * Classe que trata da leitura e gravação dos níveis.
 */
public class LeitorFicheirosNivel {
	// diretório onde estão as imagens
	private static String artDir = "art/";

	/***
	 * Método que faz a leitura de um ficheiro de nível e retorna o cenário
	 * descrito pelo ficheiro
	 * @param nomeFich nome do ficheiro que contém o nível
	 * @return o cenário desse nível
	 * @throws IOException 
	 */
	public static Cenario lerFicheiro(String nomeFich, Cenario cenario ) throws IOException {		
		BufferedReader in = new BufferedReader( new FileReader( nomeFich ));

		// começar com o fundo do nível
		String linha = in.readLine();
		cenario.setFundo( new ComponenteSimples( artDir + linha ) );
		
	   	// ler o número de refugiados a salvar
	   	String info[] = in.readLine().split(" ");
	   	int numRef = Integer.parseInt( info[0] );
	   	cenario.setNumCivis( numRef );
	   	cenario.setCivisInicio( lerPosicao( info[1]) );
		
		// agora ler a informação dos obstáculos
		linha = in.readLine();
		while( linha != null ) {
			info = linha.split(" ");
			switch( info[0] ){
			case "parede": 
				addParede( cenario, info );
				break;
			case "resgate":
				addResgate( cenario, info );
				break;
			case "civil":
				addCivil( cenario, info );
				break;
			case "soldado":
				addSoldado( cenario, info );
				break;
			case "barricada":
				addBarricada( cenario, info );
				break;					
			case "inimigo":
				addSoldadoInimigo(cenario, info);
				break;
			case "mina":
				addMina(cenario, info);
				break;
			case "porta":
				addPorta(cenario, info);
				break;
			case "empurra":
				addEmpurra(cenario, info);
				break;
			case "alterna":
				addAlterna(cenario, info);
				break;
			case "semaforo":
				addSemaforo(cenario, info);
				break;
			}
			linha = in.readLine();
		}
		in.close();
		return cenario;
	}

	private static void addParede(Cenario c, String[] info) throws IOException {
		Point p = lerPosicao( info[1] );
		ComponenteMultiAnimado img = lerMultiAnimado( info[2] );
		Parede parede = new Parede( img );
		c.colocarObstaculo( p, parede );
	}

	private static void addResgate(Cenario c, String[] info) throws IOException {
		Point p = lerPosicao( info[1] );
		int num = Integer.parseInt( info[2] );
		ComponenteMultiAnimado img = lerMultiAnimado( info[3] );
		ComponenteAnimado sair = lerAnimado( info[4] );
		ZonaResgate zr = new ZonaResgate(num, img, sair);
		c.colocarObstaculo( p, zr );
	}

	private static void addBarricada(Cenario c, String[] info) throws IOException {
		Point p = lerPosicao( info[1] );
		int nivel = Integer.parseInt( info[2] );
		ComponenteMultiAnimado img = lerMultiAnimado( info[3] );
		Barricada ob = new Barricada( img, nivel );
		c.colocarObstaculo(p, ob);
	}
	
	private static void addMina(Cenario c, String[] info) throws IOException {
		Point p = lerPosicao( info[1] );
		ComponenteMultiAnimado img = lerMultiAnimado( info[2] );
		ComponenteAnimado imgFim = lerAnimado( info[3] );
		Mina om = new Mina( img, imgFim );
		c.colocarObstaculo(p, om);
	}
	
	private static void addPorta(Cenario c, String[] info) throws IOException {
		Point p = lerPosicao( info[1] );
		boolean aberta = info[2].equals("aberta");
		ComponenteMultiAnimado img = lerMultiAnimado( info[3] );
		Porta op = new Porta( img, aberta );
		c.colocarObstaculo(p, op);
	}
	
	private static void addEmpurra(Cenario c, String[] info) throws IOException {
		Point p = lerPosicao( info[1] );
		ComponenteMultiAnimado img = lerMultiAnimado( info[2] );
		Empurravel oe = new Empurravel( img );
		c.colocarObstaculo(p, oe);
	}
	
	private static void addAlterna(Cenario c, String[] info) throws IOException {
		Point p = lerPosicao( info[1] );
		boolean on = info[2].equals("on");
		int tOn = Integer.parseInt( info[3] );
		int tOff = Integer.parseInt( info[4] );
		ComponenteMultiAnimado img = lerMultiAnimado( info[5] );
		ComponenteAnimado imgFim = lerAnimado( info[6] );
		Alternavel oa = new Alternavel( on, tOn, tOff, img, imgFim );
		c.colocarObstaculo( p, oa );
	}

	private static void addSoldadoInimigo(Cenario c, String[] info) throws IOException {
		int nPontos = info.length - 2;

		ComponenteMultiAnimado imgInimigo = lerMultiAnimado( info[info.length - 2] );
		ComponenteAnimado tiro = lerAnimado( info[info.length - 1] );
		
		SoldadoInimigo si = new SoldadoInimigo( imgInimigo, tiro );
		for( int i=1; i < nPontos; i++) {
			Point p = lerPosicao( info[i] );
			si.addPontoCaminho( p );
		}
		c.colocarObstaculo( si.getPosicaoInicio(), si );
	}

	private static void addCivil(Cenario arm, String[] info) throws IOException {
		Point p = lerPosicao( info[1] );
		Civil c = new Civil( new ComponenteMultiAnimado( new Point(), artDir + info[2], 2, 4, 4 ) );
		arm.colocarCivil( p, c );
	}

	private static void addSoldado(Cenario arm, String[] info) throws IOException {
		Point p = lerPosicao( info[1] );		
		ComponenteMultiAnimado img = new ComponenteMultiAnimado( new Point(), artDir + info[2], 3, 4, 4 );
		Soldado op = new Soldado( img );
		arm.colocarSoldado( p, op );
	}
	
	private static void addSemaforo(Cenario c, String[] info) throws IOException {
		Point p = lerPosicao( info[1] );
		ComponenteMultiAnimado img = lerMultiAnimado( info[2] );
		ComponenteAnimado imgFim = lerAnimado( info[3] );
		Semaforo os = new Semaforo( img, imgFim );
		c.colocarObstaculo(p, os);
	}

	private static ComponenteMultiAnimado lerMultiAnimado(String info) throws IOException {
		String imgInfo[] = info.split(",");
		String file = artDir + imgInfo[0];
		int nAnims = Integer.parseInt( imgInfo[1] );
		int nFrames = Integer.parseInt( imgInfo[2] );
		int delay = Integer.parseInt( imgInfo[3] );
		ComponenteMultiAnimado cma = new ComponenteMultiAnimado( new Point(), file, nAnims, nFrames, delay ); 
		switch( imgInfo[4] ) {
		case ">": cma.setAngulo( 0 ); break;
		case "^": cma.setAngulo( Math.PI/2 ); break;
		case "<": cma.setAngulo( Math.PI ); break;
		case "v": cma.setAngulo( 3*Math.PI/2 ); break;
		}
		return cma;
	}
	
	private static ComponenteAnimado lerAnimado(String info) throws IOException {
		String imgInfo[] = info.split(",");
		String file = artDir + imgInfo[0];
		int nFrames = Integer.parseInt( imgInfo[1] );
		int delay = Integer.parseInt( imgInfo[2] );
		return new ComponenteAnimado( new Point(), file, nFrames, delay );
	}
	
	/** lê a posição a partir de uma string */
	private static Point lerPosicao(String pos) {
		String xy[] = pos.split(",");
		return new Point( Integer.parseInt(xy[0]), Integer.parseInt(xy[1]) );
	}
}
