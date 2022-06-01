package cenario;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import obstaculo.Obstaculo;
import prof.jogos2D.image.ComponenteAnimado;
import prof.jogos2D.image.ComponenteMultiAnimado;
import prof.jogos2D.image.ComponenteVisual;
import prof.jogos2D.util.Vector2D;

/**
 * Classe que representa o cen�rio onde ir� decorrer o jogo
 */
public class Cenario {

	// constantes com o comprimento e altura
	// mas podiam ser personaliz�veis
	private static final int COMPRIMENTO = 25;
	private static final int ALTURA = 16;
	
    private ComponenteVisual fundo;  // figura associada ao fundo do cen�rio
    private int dimQuadricula;        // a dimens�o de cada quadricula
    private Point posicao;            // posi��o da imagem 
	private Soldado soldado;          // o soldado controlado pelo jogador

	/** o array de obst�culos */
	private Obstaculo obstaculos[][] = new Obstaculo[COMPRIMENTO][ALTURA];

	/** o array de civis */
	private Civil civis[][] = new Civil[COMPRIMENTO][ALTURA];
	
	/** o n�mero de refugiados neste cen�rio */
	private int numeroRefugiados;
	private Point saidaRefugiados;  // o ponto de onde os refugiados saiem
	
	// vari�veis para controlar o fim de n�vel
	private boolean ganhou = false, acabou = false;
	private ComponenteAnimado animacaoFinal;
	
	/**
	 * Construtor do cen�rio
	 * @param fig a figura do fundo 
	 * @param t a coordenada no �cran
	 * @param dimQuadricula a dimens�o de cada quadr�cula
	 */
    public Cenario( ComponenteVisual fig, Point t, int dimQuadricula ){
    	this.dimQuadricula = dimQuadricula;
    	posicao = t;
    	fundo = fig;
    	if( fundo != null )
    		fundo.setPosicao( t );
     }
    
    /** Construtor do Cen�rio
	 * @param t a coordenada do cen�rio
	 * @param dimAzul a dimens�o de cada azulejo 
     */
    public Cenario( Point t, int dimAzul ){
    	this( null, t, dimAzul );
    }
    
    /** devolve a altura em quadr�culas
     * @return a altura em quadr�culas
     */
    public int getAltura(){
    	return ALTURA;
    }

    /** devolve o comprimento em quadr�culas
     * @return o comprimento em quadr�culas
     */
    public int getComprimento(){
    	return COMPRIMENTO;
    }
    
    /** define a imagem de fundo
     * @param fundo a nova imagem de fundo
     */
    public void setFundo( ComponenteVisual fundo ){
    	this.fundo = fundo;
    	fundo.setPosicao( posicao );
    }

    /** Atualiza o cenario e respetivos elementos
     */
    public void atualizar() { 
    	if( acabou )
    		return;
    	
        for( int x = 0; x < obstaculos.length; x++ )
             for( int y = 0; y < obstaculos[x].length; y++ ){
                  if( obstaculos[x][y] != null )
                	  obstaculos[x][y].atualizar();
                  if( civis[x][y] != null )
                	  civis[x][y].atualizar();
             }
        if( soldado != null )
        	soldado.atualizar( );
    }
    
    /** desenha todos os elementos no cen�rio
     * @param g o ambiente gr�fico onde desenhar
     */
    public void desenhar( Graphics2D g ) {    	
    	fundo.desenhar( g );

        for( int y = 0; y < obstaculos[0].length; y++ )
             for( int x = 0; x < obstaculos.length; x++ ){
                  if( obstaculos[x][y] != null )
                	  obstaculos[x][y].desenhar( g );
                  if( civis[x][y] != null )
                	  civis[x][y].desenhar( g );
             }
        if( soldado != null )
        	soldado.desenhar( g );
        
    	if( animacaoFinal != null )
    		animacaoFinal.desenhar(g);
    }

