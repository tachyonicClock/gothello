package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.exceptions.IllegalMove;

public abstract class GameCommand {
    public abstract GameCommand makeMove(GothelloState game) throws IllegalMove;
    public abstract void unmakeMove(GothelloState game);
}
