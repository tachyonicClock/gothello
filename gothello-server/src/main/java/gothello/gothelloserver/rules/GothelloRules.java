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


  public GothelloRules(){
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
  public int getBoardSize() {
    return 8;
  }


  @Override
  public boolean isLegal(int x, int y, Stone player) {
    return game.isLegal(x, y, player);
  }


  @Override
  public boolean isGameOver() {
    return game.winner != Stone.NONE;
  }


  @Override
  public void pass(Stone player) throws IllegalMove {
    history.push(new PassTurn(player).makeMove(game));
  }


  @Override
  public void resign(Stone player) {
    history.push(new Resign(player).makeMove(game));
  }

  @Override
  public void playStone(int x, int y, Stone player) throws IllegalMove {
    history.push(new PlayStone(new Point(x,y), player).makeMove(game));
  }


  @Override
  public GameMove lastMove() {
    if (history.isEmpty()){
      return null;
    }
    return history.peek();
  }

}
