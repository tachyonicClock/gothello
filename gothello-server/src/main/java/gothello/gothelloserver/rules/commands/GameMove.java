package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.exceptions.IllegalMove;

public abstract class GameMove {
    public abstract GameMove makeMove(GothelloState game) throws IllegalMove;
    public abstract void unmakeMove(GothelloState game);

    public boolean isPass(){
        return false;
    }
}
