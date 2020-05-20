package gothello.gothelloserver;
import java.util.concurrent.ThreadLocalRandom;


public class Game {
  enum GameType {
    PRIVATE,
    PUBLIC
  }
  private final long id;
  private GameType gameType;
  private int players;

  public Game(GameType gameType){
    id = ThreadLocalRandom.current().nextLong();
    this.gameType = gameType;
  }


  
}