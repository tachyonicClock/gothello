package gothello.gothelloserver;

import java.util.concurrent.ThreadLocalRandom;

// Game is the resource representation for games
public class Game extends Response {
	enum GameType {
		PRIVATE, PUBLIC
	}

	// typeFromString returns the GameType given a string. Ignores capitalization
	public static GameType typeFromString(final String str) {
		switch (str.toLowerCase()) {
			case "public":
				return GameType.PUBLIC;
			case "private":
				return GameType.PRIVATE;
		}
		return GameType.PUBLIC;
	}

	private final int id; // The games unique id
	private final GameType gameType; // Is the game public or private
	private int players; // How many players are in the game

	Game(final GameType gameType) {
		id = Math.abs(ThreadLocalRandom.current().nextInt());
		this.gameType = gameType;
	}

	public long getId() {
		return id;
	}

	// getOpen returns whether or not someone can join the game
	public boolean getOpen() {
		return (players < 2 && gameType == GameType.PUBLIC);
	}

	public GameType getGameType() {
		return gameType;
	}

	@Override
	public String getType() {
		return "game";
	}

}