package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.exceptions.IllegalMove;

public interface GameMove {
    GameMove makeMove(GothelloState game) throws IllegalMove;
    void unmakeMove(GothelloState game);
}
