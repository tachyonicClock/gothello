package gothello.gothelloserver.game;

import gothello.gothelloserver.Game;
import gothello.gothelloserver.messages.Message;

public abstract class GameObserver {
    protected Game game;
    public abstract void update();
    public abstract void sendMessage(Message message);
}
