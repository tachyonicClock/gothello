package gothello.gothelloserver.rules;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.commands.GameCommand;
import gothello.gothelloserver.rules.commands.PassTurn;
import gothello.gothelloserver.rules.commands.PlayStone;
import gothello.gothelloserver.rules.commands.Resign;

/**
 * GothelloRules is an implementation of rules that implements the rules for
 * Gothello
 */
public class GothelloRules implements Rules {

  private GothelloState game = new GothelloState();

  public GothelloRules() {
  }

  @Override
  public Stone getSquare(int x, int y) {
    return game.board.get(new Point(x, y));
  }

  @Override
  public Stone getTurn() {
    return game.activePlayer;
  }

  @Override
  public int getTurnNumber() {
    return game.turnNumber;
  }

  @Override
  public Stone getWinner() {
    return game.winner;
  }

  @Override
  public int getScore(Stone player) {
    return game.getScore(player);
  }

  @Override
  public int getTerritory(Stone player) {
    return game.board.countStones(player);
  }

  @Override
  public int getCaptures(Stone player) {
    return game.getCaptures(player);
  }

  @Override
  public int getBoardSize() {
    return 8;
  }

  @Override
  public boolean isLegal(int x, int y, Stone player) {
    try {
      new PlayStone(new Point(x, y), player).makeMove(game).unmakeMove(game);
    } catch (IllegalMove e) {
      return false;
    }
    return true;
  }

  @Override
  public boolean isGameOver() {
    return game.winner != Stone.NONE;
  }

  @Override
  public void pass(Stone player) throws IllegalMove {
    game.makeMove(new PassTurn(player));
  }

  @Override
  public void resign(Stone player) throws IllegalMove {
    game.makeMove(new Resign(player));
  }

  @Override
  public void playStone(int x, int y, Stone player) throws IllegalMove {
    game.makeMove(new PlayStone(new Point(x, y), player));
  }

  @Override
  public boolean hasGameStarted() {
    return !game.history.isEmpty();
  }

  @Override
  public GameCommand lastMove() {
    if (game.history.isEmpty()) {
      return null;
    }
    return game.history.peek();
  }

}
