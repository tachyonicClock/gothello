package gothello.gothelloserver.rules;

import java.util.Random;

// Singleton used to generate a ZobristHash. This is a hash optimized for hashing
// a game's board state https://en.wikipedia.org/wiki/Zobrist_hashing
public class ZobristHash {

    private static ZobristHash instance = null;
    public static ZobristHash getInstance(){
        if (instance == null) {
            instance = new ZobristHash();
        }
        return instance;
    }
    
    private long[][] table = new long[64][2]; 
    private ZobristHash(){
        // Randomly fill table
        Random rand = new Random();
        for (int i = 0; i < 64; i++) {
            for (int j = 0; j < 2; j++) {
                table[i][j] = rand.nextLong();
            }
        }
    }
    
    public long hash(Board board){
        long hashCode = 0;
        for (int i = 0; i < 64; i++) {
            Stone piece = board.get(i % Board.width, i / Board.height);
            if (!piece.isPlayable())
                continue;
            int j = (piece == Stone.BLACK)? 0 : 1;
            hashCode = hashCode ^ table[i][j]; 
        }
        return hashCode;
    }
}
