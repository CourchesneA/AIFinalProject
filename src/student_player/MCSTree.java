package student_player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import boardgame.Move;
import tablut.TablutBoardState;
import tablut.TablutMove;

/**
 * All function should never modify the the given reference of the board state -> they should clone if before doing anything
 * @author Anthony Courchesne
 *
 */
public class MCSTree {
	public Node root;

	public MCSTree(int turn, TablutBoardState state) {
		root = new Node(null,state);
	}




	/**
	 * Use UCT to find the best node
	 * @return
	 */
	public Node select() {
		Node ans = root;
		while(!ans.children.isEmpty()) { //While we have not reached a leaf, find the best children
			ans = getBestChildUCT(ans.children);
		}
		return ans;
	}

	/**
	 * Expend a node in the tree with all its possible states after one move
	 * @param node
	 */
	public void expand(Node node) {
		//Get list of possible move/state from current state
		for(Move m : node.state.getAllLegalMoves()) {	//TODO maybe we can optimize here, not add all the moves
			TablutBoardState newState = (TablutBoardState) node.state.clone();
			newState.processMove((TablutMove) m);
			Node newNode = new Node(node, newState );
			newNode.move = m;
			node.children.add(newNode);
		}
	}

	/**
	 * Select a random node from the list, simulate randomly until the end and update the tree's statistics
	 * @NOTE : Does not work. Since game ends in 100 turns, will most likely not yield good results
	 * @param nodes
	 */
	public void simulateLight(List<Node> nodes) { //Simulation using random plays
		//Get a random node from the children nodes
		Node node = nodes.get((new Random()).nextInt(nodes.size()));
		TablutBoardState simState = (TablutBoardState) node.state.clone();
		while(!simState.gameOver()) {
			simState.processMove((TablutMove) simState.getRandomMove());
		}
		int winner = simState.getWinner();

		//Update / Backpropagate
		update(node, winner);

	}

	public void simulateHeavy(List<Node> nodes) { //Simulation using improved greedy
		//Get a random node from the children nodes
		Node node = nodes.get((new Random()).nextInt(nodes.size()));
		TablutBoardState simState = (TablutBoardState) node.state.clone();
		while(!simState.gameOver()) {
			simState.processMove((TablutMove) MyTools.chooseGreedyMoveFast(simState));
			//simState.processMove((TablutMove) MyTools.chooseGreedyMove(simState));
			//simState.processMove((TablutMove) MyTools.chooseGreedyMoveSlow(simState));
		}
		int winner = simState.getWinner();

		//Update / Backpropagate
		update(node, winner);
	}

	public static void update(Node node, int winner) {
		if(node == null) return;
		node.simulationCount++;							//Update the simulation count
		if(node.getTurn()==winner)node.winCount++;		//If this player won, update its wincount
		update(node.parent,winner);
	}

	public Node getBestChildUCT(ArrayList<Node> nodes) {
		return Collections.max(nodes,new NodeComparator());
	}

	private static class NodeComparator implements Comparator<Node> {
		public int compare(Node a, Node b) {
			if (a.UCTvalue() > b.UCTvalue())
				return -1; // highest value first
			return 1;
		}
	}
	
	static class NodeScoreComparator implements Comparator<Node>{ //TODO we actually want the wincout/simcount
		public int compare(Node a, Node b) {
			if( a.winCount > b.winCount)
				return -1;
			if(a.winCount < b.winCount) 
				return 1;
			return 0;
		}
	}

	public static class Node {
		int simulationCount;	//Denominator
		int winCount;	//Numerator
		Node parent;
		ArrayList<Node> children;
		Move move; //Move from the parent, i.e. was applied to node.parent and resulted in this.state
		public TablutBoardState state;

		public Node(Node parent, TablutBoardState state) {
			this.parent = parent;
			this.state = state;
			this.children = new ArrayList<Node>();
		}

		public double UCTvalue() {
			assert(parent != null);
			double simCount = simulationCount == 0 ? 0.000000001 : simulationCount;	//Catch division by zero
			double val = ((double)winCount / simCount) + Math.sqrt(2)*Math.sqrt(Math.log(parent.simulationCount)/simCount);
			return val;
		}

		public int getTurn() {
			return state.getTurnPlayer();
		}
		
	}
}
