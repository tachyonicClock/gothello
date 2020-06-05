package gothello.gothelloserver;

import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import gothello.gothelloserver.messages.*;
import gothello.gothelloserver.rules.*;

/**
 * Game represents a game match between two players. The game class is
 * responsible for both being the Games Resource representation (that is the
 * class that is transformed into JSON) and the handler for web-sockets.
 */
public class Game extends Message {
	private final Logger log = LoggerFactory.getLogger(Game.class);

	// The game id is used to refer to the Game
	public final int id;

	// rules is an interface allowing us to easily change the rule set
	private final Rules rules = new GothelloRules();

	/**
	 * A Game can be considered PUBLIC or PRIVATE. If it is PUBLIC then you can get
	 * the id through the api. Otherwise the host must share the game URL.
	 */
	public final GameType gameType;

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

	/**
	 * WebSocketSessions are maintained for the entire course of a game. They let
	 * the server and clients communicate effectively. If it is null that means the
	 * player has not joined the game yet
	 */
	private WebSocketSession black = null;
	private WebSocketSession white = null;

	// getGameFull returns whether or not both players are in the game
	private boolean getGameFull() {
		return (black != null && white != null);
	}

	// getOpen returns whether or not someone can join the game
	public boolean getOpen() {
		if (rules.isGameOver()) return false;
		return (!getGameFull() && gameType == GameType.PUBLIC);
	}

	/**
	 * Game is also responsible for managing the handlers for the web-sockets that
	 * the clients use to communicate with the server over.
	 */

	// handleWebSocketMessage is called whenever a new message is sent to the
	// server over the websocket
	public void handleWebSocketMessage(WebSocketSession session, TextMessage message) throws Exception {
		Rules.Stone player = getPlayer(session);
		String json = message.getPayload();

		// Decide how to handle the message
		switch (Util.getMessageType(json)) {
			case "error":
				ErrorMessage errorMsg = Util.<ErrorMessage>parseMessage(json, ErrorMessage.class);
				log.error("[{}] {} - encountered an error, '{}'", id, player, errorMsg.errorMessage);
				break;

			case "playStone":
				log.info("[{}] {} - played a stone", id, player);
				PlayStone playStone = Util.<PlayStone>parseMessage(json, PlayStone.class);
				playStone.makePlay(player, rules);
				break;
			case "pass":
				rules.pass(player);
				break;
			case "resign":
				rules.resign(player);
			default:
				break;
		}
		updateClientState();
	}

	// handleWebSocketConnection is called when a new client connects for the
	// first time
	public void handleWebSocketConnection(WebSocketSession session) throws Exception {
		if (black == null) {
			black = session;
			log.info("[{}] BLACK - joined the game", id);
		} else if (white == null) {
			white = session;
			log.info("[{}] WHITE - joined the game", id);
		} else {
			log.warn("[{}] Game is full, kicking player", id);
			Util.JSONMessage(session, new ErrorMessage("game is full, please join another game"));
			session.close();
			return;
		}
		if (getGameFull()) {
			log.info("[{}] Starting game", id);
			updateClientState();
		}
	}

	// handleWebSocketDisconnection is called when a client disconnects.
	public void handleWebSocketDisconnection(WebSocketSession session, CloseStatus status) throws Exception {
		Rules.Stone player = getPlayer(session);
		log.info("[{}] {} - left the game", id, player);
		switch (player) {
			case WHITE:
				white = null;
				break;
			case BLACK:
				black = null;
				break;
			default:
				break;
		}
	}

	// Internal Methods

	// updateClientState sends a unique game state object to each player
	private void updateClientState() throws Exception {
		Util.JSONMessage(black, new GameState(Rules.Stone.BLACK, rules));
		Util.JSONMessage(white, new GameState(Rules.Stone.WHITE, rules));

		if (rules.isGameOver()) {
			closeGame();
		}
	}

	private void closeGame() throws Exception{
		Util.JSONMessage(black, new GameOver(Rules.Stone.BLACK, rules));
		Util.JSONMessage(white, new GameOver(Rules.Stone.WHITE, rules));
		black.close();
		white.close();
		App.allGames.remove(id);
		log.info("[{}] closing the game", id);

	}

	// getPlayer compares the session ids to identify a message sender
	private Rules.Stone getPlayer(WebSocketSession session) {
		if (black != null && black.getId().equals(session.getId()))
			return Rules.Stone.BLACK;
		if (white != null && white.getId().equals(session.getId()))
			return Rules.Stone.WHITE;
		return Rules.Stone.NONE;
	}

	// GameState returns the game state for usage by an HTTP endpoint
	public GameState gameState(){
		return new GameState(rules.getTurn(), rules);
	}

	Game(final GameType gameType) {
		super("game");
		id = Math.abs(ThreadLocalRandom.current().nextInt());
		this.gameType = gameType;
	}
}