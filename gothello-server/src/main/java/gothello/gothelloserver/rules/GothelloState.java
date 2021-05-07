package gothello.gothelloserver.rules;

import java.util.Stack;
import java.util.TreeSet;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.commands.GameCommand;

/**
 * GothelloRules is an implementation of rules that implements the rules for
 * Gothello
 */
public class GothelloState {
  // Internal representation of the board
  public Board board = new Board();
  public Stone activePlayer = Stone.BLACK;
  public Stone winner = Stone.NONE;
  public int turnNumber = 0;
  public int whiteCaptures = 0;
  public int blackCaptures = 0;

  // Stack allowing traversal of the game state
  public Stack<GameCommand> history = new Stack<>();

  // Previous states is a set of previous states. This is used to implement the
  // ko-rule
  public TreeSet<Long> previousStates = new TreeSet<>();

  public void commitMoveToHistory(GameCommand move) throws IllegalMove {
    history.push(move.makeMove(this));
  }

  public boolean isTurn(Stone player) {
    return activePlayer == player;
  }

  public int getCaptures(Stone player) {
    if (player == Stone.BLACK)
      return blackCaptures;
    if (player == Stone.WHITE)
      return whiteCaptures;
    return 0;
  }

  public int getScore(Stone player) {
    return board.countStones(player) + getCaptures(player);
  }

}
