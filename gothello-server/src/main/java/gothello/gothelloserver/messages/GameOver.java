package gothello.gothelloserver.messages;

import gothello.gothelloserver.rules.Rules;
import gothello.gothelloserver.rules.Stone;

/**
 * GameOver is the message sent at the end of the game signaling that the game
 * is over.
 */
public class GameOver extends Message {
  public final Stone winner;
  public final Scores scores;
  public final Boolean isWinner;
  public final Boolean isLoser;
  public final Boolean isDraw;

  public class Scores {
    public final int black;
    public final int white;

    Scores(int black, int white) {
      this.black = black;
      this.white = white;
    }
  }

  public GameOver(Stone player, Rules rules) {
    super("gameOver");
    winner = rules.getWinner();
    scores = new Scores(rules.getScore(Stone.BLACK), rules.getScore(Stone.WHITE));
    isWinner = (winner == player);
    isDraw = (winner == Stone.DRAW);
    isLoser = (!isWinner && !isDraw && player != Stone.SPECTATOR);
  }
}