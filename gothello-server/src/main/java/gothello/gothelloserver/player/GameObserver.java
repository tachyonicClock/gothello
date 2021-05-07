package gothello.gothelloserver.player;

import gothello.gothelloserver.messages.Message;

public abstract class GameObserver {
    public abstract void update();
    public abstract void sendMessage(Message message);
}
