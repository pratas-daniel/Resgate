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
 * Classe que representa o cenário onde irá decorrer o jogo
 */
public class Cenario {

	// constantes com o comprimento e altura
	// mas podiam ser personalizáveis
	private static final int COMPRIMENTO = 25;
	private static final int ALTURA = 16;
	
    private ComponenteVisual fundo;  // figura associada ao fundo do cenário
    private int dimQuadricula;        // a dimensão de cada quadricula
    private Point posicao;            // posição da imagem 
	private Soldado soldado;          // o soldado controlado pelo jogador

	/** o array de obstáculos */
	private Obstaculo obstaculos[][] = new Obstaculo[COMPRIMENTO][ALTURA];

	/** o array de civis */
	private Civil civis[][] = new Civil[COMPRIMENTO][ALTURA];
	
	/** o número de refugiados neste cenário */
	private int numeroRefugiados;
	private Point saidaRefugiados;  // o ponto de onde os refugiados saiem
	
	// variáveis para controlar o fim de nível
	private boolean ganhou = false, acabou = false;
	private ComponenteAnimado animacaoFinal;
	
	/**
	 * Construtor do cenário
	 * @param fig a figura do fundo 
	 * @param t a coordenada no écran
	 * @param dimQuadricula a dimensão de cada quadrícula
	 */
    public Cenario( ComponenteVisual fig, Point t, int dimQuadricula ){
    	this.dimQuadricula = dimQuadricula;
    	posicao = t;
    	fundo = fig;
    	if( fundo != null )
    		fundo.setPosicao( t );
     }
    
    /** Construtor do Cenário
	 * @param t a coordenada do cenário
	 * @param dimAzul a dimensão de cada azulejo 
     */
    public Cenario( Point t, int dimAzul ){
    	this( null, t, dimAzul );
    }
    
    /** devolve a altura em quadrículas
     * @return a altura em quadrículas
     */
    public int getAltura(){
    	return ALTURA;
    }

    /** devolve o comprimento em quadrículas
     * @return o comprimento em quadrículas
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
    
    /** desenha todos os elementos no cenário
     * @param g o ambiente gráfico onde desenhar
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

   /** indica se a coordenada é valida 
    * @param p a coordenada a verificar
    * @return true, se a coordenada é valida 
    */
    public boolean eCoordenadaValida( Point p ) {
        return p != null && p.x>=0 && p.x < obstaculos.length && p.y>=0 && p.y< obstaculos[0].length;
    }

    /** coloca um obstáculo no cenário. 
     * @param p posição onde colocar o obstáculo
     * @param o obstáculo a colocar
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
     * remove o obstáculo da coordenada indicada   
     * @param p coordenda de onde retirar o obstáculo
     * @return o obstáculo retirado
     */
    public Obstaculo removerObstaculo( Point p ){
        if( !eCoordenadaValida( p ) )
            return null;
            
        Obstaculo old = obstaculos[p.x][p.y];
        obstaculos[p.x][p.y] = null;
        // se se remove o azulejo também se tem de remover o caixote, e o operário
        civis[p.x][p.y] = null; 
        if( soldado != null && soldado.getPosicao().equals( p ) )
        	soldado = null;
        return old;        
    }
    
    /** move um obstáculo de posição
     * @param o obstáculo a ser removido
     * @param dest destino para onde se vai mover
     * @return true, se moveu o obstáculo
     */
    public boolean moverObstaculo( Obstaculo  o, Point dest ){
        if( o== null || o.getCenario() != this || !eCoordenadaValida( dest ) )
            return false;
    	
        obstaculos[o.getPosicao().x][o.getPosicao().y] = null;
        obstaculos[dest.x][dest.y] = o;
        o.setPosicao( dest );
        return true;
    }
    
    /** devolve o obstáculo presente na coordenada indicada
     * @param p a coordenada onde está o obstáculo pretendido
     * @return o azulejo presente na coordenada indicada
     */
    public Obstaculo getObstaculo( Point p ){
        if( !eCoordenadaValida( p ) )
            return null;
        
        return obstaculos[ p.x ][ p.y ];        
    }
    
    /** devolve os obstáculos presentes no cenário
     * @return os obstáculos presentes no cenário
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
    
    /** coloca um cívil numa dada posição do cenário. NÃO testa se a colocação é válida 
     * @param p onde colocar o civil
     * @param c civil a colocar
     */
    public void colocarCivil( Point p, Civil c ){
        if( !eCoordenadaValida( p ) )
            return;
        
        // se tem obstáculo coloca o civil lá
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
        // retirar o caixote do obstáculo, se estiver em algum
        Obstaculo az = obstaculos[p.x][p.y];
        if( az != null )
        	az.sair( old );
        
        civis[p.x][p.y] = null;
        return old;        
    }

    /** devolve o civil presente na coordenada indicada
     * @param p a coordenada onde está o caixote pretendido
     * @return o civil presente na coordenada indicada
     */
    public Civil getCivil( Point pt ){
        if( !eCoordenadaValida( pt ) )
            return null;
        
        return civis[ pt.x ][ pt.y ];        
    }
    
