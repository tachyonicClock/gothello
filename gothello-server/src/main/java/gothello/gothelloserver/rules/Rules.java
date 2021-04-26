package gothello.gothelloserver.rules;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.commands.GameMove;

public interface Rules {
  //
  // Get Game State
  //

  // getSquare returns the stone at (x,y) on the board
  Stone getSquare(int x, int y);

  GameMove lastMove();

  // getTurn returns the player who's turn it is
  Stone getTurn();

  // getTurnNumber returns the number of turns that have been taken by both sides
  int getTurnNumber();

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

  // hasGameStarted checks weather at least one move has been made
  boolean hasGameStarted();

  //
  // Change Game State
  //

  // pass skips a player's turn
  void pass(Stone player) throws IllegalMove;

  // resign forfeits the game
  void resign(Stone player) throws IllegalMove;

  // playStone places a stone at (x,y)
  void playStone(int x, int y, Stone player) throws IllegalMove;
}