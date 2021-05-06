package gothello.gothelloserver.rules;

public class Board {
    public final static ZobristHash zHash = ZobristHash.getInstance();
    public final static int width = 8;
    public final static int height = 8;

    private Stone[][] board = new Stone[width][height];

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

    // Is the point in the othello quad
    public boolean inOthelloQuad(Point p){
        return (p.x < width / 2 && p.y < height / 2) ||
               (p.x >= width / 2 && p.y >= height / 2);
    }

    // Is the point in the go quad
    public boolean inGoQuad(Point p){
        return !inOthelloQuad(p);
    }

    // Is the point on the board
    public boolean inBounds(Point p) {
        return (p.x >= 0 && p.x < width) && (p.y >= 0 && p.y < height);
    }

    // Count the number of a particular player's stones on the board
    public int countStones(Stone player){
        int stones = 0;
        // For each square on the board
        for (int x = 0; x < width; x++) {
          for (int y = 0; y < height; y++) {
            if (get(x, y) == player) 
                stones++;
          }
        }
        return stones;
    }
    
    public void reset(){
        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board[x].length; y++) {
              board[x][y] = Stone.NONE;
            }
        }
        set(1, 1, Stone.WHITE);
        set(2, 2, Stone.WHITE);
        set(2, 1, Stone.BLACK);
        set(1, 2, Stone.BLACK);
        set(6, 5, Stone.WHITE);
        set(5, 6, Stone.WHITE);
        set(5, 5, Stone.BLACK);
        set(6, 6, Stone.BLACK);
    }

    public long zKey() {
        return zHash.hash(this);
    }

    public Board() {
        reset();
    }

    public Board(Board board) {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                set(x, y, board.get(x, y));
            }
        }
    }
}
