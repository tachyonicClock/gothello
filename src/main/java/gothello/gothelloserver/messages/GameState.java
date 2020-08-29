package gothello.gothelloserver.messages;

import gothello.gothelloserver.rules.Rules;

/**
 * GameState defines how the game state is serialized and sent to the players
 */
public class GameState extends Message {
  public final Boolean yourTurn;
  public final Rules.Stone yourStones;
  public final Rules.Stone turn;
  public final String[][] board;
  public final int turnNumber;

  public GameState(Rules.Stone player, Rules rules) {
    super("state");
    turn = rules.getTurn();
    yourTurn = (turn == player);
    yourStones = player;
    turnNumber = rules.getTurnNumber();

    // Populate board with state
    int n = rules.getBoardSize();
    board = new String[n][n];
    for (int x = 0; x < n; x++) {
      for (int y = 0; y < n; y++) {
        switch (rules.getSquare(x, y)) {
          case BLACK:
            board[x][y] = "B";
            break;
          case WHITE:
            board[x][y] = "W";
            break;
          case NONE:
            board[x][y] = rules.isLegal(x, y, player) ? "L" : "I";
            break;
          default:
            board[x][y] = "X";
            break;
        }
      }
    }
  }

}