   /** indica se a coordenada � valida 
    * @param p a coordenada a verificar
    * @return true, se a coordenada � valida 
    */
    public boolean eCoordenadaValida( Point p ) {
        return p != null && p.x>=0 && p.x < obstaculos.length && p.y>=0 && p.y< obstaculos[0].length;
    }

    /** coloca um obst�culo no cen�rio. 
     * @param p posi��o onde colocar o obst�culo
     * @param o obst�culo a colocar
     */
    public void colocarObstaculo( Point p, Obstaculo o ){
        if( o == null ) {
        	removerObstaculo(p);
        	return;
        }
        if( !eCoordenadaValida( p ) )
            return;
        obstaculos[p.x][p.y] = o;        
        o.setCenario( this );
        o.setPosicao( p );                
    }
    
    /**
     * remove o obst�culo da coordenada indicada   
     * @param p coordenda de onde retirar o obst�culo
     * @return o obst�culo retirado
     */
    public Obstaculo removerObstaculo( Point p ){
        if( !eCoordenadaValida( p ) )
            return null;
            
        Obstaculo old = obstaculos[p.x][p.y];
        obstaculos[p.x][p.y] = null;
        // se se remove o azulejo tamb�m se tem de remover o caixote, e o oper�rio
        civis[p.x][p.y] = null; 
        if( soldado != null && soldado.getPosicao().equals( p ) )
        	soldado = null;
        return old;        
    }
    
    /** move um obst�culo de posi��o
     * @param o obst�culo a ser removido
     * @param dest destino para onde se vai mover
     * @return true, se moveu o obst�culo
     */
    public boolean moverObstaculo( Obstaculo  o, Point dest ){
        if( o== null || o.getCenario() != this || !eCoordenadaValida( dest ) )
            return false;
    	
        obstaculos[o.getPosicao().x][o.getPosicao().y] = null;
        obstaculos[dest.x][dest.y] = o;
        o.setPosicao( dest );
        return true;
    }
    
    /** devolve o obst�culo presente na coordenada indicada
     * @param p a coordenada onde est� o obst�culo pretendido
     * @return o azulejo presente na coordenada indicada
     */
    public Obstaculo getObstaculo( Point p ){
        if( !eCoordenadaValida( p ) )
            return null;
        
        return obstaculos[ p.x ][ p.y ];        
    }
    
    /** devolve os obst�culos presentes no cen�rio
     * @return os obst�culos presentes no cen�rio
     */
    public java.util.List<Obstaculo> getObstaculos(){
    	ArrayList<Obstaculo> as = new ArrayList<Obstaculo>();
    	
        for( int x = 0; x < obstaculos.length; x++ )
            for( int y = 0; y < obstaculos[x].length; y++ ){
                 if( obstaculos[x][y] != null )
               	  	as.add( obstaculos[x][y] );                  
            }    	
    	return as;
    }
    
    /** coloca um c�vil numa dada posi��o do cen�rio. N�O testa se a coloca��o � v�lida 
     * @param p onde colocar o civil
     * @param c civil a colocar
     */
    public void colocarCivil( Point p, Civil c ){
        if( !eCoordenadaValida( p ) )
            return;
        
        // se tem obst�culo coloca o civil l�
        Obstaculo az = obstaculos[p.x][p.y];
        if( az != null )
        	az.entrar( c );
        
        // colocar o civil
        civis[p.x][p.y] = c;        
        if( c == null ) return;
        c.setCenario( this );
        c.setPosicao( p );
    }
    
    /**
     * remove o civil presente na coordenada p 
     * @param p a coordenada de onde retirar o civil
     * @return o civil retirado
     */
    public Civil removerCivil( Point p ){
        if( !eCoordenadaValida( p ) )
            return null;
        
        Civil old = civis[p.x][p.y];
        if( old == null )
        	return null;
        // retirar o caixote do obst�culo, se estiver em algum
        Obstaculo az = obstaculos[p.x][p.y];
        if( az != null )
        	az.sair( old );
        
        civis[p.x][p.y] = null;
        return old;        
    }

