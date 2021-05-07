package gothello.gothelloserver.player;

import gothello.gothelloserver.messages.Message;

public abstract class GameObserver implements Comparable<GameObserver>  {
    public abstract void update();
    public abstract void sendMessage(Message message);

    // To make the application appear more responsive some observers such as
    // real players should be updated first. Thus, we assign observers priorities
    public int observerPriority(){
        return 0;
    }

    @Override
    public int compareTo(GameObserver other){
        return this.observerPriority() - other.observerPriority();
    }

}
