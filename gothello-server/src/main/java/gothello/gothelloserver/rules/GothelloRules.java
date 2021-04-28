package gothello.gothelloserver.rules;

import java.util.Stack;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.commands.GameMove;
import gothello.gothelloserver.rules.commands.PassTurn;
import gothello.gothelloserver.rules.commands.PlayStone;
import gothello.gothelloserver.rules.commands.Resign;

/**
 * GothelloRules is an implementation of rules that implements the rules for
 * Gothello
 */
public class GothelloRules implements Rules {

  private GothelloState game = new GothelloState();
  private Stack<GameMove> history = new Stack<>();

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
    return getTerritory(player) + getCaptures(player);
  }

  @Override
  public int getTerritory(Stone player) {
    return game.getTerritory(player);
  }

  @Override
  public int getCaptures(Stone player) {
    if (player == Stone.BLACK)
      return game.blackCaptures;
    if (player == Stone.WHITE)
      return game.whiteCaptures;
    return 0;
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

  private Stone getLeader(int blackScore, int whiteScore){
    if (blackScore == whiteScore)
      return Stone.DRAW;
    return blackScore > whiteScore? Stone.BLACK : Stone.WHITE;
  }

  @Override
  public void pass(Stone player) throws IllegalMove {
    // Two consecutive passes end the game
    history.push(new PassTurn(player).makeMove(game));
    int size = history.size();
    if (size >= 2 && history.get(size - 1).isPass() && history.get(size - 2).isPass()) {
      game.winner = getLeader(getScore(Stone.BLACK), getScore(Stone.WHITE));
    }
  }

  @Override
  public void resign(Stone player) {
    history.push(new Resign(player).makeMove(game));
  }

  @Override
  public void playStone(int x, int y, Stone player) throws IllegalMove {
    history.push(new PlayStone(new Point(x, y), player).makeMove(game));
  }

  @Override
  public boolean hasGameStarted() {
    return !history.isEmpty();
  }

  @Override
  public GameMove lastMove() {
    if (history.isEmpty()) {
      return null;
    }
    return history.peek();
  }

}
