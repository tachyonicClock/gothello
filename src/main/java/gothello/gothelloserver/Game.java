package gothello.gothelloserver;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Game represents a game match between two players. The game class is
 * responsible for both being the Games Resource representation (that is the
 * class that is transformed into JSON) and the handler for web-sockets.
 */
public class Game extends Response {
	// The game id is used to refer to the Game
	public final int id;

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

	// getOpen returns whether or not someone can join the game
	public boolean getOpen() {
		return (black != null && white != null && gameType == GameType.PUBLIC);
	}

	// This is a response of type "game" this communicates what sort of message
	// it is to the client
	@Override
	public String getType() {
		return "game";
	}

	/**
	 * Game is also responsible for managing the handlers for the websockets that
	 * the clients use to communicate with the server over.
	 */

	// handleWebSocketMessage is called whenever a new message is sent to the
	// server over the websocket
	public void handleWebSocketMessage(WebSocketSession session, TextMessage message) throws Exception {
		System.out.println("\nWS New Message\n");
		session.sendMessage(message);
	}

	// handleWebSocketConnection is called when a new client connects for the
	// first time
	public void handleWebSocketConnection(WebSocketSession session) throws Exception {
		System.out.println("\nWS New Connection\n");
	}

	// handleWebSocketDisconnection is called when a client disconnects.
	public void handleWebSocketDisconnection(WebSocketSession session, CloseStatus status) throws Exception {
		System.out.println("\nWS Disconnection\n");
	}

	Game(final GameType gameType) {
		id = Math.abs(ThreadLocalRandom.current().nextInt());
		this.gameType = gameType;
	}
}