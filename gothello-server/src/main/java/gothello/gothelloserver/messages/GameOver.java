package gothello.gothelloserver.messages;

import gothello.gothelloserver.rules.Rules;
import gothello.gothelloserver.rules.Stone;

/**
 * GameOver is the message sent at the end of the game signaling that the game
 * is over.
 */
public class GameOver extends Message {
  public final Stone winner;
  public final Score scores;
  public final Boolean isWinner;
  public final Boolean isLoser;
  public final Boolean isDraw;

  public GameOver(Stone player, Rules rules) {
    super("gameOver");
    winner = rules.getWinner();
    scores = new Score(player, rules);
    isWinner = (winner == player);
    isDraw = (winner == Stone.DRAW);
    isLoser = (!isWinner && !isDraw && player != Stone.SPECTATOR);
  }
}