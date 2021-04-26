package gothello.gothelloserver.messages;

import gothello.gothelloserver.rules.Rules;
import gothello.gothelloserver.rules.Stone;

/**
 * GameState defines how the game state is serialized and sent to the players
 */
public class GameState extends Message {
  public final Boolean yourTurn;
  public final Stone yourStones;
  public final Stone turn;
  public final String[][] board;
  public final int turnNumber;
  public final LastMove lastMove;

  public GameState(Stone player, Rules rules) {
    super("state");
    turn = rules.getTurn();
    yourTurn = (turn == player);
    yourStones = player;
    turnNumber = rules.getTurnNumber();
    lastMove = new LastMove(rules.lastMove());

    // Populate board with state
    int n = rules.getBoardSize();
    board = new String[n][n];
    for (int x = 0; x < n; x++) {
      for (int y = 0; y < n; y++) {
        switch (rules.getSquare(x, y)) {
          case BLACK:
            board[y][x] = "B";
            break;
          case WHITE:
            board[y][x] = "W";
            break;
          case NONE:
            board[y][x] = rules.isLegal(x, y, player) ? "L" : "I";
            break;
          default:
            board[y][x] = "X";
            break;
        }
      }
    }
  }

}