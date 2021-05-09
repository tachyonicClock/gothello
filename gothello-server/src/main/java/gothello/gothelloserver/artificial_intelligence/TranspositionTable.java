package gothello.gothelloserver.artificial_intelligence;

import java.util.HashMap;
import gothello.gothelloserver.rules.commands.GameCommand;

/**
 * Implementation of https://www.chessprogramming.org/Transposition_Table
 * for gothello
 */
public class TranspositionTable {
    public static class TableEntry {
        enum ScoreType {
            EXACT,
            LOWER_BOUND,
            UPPER_BOUND
        }

        public final int depth;
        public final int score;
        public final ScoreType scoreType;
        public final GameCommand bestMove;


        /**
         * Construct a new table entry for the transposition table
         * @param score The score achieved at the node. May be a upper/lower bound or exact
         * @param scoreType What information do we have about the score
         * @param bestMove The best move at the position
         */
        public TableEntry(int score, ScoreType scoreType, int depth, GameCommand bestMove){
            this.depth = depth;
            this.score = score;
            this.scoreType = scoreType;
            this.bestMove = bestMove;
        }
    }

    private HashMap<Long, TableEntry> table = new HashMap<>();

    public void add(long zKey, TableEntry tableEntry){
        table.put(zKey, tableEntry);
    }

    public TableEntry get(long zKey){
        return table.get(zKey);
    }
    
}
