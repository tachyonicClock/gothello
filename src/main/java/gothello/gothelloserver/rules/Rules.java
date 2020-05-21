package gothello.gothelloserver.rules;

public interface Rules {
  enum Stone {
    BLACK,
    WHITE,
    NONE
  }

  // 
  // Get Game State 
  // 

  // getSquare returns the stone at (x,y) on the board
  Stone getSquare(int x, int y);

  // getTurn returns the player who's turn it is
  Stone getTurn();

  // getWinner returns the player who has won or Stone.NONE
  Stone getWinner();

  // getScore returns the score of the specified player
  int getScore(Stone player);

  // getBoardSize returns the size of a square board
  int getBoardSize();

  // isLegal returns true or false depending on if the square is a legal move
  // for the specified player
  boolean isLegal(int x, int y, Stone player);

  // isGameOver returns true if the game is finished
  boolean isGameOver();

  // 
  // Change Game State
  // 

  // pass skips a player's turn
  void pass(Stone player);

  // resign forfeits the game
  void resign(Stone player);

  // playStone places a stone at (x,y)
  boolean playStone(int x, int y, Stone player);
}