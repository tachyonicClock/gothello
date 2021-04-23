package gothello.gothelloserver.rules;

import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import gothello.gothelloserver.rules.Rules.Stone;

public class Board {
    private Stone[][] board = new Stone[8][8];
    // public Set<Change> boardChanges = new TreeSet<>();

    public Stone get(Point p){
        return get(p.x, p.y);
    }

    public Stone get(int x, int y){
        return board[x][y];
    }
    
    public void set(int x, int y, Stone stone){
        // boardChanges.add(new Change(x, y, board[x][y], stone));
        board[x][y] = stone;
    }

    public void set(Point p, Stone stone){
        set(p.x, p.y, stone);
    }

    public Board() {
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
              board[x][y] = Stone.NONE;
            }
        }
    }

    // public class Change {
    //     final int x;
    //     final int y;
    //     final Stone before;
    //     final Stone after;

    //     public Change(int x, int y, Stone before, Stone after){
    //         this.x = x;
    //         this.y = y;
    //         this.before = before;
    //         this.after = after;
    //     }
    // }
}