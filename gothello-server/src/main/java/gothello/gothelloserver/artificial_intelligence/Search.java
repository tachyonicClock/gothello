package gothello.gothelloserver.artificial_intelligence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.commands.GameCommand;

public class Search {
    private final Logger log = LoggerFactory.getLogger(Search.class);
    private GothelloRules rules;
    private GothelloState state;
    private Stone player;
    private PositionEval eval = new PositionEval();

    public GameCommand moveList[];
    public int positionsEvaluated = 0;
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
    private int maximize(int depth) {
        MoveIterator moves = new MoveIterator(rules.getTurn());
        int value = Integer.MIN_VALUE;

        // Try each child
        while (moves.hasNext()) {
            GameCommand move = moves.next();
            
            if (makeMoveIfLegal(move)) {
                
                // Get maximum value
                int mmValue = minimax(depth);
                if (mmValue >= value) {
                    value = mmValue;
                    // Set the best move as the one that leads to this state
                    moveList[depth] = move;
                }
                
                move.unmakeMove(state);
            }
        }
        return value;
    }

    // minimize picks the move with the minimum score
    private int minimize(int depth) {
        MoveIterator moves = new MoveIterator(rules.getTurn());
        int value = Integer.MAX_VALUE;

        // Try each child
        while (moves.hasNext()) {
            GameCommand move = moves.next();

            if (makeMoveIfLegal(move)) {
                int mmValue = minimax(depth);
                if(mmValue <= value){
                    value = mmValue;
                    moveList[depth] = move;
                }
                move.unmakeMove(state);
            }
        }

        return value;
    }

    private int minimax(int depth) {
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
            return maximize(depth - 1);
        } else {
            return minimize(depth - 1);
        }
    }

    public int performSearch(int depth){
        positionsEvaluated = 0;
        terminalStatesReached = 0;
        moveList = new GameCommand[depth];
        int value = minimax(depth);

        for (int i = 0; i < moveList.length; i++) {
            log.info("{} {} {}", i,  i % 2 == 0? "MAX":"MIN", moveList[i]);
            
        }

        return value;
    }

    public GameCommand getBestMove(){
        return moveList[moveList.length - 1];
    }
}
