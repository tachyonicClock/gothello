package gothello.gothelloserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import gothello.gothelloserver.messages.GameState;
import gothello.gothelloserver.messages.Message;
import gothello.gothelloserver.messages.ShowStatus;
import gothello.gothelloserver.player.GameObserver;
import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.rules.Rules;
import gothello.gothelloserver.rules.Stone;

import org.slf4j.Logger;

public class Game extends Message {
	private final Logger log = LoggerFactory.getLogger(Game.class);
	private List<GameObserver> observers = new ArrayList<>();
	
	enum GameType {
		PRIVATE, PUBLIC, SINGLE_PLAYER, INVALID;
		
		public static GameType fromString(String gameType) {
			switch (gameType.toLowerCase()) {
				case "public":
				return GameType.PUBLIC;
				case "private":
				return GameType.PRIVATE;
				case "single_player":
				return GameType.SINGLE_PLAYER;
			}
			return GameType.INVALID;
		}
	}
	
	@JsonIgnore
	public final Rules rules = new GothelloRules();

	// The game id is used to refer to the Game
	public final int id;

	// Is black/white in the game
	public boolean whiteInGame = false;
	public boolean blackInGame = false;

	/**
	 * A Game can be considered PUBLIC or PRIVATE. If it is PUBLIC then you can get
	 * the id through the api. Otherwise the host must share the game URL.
	 */
	public final GameType gameType;

	// whether or not both players are in the game
	public boolean getGameFull() {
		return whiteInGame && blackInGame;
	}

	private boolean gameStarted = false;

	// getOpen returns whether or not someone can join the game
	public boolean getOpen() {
		if (rules.isGameOver())
			return false;
		return (gameType == GameType.PUBLIC && !gameStarted && !getGameFull());
	}

	public void leaveGame(Stone player) {
		messageAllObservers(new ShowStatus(ShowStatus.Variant.SUCCESS,
				StringUtils.capitalize(String.format("%s left the game", player.toString()))));

		if (player == Stone.BLACK) {
			blackInGame = false;
		} else if (player == Stone.WHITE) {
			whiteInGame = false;
		}
	}

	private void start() {
		log.info("[{}] game {}", id, gameStarted ? "restarted" : "started");
		if (!gameStarted) {
			notifyAllObservers();
		}
		gameStarted = true;
	}

	public Stone joinGame() {
		Stone player = Stone.SPECTATOR;
		if (!blackInGame) {
			blackInGame = true;
			player = Stone.BLACK;
		} else if (!whiteInGame) {
			whiteInGame = true;
			player = Stone.WHITE;
		}

		messageAllObservers(new ShowStatus(ShowStatus.Variant.SUCCESS,
				StringUtils.capitalize(String.format("%s has joined", player.toString()))));
		log.info("[{}] {} joined", id, player.toString());

		if (getGameFull()) {
			MatchMaker.removeFromQueue(id);
			start();
		}
		return player;
	}

	public void attach(GameObserver observer) {
		log.info("[{}] {} - Joined", id, observer.toString());
		observers.add(observer);
		Collections.sort(observers);
		if (gameStarted) {
			observer.update();
		}
	}

	public void detach(GameObserver observer) {
		log.info("[{}] {} - Left", id, observer.toString());
		observers.remove(observer);

		if (observers.isEmpty()) {
			MatchMaker.deleteGame(id);
		}
	}

	public void notifyAllObservers() {
		for (GameObserver observer : observers) {
			observer.update();
		}
	}

	public void messageAllObservers(Message message) {
		for (GameObserver observer : observers) {
			observer.sendMessage(message);
		}
	}

	public GameState gameState() {
		return new GameState(Stone.SPECTATOR, rules);
	}

	public Game(GameType gameType) {
		super("game");
		this.id = Math.abs(ThreadLocalRandom.current().nextInt());
		this.gameType = gameType;
	}
}
