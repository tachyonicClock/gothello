package gothello.gothelloserver;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.slf4j.LoggerFactory;

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
	Logger log = LoggerFactory.getLogger(HTTPHandler.class);
	ObjectMapper objectMapper = new ObjectMapper();

	// getGameId gets the Game id from the path
	public static int getGameId(WebSocketSession session) throws Exception {
		try {
			return Integer.parseInt(session.getUri().getPath().split("/")[4]);
		} catch (Exception e) {
			throw new Exception("Path variable 'id' not found");
		}
	}

	// getGame finds the game related to an id
	public static Game getGame(int id) throws Exception {
		Game game = App.allGames.get(id);
		if (game == null) {
			throw new Exception("Game not found");
		}
		return game;
	}

	// handleTextMessage gets the correct Game and calls its handler for messages
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		try {
			getGame(getGameId(session)).handleWebSocketMessage(session, message);
		} catch (Exception e) {
			session.sendMessage(Util.JSONMessage(new ErrorResponse(e.getMessage())));
			log.error(e.getMessage());
		}
	}

	// afterConnectionEstablished gets the correct Game and calls its handler for
	// new connections
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		try {
			getGame(getGameId(session)).handleWebSocketConnection(session);
		} catch (Exception e) {
			session.sendMessage(Util.JSONMessage(new ErrorResponse(e.getMessage())));
			log.error(e.getMessage());
		}
	}

	// afterConnectionClosed gets the correct Game and calls its handler for 
	// connection closes
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		try {
			getGame(getGameId(session)).handleWebSocketDisconnection(session, status);
		} catch (Exception e) {
			log.error("Error after connection closed, " + e.getMessage());
		}
	}

}