    /** devolve o civil presente na coordenada indicada
     * @param p a coordenada onde est� o caixote pretendido
     * @return o civil presente na coordenada indicada
     */
    public Civil getCivil( Point pt ){
        if( !eCoordenadaValida( pt ) )
            return null;
        
        return civis[ pt.x ][ pt.y ];        
    }
    
    /** indica quantos civis est�o no cen�rio
     * @return o n�mero de civis presentes no cen�rio
     */
    public int getNumeroCivis(){
    	int n = 0;
        for( int x = 0; x < civis.length; x++ )
            for( int y = 0; y < civis[x].length; y++ ){
                 if( civis[x][y] != null )
               	   n++;                  
            }    	
    	return n;
    }
    
    /** devolve os civis presentes no cen�rio
     * @return os civis presentes no cen�rio
     */
    public java.util.List<Civil> getCivis(){
    	ArrayList<Civil> cxs = new ArrayList<Civil>();
    	
        for( int x = 0; x < civis.length; x++ )
            for( int y = 0; y < civis[x].length; y++ ){
                 if( civis[x][y] != null )
               	  	cxs.add( civis[x][y] );                  
            }    	
    	return cxs;
    }
    
    /** coloca o soldado numa dada posi��o do cen�rio. N�O testa se a coloca��o � v�lida
     * @param p onde colocar o soldado
     * @param s o soldado
     */
    public void colocarSoldado( Point p, Soldado s  ){
        if( !eCoordenadaValida( p ) || s == null ) 
            return;     
        
        // se tem obstaculo nessa posi��o, informa o obst�culo
        Obstaculo ob = obstaculos[p.x][p.y];
        if( ob != null )
       		ob.entrar( s );

        if( soldado != null )
        	removerSoldado( soldado.getPosicao() );
        
        soldado = s;
        s.setCenario( this );
        s.setPosicao( p );                
    }
    
    /**
     * remove o soldado presente numa dada coordenada 
     * @param p a coordenada de onde remover o soldado
     * @return o soldado retirado
     */
    public Soldado removerSoldado( Point p ){
        if( !eCoordenadaValida( p ) )
            return null;
        
        // se tem obstaculo nessa posi��o tira de l� o soldado
        Obstaculo ob = obstaculos[p.x][p.y];
        if( ob != null )
       		ob.sair( soldado );

        Soldado old = soldado;
        soldado = null;
        return old;        
    }
    
    /** devolve o soldado que opera no cen�rio
     * @return o soldado que opera no cen�rio
     */
    public Soldado getSoldado(){
    	return soldado;
    }
    
    /** Indica se uma dada coordenada est� ocupada.
     * Uma coordenada est� ocupada, se tiver pelo menos um civil, soldado ou obst�culo
     * @param p coordenada a verificar
     * @return true, se a coordenada tiver alguma coisa
     */
	public boolean estaOcupado(Point p) {
        if( !eCoordenadaValida( p ) )
            return true;
		return obstaculos[p.x][p.y] != null || civis[p.x][p.y]!= null || soldado.getPosicao().equals( p ); 
	}
	
	/** testa se tem pessoas (civis ou soldado) no caminho
	 * @param pos posi��o onde come�a  verificar (exclusive)
	 * @param direcao dire��o segundo a qual vai verificar o caminho 
	 * @return true, se encontrar uma pessoa no caminho
	 */
	public boolean temPessoas(Point pos, Vector2D direcao) {
		if( pos == null || (direcao.x==0 && direcao.y==0) )
			return false;
		// como vamos alterar a posi��o conv�m fazer um clone dela
		Point p = (Point)pos.clone();
		// passar para a primeira posi��o v�lida
		p.translate( (int)direcao.x,  (int)direcao.y );
		
		// enquanto estiver dentro do cen�rio
		while( eCoordenadaValida( p ) ) {
			// se vir um civil, tem pessoas
			if( getCivil( p ) != null ) 
				return true;
			
			// se estiver l� o soldado, tem pessoas
			if( soldado.getPosicao().equals( p ) )
				return true;
			
			// se tem um obst�culo n�o transparente, n�o v� mais nada
			Obstaculo o = getObstaculo( p );
			if( o != null && !o.eTransparente() ) {
				return false;
			}
			// passa para a pr�xima posi��o
			p.translate( (int)direcao.x,  (int)direcao.y );
		}
		return false;
	}
    
