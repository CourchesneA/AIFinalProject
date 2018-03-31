package student_player;

import boardgame.Move;
import student_player.MCSTree.Node;
import tablut.TablutBoardState;
import tablut.TablutPlayer;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260688650");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public Move chooseMove(TablutBoardState boardState) {
    	long startTime = System.currentTimeMillis();
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...
        //MyTools.getSomething();

        // Is random the best you can do?
        Move myMove = boardState.getRandomMove();
        System.out.println(">>board state turn: "+boardState.getTurnPlayer());
        
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        MCSTree tree = new MCSTree(boardState.getTurnPlayer(), boardState);
        
        //TODO what is the lookahead dafuq
        while(System.currentTimeMillis()-startTime < 1750) {
        	System.out.println(">>ITERATION");
        	//1. Select
        	Node bestNode = tree.select();
        	myMove = bestNode.move;
        	//2. Expend
        	tree.expand(bestNode);
        	//3. Simulate and (4.) update
        	tree.simulateLight(bestNode.children);
        }
        
        System.out.println(">>Given move turn: "+myMove.getPlayerID());
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        // Return your move to be processed by the server.
        return myMove;
    }
}