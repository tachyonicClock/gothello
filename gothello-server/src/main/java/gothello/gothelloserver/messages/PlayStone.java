package gothello.gothelloserver.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import gothello.gothelloserver.rules.Rules;
import gothello.gothelloserver.rules.Stone;

public class PlayStone extends Message {
  public final int row;
  public final int col;

  PlayStone(@JsonProperty("messageType") String messageType, @JsonProperty("row") int row,
      @JsonProperty("col") int col) {
    super(messageType);
    this.row = row;
    this.col = col;
  }

  public void makePlay(Stone player, Rules rules) throws Exception {
    rules.playStone(col, row, player);
  }
}