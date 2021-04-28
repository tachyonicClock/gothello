package gothello.gothelloserver.player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import gothello.gothelloserver.Game;
import gothello.gothelloserver.Util;
import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.messages.GameOver;
import gothello.gothelloserver.messages.GameState;
import gothello.gothelloserver.messages.Message;
import gothello.gothelloserver.messages.PlayStone;
import gothello.gothelloserver.messages.ShowStatus;
import gothello.gothelloserver.rules.Stone;

public class SocketPlayer extends GameObserver {
    private final Logger log = LoggerFactory.getLogger(SocketPlayer.class);
    private final WebSocketSession session;
    private Stone player = Stone.NONE;

    public void sendMessage(Message message) {
        synchronized(session){
            try {
                Util.JSONMessage(session, message);
            } catch (Exception e) {
                log.error("Unable to send message through a web-socket", e);
            }
        }
    }

    @Override
    public void update() {
        if (!game.rules.isGameOver()) {
            sendMessage(new GameState(player, game.rules));
        } else {
            sendMessage(new GameOver(player, game.rules));
        }
    }

    public void handleWebSocketDisconnection(CloseStatus status) {
        game.detach(this);
        game.leaveGame(player);
    }

    // handleWebSocketMessage is called whenever a new message is sent to the
    // server over the websocket
    public void handleWebSocketMessage(TextMessage message) throws Exception {
        String json = message.getPayload();
        String messageType = Util.getMessageType(json);

        // Keep alive does not prompt any response from the server
        if (messageType.equals("keepAlive"))
            return;

        // Spectators should not be able to do anything
        if (!player.isPlayable()) {
            sendMessage(new ShowStatus(ShowStatus.Variant.WARNING, "Spectators may only watch"));
        }

        // Decide how to handle a game action
        try {
            switch (messageType) {
            case "playStone":
                PlayStone playStone = Util.<PlayStone>parseMessage(json, PlayStone.class);
                playStone.makePlay(player, game.rules);
                break;
            case "pass":
                game.rules.pass(player);
                break;
            case "resign":
                game.rules.resign(player);
                break;
            default:
                throw new Exception("Unexpected message type: " + messageType);
            }

            // Update other players
            game.notifyAllObservers();
        } catch (IllegalMove e) {
            sendMessage(new ShowStatus(ShowStatus.Variant.INFO, e.getMessage()));
        }
    }

    public SocketPlayer(Game game, WebSocketSession session) {
        this.session = session;
        this.game = game;
        this.player = game.joinGame();
        game.attach(this);
    }

    @Override
    public String toString() {
        return String.format("SocketPlayer %s", this.player.toString());
    }

}
