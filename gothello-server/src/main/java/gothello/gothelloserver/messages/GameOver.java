package gothello.gothelloserver.messages;

import gothello.gothelloserver.rules.Rules;

/**
 * GameOver is the message sent at the end of the game signaling that the game
 * is over.
 */
public class GameOver extends Message {
  public final Rules.Stone winner;
  public final Scores scores;
  public final Boolean isWinner;

  public class Scores {
    public final int black;
    public final int white;

    Scores(int black, int white) {
      this.black = black;
      this.white = white;
    }
  }

  public GameOver(Rules.Stone player, Rules rules) {
    super("gameOver");
    winner = rules.getWinner();
    scores = new Scores(rules.getScore(Rules.Stone.BLACK), rules.getScore(Rules.Stone.WHITE));
    isWinner = (winner == player);
  }
}