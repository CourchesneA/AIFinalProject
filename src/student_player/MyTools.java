package student_player;

import java.util.List;
import java.util.Random;

import boardgame.Move;
import coordinates.Coord;
import coordinates.Coordinates;
import tablut.TablutBoardState;
import tablut.TablutMove;

public class MyTools {
    public static double getSomething() {
        return Math.random();
    }
    
    /**
     * This is the code provided for the greedy choice, recopied for use for simulations
     * @param bs
     * @return
     */
    public static Move  chooseGreedyMove(TablutBoardState bs) {
    	Random rand = new Random();
        List<TablutMove> options = bs.getAllLegalMoves();

        // Set an initial move as some random one.
        TablutMove bestMove = options.get(rand.nextInt(options.size()));

        // This greedy player seeks to capture as many opponents as possible.
        int opponent = bs.getOpponent();
        int minNumberOfOpponentPieces = bs.getNumberPlayerPieces(opponent);

        // Iterate over move options and evaluate them.
        for (TablutMove move : options) {
            // To evaluate a move, clone the boardState so that we can do modifications on
            // it.
            TablutBoardState cloneBS = (TablutBoardState) bs.clone();

            // Process that move, as if we actually made it happen.
            cloneBS.processMove(move);

            // Check how many opponent pieces there are now, maybe we captured some!
            int newNumberOfOpponentPieces = cloneBS.getNumberPlayerPieces(opponent);

            // If this move caused some capturing to happen, then do it! Greedy!
            if (newNumberOfOpponentPieces < minNumberOfOpponentPieces) {
                bestMove = move;
                minNumberOfOpponentPieces = newNumberOfOpponentPieces;
            }

            /*
             * If we also want to check if the move would cause us to win, this works for
             * both! This will check if black can capture the king, and will also check if
             * white can move to a corner, since if either of these things happen then a
             * winner will be set.
             */
            if (cloneBS.getWinner() == bs.getTurnNumber()) {
                bestMove = move;
                break;
            }
        }
        return bestMove;
    }
    
    /**
     * This is a week version of the provided algorithm, as soon as it find a move where it capture a piece, it will do it
     * @param bs
     * @return
     */
    public static Move chooseGreedyMoveFast(TablutBoardState bs) {
    	Random rand = new Random();
        List<TablutMove> options = bs.getAllLegalMoves();

        // Set an initial move as some random one.
        TablutMove bestMove = options.get(rand.nextInt(options.size()));

        // This greedy player seeks to capture as many opponents as possible.
        int opponent = bs.getOpponent();
        int minNumberOfOpponentPieces = bs.getNumberPlayerPieces(opponent);

        // Iterate over move options and evaluate them.
        for (TablutMove move : options) {
            // To evaluate a move, clone the boardState so that we can do modifications on
            // it.
            TablutBoardState cloneBS = (TablutBoardState) bs.clone();

            // Process that move, as if we actually made it happen.
            cloneBS.processMove(move);

            // Check how many opponent pieces there are now, maybe we captured some!
            int newNumberOfOpponentPieces = cloneBS.getNumberPlayerPieces(opponent);

            // If this move caused some capturing to happen, then do it! Greedy!
            if (newNumberOfOpponentPieces < minNumberOfOpponentPieces) {
                return move;
            }
        }
        return bestMove;
    }
    
    /**
     * Greedy with lookahead, i.e. Do not make a move if the enemy could win the following turn
     * @param bs
     * @return
     */
    public static Move chooseGreedyMoveSlow(TablutBoardState bs) {
    	Random rand = new Random();
        List<TablutMove> options = bs.getAllLegalMoves();

        // Set an initial move as some random one.
        TablutMove bestMove = options.get(rand.nextInt(options.size()));

        // This greedy player seeks to capture as many opponents as possible.
        int opponent = bs.getOpponent();
        int numberOfOpponentPieces = bs.getNumberPlayerPieces(opponent);

        // Iterate over move options and evaluate them.
        for (TablutMove move : options) {
            // To evaluate a move, clone the boardState so that we can do modifications on
            // it.
            TablutBoardState cloneBS = (TablutBoardState) bs.clone();

            // Process that move, as if we actually made it happen.
            cloneBS.processMove(move);

            // Check how many opponent pieces there are now, maybe we captured some!
            int newNumberOfOpponentPieces = cloneBS.getNumberPlayerPieces(opponent);

            /*
             * If the move makes us win do it no matter what
            */
            if (cloneBS.getWinner() == bs.getTurnNumber()) {
                return move;
            }
            
            //As soon as we have found a move, check if we can lose the next turn by doing it. If not, choose that move
            if (newNumberOfOpponentPieces < numberOfOpponentPieces) {
            	boolean validMove = true;
            	if(cloneBS.getTurnNumber() > 2) {// Assume we dont have to check for a lose next turn if its too early in the game 
            		for(TablutMove m2 : cloneBS.getAllLegalMoves()) {
                		TablutBoardState cloneBS2 = (TablutBoardState) cloneBS.clone();
                		cloneBS2.processMove(m2);
                		if(cloneBS2.getWinner() != bs.getTurnNumber()) {
                			validMove = false;
                			break;
                		}
                	}
            	}
            	
            	if(validMove) {
                    return move;
            	}
            }

        }
        return bestMove;
    }
}
