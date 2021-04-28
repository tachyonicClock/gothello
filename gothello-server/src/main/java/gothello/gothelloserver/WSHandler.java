package gothello.gothelloserver;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import gothello.gothelloserver.messages.ErrorMessage;
import gothello.gothelloserver.player.SocketPlayer;
import gothello.gothelloserver.exceptions.GameNotFound;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;

/**
 * WSHandler handles the web-socket messages/connections/disconnections and
 * sends control of the web-socket to the appropriate Game object based off of
 * the path variable
 */
public class WSHandler extends TextWebSocketHandler {
	Logger log = LoggerFactory.getLogger(WSHandler.class);
	Map<WebSocketSession, SocketPlayer> activePlayers = new ConcurrentHashMap<>();

	ObjectMapper objectMapper = new ObjectMapper();

	// getGameId gets the Game id from the path
	public static int getGameId(WebSocketSession session) throws IllegalArgumentException {
		try {
			return Integer.parseInt(session.getUri().getPath().split("/")[4]);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Path variable 'id' not found");
		}
	}

	public SocketPlayer getPlayer(WebSocketSession session){
		SocketPlayer player = activePlayers.get(session);
		if (player != null) {
			return player;
		}

		try {
			Game game = MatchMaker.getGame(getGameId(session));
			activePlayers.put(session, new SocketPlayer(game, session));
		} catch (IllegalArgumentException | GameNotFound e) {

			// Attempt to report error to the user
			try {
				Util.JSONMessage(session, new ErrorMessage(e.getMessage()));
			} catch (IOException e1) {
				log.error("Failed to send error message", e1);
			}

			log.warn(e.getMessage());
		}
		return player;
	}

	// handleTextMessage gets the correct Game and calls its handler for messages
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		getPlayer(session).handleWebSocketMessage(message);
	}

	// afterConnectionEstablished gets the correct Game and calls its handler for
	// new connections
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		getPlayer(session);
	}

	// afterConnectionClosed gets the correct Game and calls its handler for
	// connection closes
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// getPlayer(session);
		SocketPlayer player = activePlayers.get(session);
		if (player == null) return;
		activePlayers.remove(session);
		player.handleWebSocketDisconnection(status);
	}

}