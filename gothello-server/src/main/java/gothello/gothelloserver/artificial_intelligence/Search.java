package gothello.gothelloserver.artificial_intelligence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.commands.GameCommand;

/**
 * Search implements the MiniMax algorithm
 * https://en.wikipedia.org/wiki/Minimax
 */
public class Search {
    private final Logger log = LoggerFactory.getLogger(Search.class);

    // Maximizing player (the one we want to win) for the search we are conducting
    private Stone player;

    // Rules gives a higher level understanding of the game
    private GothelloRules rules;
    // Game state. Search should not lastingly effect the game state since all
    // its changes are done
    private GothelloState state;

    // Algorithm to evaluate a board position
    private PositionEval eval = new PositionEval();

    // The path for the best moves
    private GameCommand moveList[];

    // Total number of positions searched
    public int positionsEvaluated = 0;

    // Total number of terminal positions reached in search
    public int terminalStatesReached = 0;

    public Search(GothelloRules rules, Stone player) {
        this.rules = rules;
        this.player = player;
        state = rules.game;
    }

    private boolean makeMoveIfLegal(GameCommand move) {
        try {
            move.makeMove(state);
        } catch (IllegalMove e) {
            return false;
        }
        return true;
    }

    // maximize picks the move with the maximum score
    private int maximize(int depth, int alpha, int beta) {
        MoveIterator moves = new MoveIterator(rules.getTurn());
        int maxScore = Integer.MIN_VALUE;

        // Try each child
        while (moves.hasNext()) {
            GameCommand move = moves.next();
            
            if (makeMoveIfLegal(move)) {
                // Get the likely score if this move was taken
                int likelyScore = minimax(depth, alpha, beta);
                move.unmakeMove(state);
                // Track the best move
                if (likelyScore >= maxScore) {
                    maxScore = likelyScore;
                    moveList[depth] = move;
                }
                
                // Cutoff the branch if it is not possible to do better
                alpha = Math.max(alpha, maxScore);
                if(alpha >= beta)
                    break;
            }
        }
        return maxScore;
    }

    // minimize picks the move with the minimum score
    private int minimize(int depth, int alpha, int beta) {
        MoveIterator moves = new MoveIterator(rules.getTurn());
        int minScore = Integer.MAX_VALUE;

        // Try each child
        while (moves.hasNext()) {
            GameCommand move = moves.next();

            if (makeMoveIfLegal(move)) {
                // Get the likely score if this move was taken
                int likelyScore = minimax(depth, alpha, beta);
                move.unmakeMove(state);
                // Tack the worst possible move as a likely move by the minimizer player
                if(likelyScore <= minScore){
                    minScore = likelyScore;
                    moveList[depth] = move;
                }

                // Cutoff the branch if it is not possible to do worse
                beta = Math.min(beta, minScore);
                if (beta <= alpha)
                    break;
            }
        }

        return minScore;
    }

    private int minimax(int depth, int alpha, int beta) {
        // Base case where the game has a winner
        if (rules.isGameOver()) {
            terminalStatesReached++;

            // Maximizing player won the game
            if (rules.getWinner() == player)
                return Integer.MAX_VALUE;
            // Minimizing player one the game
            if (rules.getWinner() == Stone.otherPlayer(player))
                return Integer.MIN_VALUE;

            // The game was a draw
            return 0;
        }
        // Base case where we go past our search depth
        else if (depth <= 0) {
            positionsEvaluated++;
            return eval.eval(state, player);
        }

        // Recursive cases
        if (player == rules.getTurn()) {
            return maximize(depth - 1, alpha, beta);
        } else {
            return minimize(depth - 1, alpha, beta);
        }
    }

    
    public int performSearch(int depth){
        positionsEvaluated = 0;
        terminalStatesReached = 0;
        moveList = new GameCommand[depth];
        int value = minimax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE);

        for (int i = 0; i < moveList.length; i++) {
            log.info("{} {} {}", i,  i % 2 == 0? "MAX":"MIN", moveList[i]);
        }

        return value;
    }

    public GameCommand getBestMove(){
        return moveList[moveList.length - 1];
    }
}
