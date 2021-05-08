package gothello.gothelloserver.artificial_intelligence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gothello.gothelloserver.artificial_intelligence.TranspositionTable.TableEntry;
import gothello.gothelloserver.artificial_intelligence.TranspositionTable.TableEntry.ScoreType;
import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.commands.GameCommand;
import gothello.gothelloserver.rules.commands.PassTurn;

/**
 * Search implements the MiniMax algorithm https://en.wikipedia.org/wiki/Minimax
 */
public class Search {
    private final Logger log = LoggerFactory.getLogger(Search.class);

    // Maximizing maxPlayer (the one we want to win) for the search we are
    // conducting
    private Stone maximizingPlayer;

    // Rules gives a higher level understanding of the game
    private GothelloRules rules;
    // Game state. Search should not lastingly effect the game state since all
    // its changes are done
    private GothelloState state;

    // Algorithm to evaluate a board position
    private PositionEval eval = new PositionEval();

    private TranspositionTable transTable = new TranspositionTable();

    // The path for the best moves
    private GameCommand moveList[];

    // Total number of positions searched
    public int positionsEvaluated = 0;

    // Total number of terminal positions reached in search
    public int terminalStatesReached = 0;

    public int transTableHits = 0;

    public Search(GothelloRules rules, Stone player) {
        this.rules = rules;
        maximizingPlayer = player;
        state = rules.game;
    }

    /**
     * negamax search https://en.wikipedia.org/wiki/Negamax
     * 
     * @param depth  The depth to search to in the game tree
     * @param alpha  The minimum score that the maximizing player is assured
     * @param beta   The maximum score that the minimizing player is assured
     * @param player The player currently being maximized
     * @return
     */
    private int negamax(int depth, int alpha, int beta, Stone player) {

        // Base case where the game has a winner
        if (rules.isGameOver()) {
            terminalStatesReached++;
            // The player won the game at this node
            if (rules.getWinner() == player)
                return Integer.MAX_VALUE;
            // The player lost the game at this node
            if (rules.getWinner() == Stone.otherPlayer(player))
                return Integer.MIN_VALUE;
            // The game was a draw at this node
            return 0;
        }
        // Base case where we go past our search depth
        else if (depth <= 0) {
            positionsEvaluated++;
            return eval.eval(state, player);
        }

        // Check transposition cache. On a cache hit we can exit early
        TranspositionTable.TableEntry ttEntry;
        ttEntry = transTable.get(state.board.zKey());
        // We use the depth-preferred replacement strategy where transpositions
        // are overwritten if they happen at a shallower level
        if (ttEntry != null && ttEntry.depth >= depth) {
            transTableHits++;
            switch (ttEntry.scoreType) {
                case EXACT:
                    return ttEntry.score;
                case LOWER_BOUND:
                    alpha = Math.max(alpha, ttEntry.score);
                    break;
                case UPPER_BOUND:
                    beta = Math.min(beta, ttEntry.score);
                    break;
            }
            if (alpha >= beta)
                return ttEntry.score;
        }

        int score = Integer.MIN_VALUE;
        int alphaOriginal = alpha;
        GameCommand bestMove = new PassTurn(player);

        MoveIterator moves = new MoveIterator(player);
        while (moves.hasNext()) {
            GameCommand move = moves.next();

            if (move.attemptMove(state)) {
                // Get the likely score if this move was taken
                int likelyScore = -negamax(depth - 1, -beta, -alpha, Stone.otherPlayer(player));
                move.unmakeMove(state);
                // Track the best move
                if (likelyScore >= score) {
                    score = likelyScore;
                    moveList[depth - 1] = move;
                    bestMove = move;
                }

                // Cutoff the branch if it is not possible to do better
                alpha = Math.max(alpha, score);
                if (alpha >= beta)
                    break;
            }
        }

        // Store as much information as we know. Since we do alpha/beta pruning
        // sometimes we may only know an upper bound
        if (alpha >= beta)
            // Only the lower bound is known
            // https://www.chessprogramming.org/Node_Types#CUT
            ttEntry = new TableEntry(score, ScoreType.LOWER_BOUND, depth, bestMove);
        if (alpha <= alphaOriginal)
            // Only the upper bound is known
            // https://www.chessprogramming.org/Node_Types#ALL
            ttEntry = new TableEntry(score, ScoreType.UPPER_BOUND, depth, bestMove);
        else
            // The score is exactly known for the position
            // https://www.chessprogramming.org/Node_Types#PV
            ttEntry = new TableEntry(score, ScoreType.EXACT, depth, bestMove);
        transTable.add(state.board.zKey(), ttEntry);

        return score;
    }

    public int performSearch(int depth) {
        positionsEvaluated = 0;
        terminalStatesReached = 0;
        transTableHits = 0;
        moveList = new GameCommand[depth];
        int value = negamax(depth, Integer.MIN_VALUE, Integer.MAX_VALUE, maximizingPlayer);

        for (int i = 0; i < moveList.length; i++) {
            log.info("{} {} {}", i, i % 2 == 0 ? "MAX" : "MIN", moveList[i]);
        }

        return value;
    }

    public GameCommand getBestMove() {
        return moveList[moveList.length - 1];
    }
}
