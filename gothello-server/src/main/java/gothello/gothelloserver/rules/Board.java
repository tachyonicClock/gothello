package gothello.gothelloserver.rules;

import gothello.gothelloserver.rules.Stone;

public class Board {
    private Stone[][] board = new Stone[8][8];

    public Stone get(Point p){
        return get(p.x, p.y);
    }

    public Stone get(int x, int y){
        return board[x][y];
    }
    
    public void set(int x, int y, Stone stone){
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
}
