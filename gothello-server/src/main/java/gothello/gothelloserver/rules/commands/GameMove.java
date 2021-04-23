package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.exceptions.IllegalMove;

public interface GameMove {

    GameMove makeMove(GothelloRules game) throws IllegalMove;
    void unmakeMove(GothelloRules game);
}
