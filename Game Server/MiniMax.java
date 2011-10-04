import java.util.HashSet;
import java.util.Scanner;


public class MiniMax extends Player {

	private static int MAX_DEPTH=6; 
	private Move bestMove=null; 
	
	public MiniMax(Scanner sc) {
		super(sc);
	}

	private int boardHeuristic(int turn){
		int targetR = -1;
		int targetC = -1;
		if (turn==1){
			targetR = 0;
			targetC = 12;
		}else{
			targetR = 16;
			targetC = 12;
		}
		
		HashSet<Point> positions = new HashSet<Point>(); 
		this.board.myPiecePositions(positions,turn);
		
		int sum =0; 
				
		 System.err.println("board heuristic"); 
		
		for(Point p: positions){
			System.err.println("board distance for piece:"+Board.dist(p.mx, p.my, targetR, targetC)); 
			sum +=Board.dist(p.mx, p.my, targetR, targetC); 
		}
		
		return (int)Math.floor(100000.0/sum); 
	}
	
	private Move alphaBetaSearch(Board b, int turn){
		int alpha = Integer.MIN_VALUE; 
		int beta  = Integer.MAX_VALUE; 
		
		int max = maxValue(this.board, alpha, beta, 0, turn);
		System.err.println("Minimax value:"+max); 
		b.move(bestMove);
		return bestMove; 
	}
	
	private Integer maxValue(Board b, int alpha, int beta, int depth, int myturn){
		if(b.checkWin(myturn) || depth ==MAX_DEPTH){
			System.err.println("should check"); 
			return boardHeuristic(myturn); 
		}
		int v = Integer.MIN_VALUE; 
		HashSet<Point> positions = new HashSet<Point>(); 
		b.myPiecePositions(positions,myturn);
		
		// iterate through all marbles and find all valid moves
		for(Point p : positions){
			HashSet<Integer> validMoves = new HashSet<Integer>(); 			
			b.legalMoves(p.mx, p.my, validMoves); 
			
			System.err.println("max and depth:"+depth); 
			
			for(Integer i : validMoves){
				
				if(b.validateSimpleMove(p.mx,p.my,i/25, i%25,-1,-1,myturn)){
				
					Move m = new Move(0,0,0, p.mx,p.my,i/25,i%25,-1,-1 ); 
					b.move(m);
					
					v = Math.max(v, minValue(b,alpha,beta,depth+1, myturn)); 
					if(v>= beta){
						Move un = new Move(0,0,0,i/25,i%25,p.mx,p.my,-1,-1); 
						b.move(un); 
						return v;
					}

					if(depth == 0 && v>=alpha){
						bestMove = m; 
					}
					Move un = new Move(0,0,0,i/25,i%25,p.mx,p.my,-1,-1); 
					b.move(un);
					alpha = Math.max(alpha,v);
				}
			}
			
		}
		return v;
		
	}
	
	private Integer minValue(Board b, int alpha, int beta, int depth, int myturn){
		if(b.checkWin(myturn) || depth ==MAX_DEPTH){
			return boardHeuristic(myturn); 
		}
		int v = Integer.MAX_VALUE; 
		HashSet<Point> positions = new HashSet<Point>(); 
		b.opponentsPiecePositions(positions, myturn);
		
		System.err.println("min and depth:"+depth); 

		
		// iterate through all marbles and find all valid moves
		for(Point p : positions){
			HashSet<Integer> validMoves = new HashSet<Integer>(); 			
			b.legalMoves(p.mx, p.my, validMoves); 
			
			
			for(Integer i : validMoves){
				if(b.validateSimpleMove(p.mx,p.my,i/25, i%25,-1,-1,3-myturn)){

					Move m = new Move(0,0,0, p.mx,p.my,i/25,i%25,-1,-1 ); 
					b.move(m);
					
					v = Math.min(v, maxValue(b,alpha,beta,depth+1, myturn)); 
					if(v <= alpha){
						Move un = new Move(0,0,0,i/25,i%25,p.mx,p.my,-1,-1); 
						b.move(un); 
						return v;
					}
					beta = Math.min(beta,v); 
					Move un = new Move(0,0,0,i/25,i%25,p.mx,p.my,-1,-1); 
					b.move(un);
				}


			}
			
		}
		return v; 
	}
	
	
	@Override
	public String think() {
		Move m =alphaBetaSearch(this.board, getMyturn());
		return m.r1+" "+m.c1+" "+m.r2+" "+m.c2 + " "+ m.r3+" "+ m.c3;
	}

	
	public static void main(String args[]){
		int turn = 1;
		MiniMax p = new MiniMax(new Scanner(System.in));
		while (true)
		{
			System.err.println("turn = "+turn+"   myturn = "+p.getMyturn());	
			if (turn == p.getMyturn()){
				System.err.println("It is my turn and I am thinking");	
				System.out.println(p.think());
				int status = p.getStatus();
				if(status<0){
					System.err.println("I lost:"+status);
					break;
				}
				else if(status>0){
					System.err.println("I won");
					break;
				}
			}
			else{
				int res = p.getOpponentMove();
				if(res == -1)
					System.err.println("The server is messed up");
				else if (res == 1){
					System.err.println("The other player won.");
					break;
				}
				else 
					System.err.println("OK lemme think...");	
					
			}
            System.err.println(p.getBoard().toString('*'));
			turn = 3-turn;
		}	
	}
}
