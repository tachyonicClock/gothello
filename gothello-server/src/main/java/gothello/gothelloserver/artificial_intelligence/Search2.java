package gothello.gothelloserver.artificial_intelligence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.commands.GameCommand;
import gothello.gothelloserver.rules.commands.PassTurn;

public class Search2 {
    private final Logger log = LoggerFactory.getLogger(Search.class);

    private static final int INFINITY = Integer.MAX_VALUE;

    private Limit limit;

    private GothelloRules rules;
    private GothelloState state;
    private Stone maximizingPlayer;
    private PositionEval positionEval = new PositionEval();

    private int positionsEvaluated = 0;
    private int terminalPositionsReached = 0;

    public static class ScoredMove implements Comparable<ScoredMove> {
        public int score;
        public GameCommand move;

        public ScoredMove(int score, GameCommand move) {
            this.score = score;
            this.move = move;
        }

        @Override
        public int compareTo(ScoredMove otherTuple) {
            return Integer.compare(this.score, otherTuple.score);
        }

        public static ScoredMove max(ScoredMove a, ScoredMove b) {
            return a.compareTo(b) > 0 ? a : b;
        }

        @Override
        public String toString() {

            return String.format("[score:%d] '%s'", score, move);
        }
    }

    private static class Limit {
        public static final Limit noLimit = new Limit();

        public static class TimeLimit extends Limit{
            public final long deadline;
            public TimeLimit(long duration){
                this.deadline = System.currentTimeMillis() + duration;
            }
            public boolean isLimitReached() {
                return System.currentTimeMillis() >= deadline;
            }
        }

        public boolean isLimitReached() {
            return false;
        }
    }


    private ScoredMove scoreSearchEnd(GameCommand move, Stone player) {
        positionsEvaluated++;
        return new ScoredMove(positionEval.eval(state, player), move);
    }

    private ScoredMove scoreGameOver(GameCommand move, Stone player) {
        terminalPositionsReached++;
        if (rules.getWinner() == player) {
            // The player won the game at this node
            return new ScoredMove(INFINITY, move);
        } else if (rules.getWinner() == Stone.otherPlayer(player)) {
            // The player lost the game at this node
            return new ScoredMove(-INFINITY, move);
        } else {
            // The game was a draw at this node
            return new ScoredMove(0, move);
        }

    }

    /**
     * negamax search https://en.wikipedia.org/wiki/Negamax
     * 
     * @param move   The move to evaluate
     * @param depth  The depth to search to in the game tree
     * @param alpha  The minimum score that the maximizing player is assured
     * @param beta   The maximum score that the minimizing player is assured
     * @param player The player currently being maximized
     * @return A score and a move to (maybe) reach the score
     */
    private ScoredMove negamax(GameCommand move, int depth, int alpha, int beta, Stone player) {
        // Has a limit been reached that would mean we should terminate our search
        // typically a time limit
        if (limit.isLimitReached())
            return null;

        // Base case
        // return color × the heuristic value of node
        if (rules.isGameOver()) {
            return scoreGameOver(move, player);
        } else if (depth <= 0) {
            return scoreSearchEnd(move, player);
        }

        ScoredMove best = new ScoredMove(-INFINITY, new PassTurn(player));
        MoveIterator moves = new MoveIterator(player);
        while (moves.hasNext()) {
            move = moves.next();

            if (!move.attemptMove(state))
                continue;

            // Get the likely score if this move was taken
            ScoredMove child = negamax(move, depth - 1, -beta, -alpha, Stone.otherPlayer(player));
            child.move = move;
            child.score = -child.score;

            // Update the best move
            best = ScoredMove.max(best, child);
            state.undoMove();

            // Cutoff the branch if it is not possible to do better
            alpha = Math.max(alpha, best.score);
            if (alpha >= beta)
                break;
        }

        return best;
    }

    public ScoredMove performUnlimitedSearch(int depth) {
        limit = Limit.noLimit;
        terminalPositionsReached = 0;
        positionsEvaluated = 0;
        return negamax(new PassTurn(maximizingPlayer), depth, -INFINITY, INFINITY, maximizingPlayer);
    }

    public int getPositionsEvaluated() {
        return positionsEvaluated;
    }

    public int getTerminalPositionsReached() {
        return terminalPositionsReached;
    }

    public Search2(GothelloRules rules, Stone player) {
        this.rules = rules;
        maximizingPlayer = player;
        state = rules.game;
    }

}
