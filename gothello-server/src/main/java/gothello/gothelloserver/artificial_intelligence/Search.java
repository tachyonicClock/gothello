package gothello.gothelloserver.artificial_intelligence;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gothello.gothelloserver.artificial_intelligence.TranspositionTable.TableEntry;
import gothello.gothelloserver.artificial_intelligence.TranspositionTable.TableEntry.ScoreType;
import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.ZobristHash;
import gothello.gothelloserver.rules.commands.GameCommand;
import gothello.gothelloserver.rules.commands.PassTurn;

public class Search {
    private final Logger log = LoggerFactory.getLogger(Search.class);

    private static final int INFINITY = Integer.MAX_VALUE;

    private ZobristHash zHash = ZobristHash.getInstance();


    private TranspositionTable transTable = new TranspositionTable();

    private Limit limit;

    private GothelloRules rules;
    private GothelloState state;
    private Stone maximizingPlayer;
    private PositionEval positionEval = PositionEval.getInstance();

    private int positionsEvaluated = 0;
    private int terminalPositionsReached = 0;
    private int transTableHits = 0;
    private int depthReached = 0;

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
            return a.compareTo(b) >= 0 ? a : b;
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
            return new ScoredMove(-100, move);
        }

    }

    private long zKey(){
        return zHash.hashWithPlayer(rules.getTurn(), (rules.lastMove() instanceof PassTurn), state.board);
    }

    /**
     * negamax search https://en.wikipedia.org/wiki/Negamax with transposition table
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
        
        int alphaOriginal = alpha;

        // Base case
        // return the heuristic value of the node from the players point of view
        if (rules.isGameOver()) {
            return scoreGameOver(move, player);
        } else if (depth <= 0) {
            return scoreSearchEnd(move, player);
        }

        // Check transposition cache. On a cache hit we can exit early
        TranspositionTable.TableEntry ttEntry;
        ttEntry = transTable.get(zKey());
        // We use the depth-preferred replacement strategy where transpositions
        // are overwritten if they happen at a shallower level
        if (ttEntry != null && ttEntry.depth >= depth)
        {
            transTableHits++;
            switch (ttEntry.scoreType) {
                case EXACT:
                    return new ScoredMove(ttEntry.score, ttEntry.bestMove);
                case LOWER_BOUND:
                    alpha = Math.max(alpha, ttEntry.score);
                    break;
                case UPPER_BOUND:
                    beta = Math.min(beta, ttEntry.score);
                    break;
            }
            if (alpha >= beta)
                return new ScoredMove(ttEntry.score, ttEntry.bestMove);
        }

        ScoredMove best = new ScoredMove(-INFINITY, new PassTurn(player));
        MoveIterator moves = new MoveIterator(player);
        while (moves.hasNext()) {
            move = moves.next();

            if (!move.attemptMove(state))
                continue;

            // Get the likely score if this move was taken
            ScoredMove child = negamax(move, depth - 1, -beta, -alpha, Stone.otherPlayer(player));
            state.undoMove();

            // Time for search must have expired
            if (child == null)
                return null;

            child.move = move;
            child.score = -child.score;

            // Update the best move
            best = ScoredMove.max(best, child);

            // Cutoff the branch if it is not possible to do better
            alpha = Math.max(alpha, best.score);
            if (alpha >= beta)
                break;
        }


        if (player != maximizingPlayer)
            return best;

        // Store as much information as we know. Since we do alpha/beta pruning
        // sometimes we may only know an upper bound
        if (alpha <= alphaOriginal)
            // Only the upper bound is known
            // https://www.chessprogramming.org/Node_Types#ALL
            ttEntry = new TableEntry(best.score, ScoreType.UPPER_BOUND, depth, best.move);
        if (alpha >= beta)
            // Only the lower bound is known
            // https://www.chessprogramming.org/Node_Types#CUT
            ttEntry = new TableEntry(best.score, ScoreType.LOWER_BOUND, depth, best.move);
        else
            // The score is exactly known for the position
            // https://www.chessprogramming.org/Node_Types#PV
            ttEntry = new TableEntry(best.score, ScoreType.EXACT, depth, best.move);
        transTable.add(zKey(), ttEntry);
        return best;
    }

    private ScoredMove startSearch(int depth){
        return negamax(null, depth, -INFINITY, INFINITY, maximizingPlayer);
    }

    public ScoredMove performUnlimitedSearch(int depth) {
        limit = Limit.noLimit;
        terminalPositionsReached = 0;
        positionsEvaluated = 0;
        return startSearch(depth);
    }

    public ScoredMove progressiveSearch(long duration){
        limit = new Limit.TimeLimit(duration);
        terminalPositionsReached = 0;
        positionsEvaluated = 0;

        ScoredMove result = startSearch(1);
        for (int depth = 2; depth < 50; depth++) {
            // Only set if it is not null
            result = Optional.ofNullable(startSearch(depth)).orElse(result);
            depthReached = depth;
            if (limit.isLimitReached()) {
                break;
            }
        }
        depthReached--;
        return result;
    }

    public int getPositionsEvaluated() {
        return positionsEvaluated;
    }

    public int getTerminalPositionsReached() {
        return terminalPositionsReached;
    }

    public int getCacheHits(){
        return transTableHits;
    }

    public int getDepthReached(){
        return depthReached;
    }

    public Search(GothelloRules rules, Stone player) {
        this.rules = rules;
        maximizingPlayer = player;
        state = rules.game;
    }

}