     /** limpa o cen�rio todo
     */
    public void limpar( ){
   	    for( int x = 0; x < 8; x++ )
   	         for( int y = 0; y < 8; y++ ){
   	              obstaculos[x][y] = null;
   	              civis[x][y] = null;
   	         }
   	    soldado = null;
    }

    /** define quantos civis se devem salvar neste cen�rio
     * @param numCivis o n�mero de civis a salvar
     */
	public void setNumCivis(int numCivis) {
		numeroRefugiados = numCivis;		
	}

	/** define a posi��o de onde saiem os civis
	 * @param pos a posi��o de onde saiem os civis
	 */
	public void setCivisInicio(Point pos) {
		saidaRefugiados = pos;		
	}

	/** Coloca mais um civil no cen�rio, se ainda houver e se a posi��o
	 * de saida estiver livre
	 */
	public void proximoCivil() {
		if( numeroRefugiados <= 0)
			return;
		if( civis[saidaRefugiados.x][saidaRefugiados.y] != null )
			return;
		
		try {
			Civil c = new Civil( new ComponenteMultiAnimado(new Point(), "art/civil" + ((numeroRefugiados%6)+1) + ".png", 2, 4, 4));
			colocarCivil( (Point)saidaRefugiados.clone(), c );
			numeroRefugiados--;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Indica que o cen�rio vai terminar, indicando se � por vit�ria ou derrota
	 * @param ganhou indica se ganhou ou perdeu o cen�rio
	 * @param animFim anima��o final
	 */
	public void iniciaFimNivel( boolean ganhou, ComponenteAnimado animFim ) {		
		this.ganhou = ganhou;
		acabou = true;
		animacaoFinal = animFim;
	}

	/** Indica se o cen�rio est� ganho e acabado
	 * Para estar acabado a anima��o final deve ter terminado
	 * @return true, se o cen�rio est� ganho
	 */
	public boolean ganhou() {		
		return acabou && ganhou && animacaoFinal.numCiclosFeitos() > 0;
	}

	/** Indica se o cen�rio est� perdido e acabado
	 * Para estar acabado a anima��o final deve ter terminado
	 * @return true, se o cen�rio est� perdido 
	 */
	public boolean perdeu() {		
		return acabou && !ganhou && animacaoFinal.numCiclosFeitos() > 0;
	}
	
	   /** Converte coordenadas do �cran para quadriculas do cen�rio
     * @param ecran coordenadas do �cran a converter
     * @return a quadricula respetiva do cen�rio, null se n�o for v�lida
     */
    public Point doEcranParaCenario( Point ecran ){
        // calcular em que casa se premiu
        int x = ((ecran.x - posicao.x) / dimQuadricula);
        int y = ((ecran.y - posicao.y) / dimQuadricula);

        // verificar se a casa est� dentro do cen�rio
        Point casa = new Point( x, y);
        if( !eCoordenadaValida( casa ) )
            return null;
        return casa;
    }

    /** converte coordenadas do cen�rio para o �cran
     * @param casa coordenada do cen�rio
     * @return a respetiva coordenada em pixeis (no meio da quadr�cula correspondente)
     */
    public Point getEcran( Point p ){
        int x = (p.x) * dimQuadricula + posicao.x + dimQuadricula/2;
        int y = (p.y) * dimQuadricula + posicao.y + dimQuadricula/2;
        return new Point(x,y);
    }
}