    /** indica quantos civis estão no cenário
     * @return o número de civis presentes no cenário
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
    
    /** devolve os civis presentes no cenário
     * @return os civis presentes no cenário
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
    
    /** coloca o soldado numa dada posição do cenário. NÃO testa se a colocação é válida
     * @param p onde colocar o soldado
     * @param s o soldado
     */
    public void colocarSoldado( Point p, Soldado s  ){
        if( !eCoordenadaValida( p ) || s == null ) 
            return;     
        
        // se tem obstaculo nessa posição, informa o obstáculo
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
        
        // se tem obstaculo nessa posição tira de lá o soldado
        Obstaculo ob = obstaculos[p.x][p.y];
        if( ob != null )
       		ob.sair( soldado );

        Soldado old = soldado;
        soldado = null;
        return old;        
    }
    
    /** devolve o soldado que opera no cenário
     * @return o soldado que opera no cenário
     */
    public Soldado getSoldado(){
    	return soldado;
    }
    
    /** Indica se uma dada coordenada está ocupada.
     * Uma coordenada está ocupada, se tiver pelo menos um civil, soldado ou obstáculo
     * @param p coordenada a verificar
     * @return true, se a coordenada tiver alguma coisa
     */
	public boolean estaOcupado(Point p) {
        if( !eCoordenadaValida( p ) )
            return true;
		return obstaculos[p.x][p.y] != null || civis[p.x][p.y]!= null || soldado.getPosicao().equals( p ); 
	}
	
	/** testa se tem pessoas (civis ou soldado) no caminho
	 * @param pos posição onde começa  verificar (exclusive)
	 * @param direcao direção segundo a qual vai verificar o caminho 
	 * @return true, se encontrar uma pessoa no caminho
	 */
	public boolean temPessoas(Point pos, Vector2D direcao) {
		if( pos == null || (direcao.x==0 && direcao.y==0) )
			return false;
		// como vamos alterar a posição convém fazer um clone dela
		Point p = (Point)pos.clone();
		// passar para a primeira posição válida
		p.translate( (int)direcao.x,  (int)direcao.y );
		
		// enquanto estiver dentro do cenário
		while( eCoordenadaValida( p ) ) {
			// se vir um civil, tem pessoas
			if( getCivil( p ) != null ) 
				return true;
			
			// se estiver lá o soldado, tem pessoas
			if( soldado.getPosicao().equals( p ) )
				return true;
			
			// se tem um obstáculo não transparente, não vê mais nada
			Obstaculo o = getObstaculo( p );
			if( o != null && !o.eTransparente() ) {
				return false;
			}
			// passa para a próxima posição
			p.translate( (int)direcao.x,  (int)direcao.y );
		}
		return false;
	}
    
     /** limpa o cenário todo
     */
    public void limpar( ){
   	    for( int x = 0; x < 8; x++ )
   	         for( int y = 0; y < 8; y++ ){
   	              obstaculos[x][y] = null;
   	              civis[x][y] = null;
   	         }
   	    soldado = null;
    }

    /** define quantos civis se devem salvar neste cenário
     * @param numCivis o número de civis a salvar
     */
	public void setNumCivis(int numCivis) {
		numeroRefugiados = numCivis;		
	}

	/** define a posição de onde saiem os civis
	 * @param pos a posição de onde saiem os civis
	 */
	public void setCivisInicio(Point pos) {
		saidaRefugiados = pos;		
	}

	/** Coloca mais um civil no cenário, se ainda houver e se a posição
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

	/** Indica que o cenário vai terminar, indicando se é por vitória ou derrota
	 * @param ganhou indica se ganhou ou perdeu o cenário
	 * @param animFim animação final
	 */
	public void iniciaFimNivel( boolean ganhou, ComponenteAnimado animFim ) {		
		this.ganhou = ganhou;
		acabou = true;
		animacaoFinal = animFim;
	}

	/** Indica se o cenário está ganho e acabado
	 * Para estar acabado a animação final deve ter terminado
	 * @return true, se o cenário está ganho
	 */
	public boolean ganhou() {		
		return acabou && ganhou && animacaoFinal.numCiclosFeitos() > 0;
	}

	/** Indica se o cenário está perdido e acabado
	 * Para estar acabado a animação final deve ter terminado
	 * @return true, se o cenário está perdido 
	 */
	public boolean perdeu() {		
		return acabou && !ganhou && animacaoFinal.numCiclosFeitos() > 0;
	}
	
	   /** Converte coordenadas do écran para quadriculas do cenário
     * @param ecran coordenadas do écran a converter
     * @return a quadricula respetiva do cenário, null se não for válida
     */
    public Point doEcranParaCenario( Point ecran ){
        // calcular em que casa se premiu
        int x = ((ecran.x - posicao.x) / dimQuadricula);
        int y = ((ecran.y - posicao.y) / dimQuadricula);

        // verificar se a casa está dentro do cenário
        Point casa = new Point( x, y);
        if( !eCoordenadaValida( casa ) )
            return null;
        return casa;
    }

    /** converte coordenadas do cenário para o écran
     * @param casa coordenada do cenário
     * @return a respetiva coordenada em pixeis (no meio da quadrícula correspondente)
     */
    public Point getEcran( Point p ){
        int x = (p.x) * dimQuadricula + posicao.x + dimQuadricula/2;
        int y = (p.y) * dimQuadricula + posicao.y + dimQuadricula/2;
        return new Point(x,y);
    }
}
