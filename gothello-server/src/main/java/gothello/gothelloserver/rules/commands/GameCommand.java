package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.exceptions.IllegalMove;

public abstract class GameCommand {
    public abstract GameCommand makeMove(GothelloState game) throws IllegalMove;
    public abstract void unmakeMove(GothelloState game);

    /**
     * Attempt a move and make it only if it is legal
     * @param game The gothello game state to mutate
     * @return true if the move was legal
     */
    public boolean attemptMove(GothelloState game){
        try {
            makeMove(game);
        } catch (IllegalMove e) {
            return false;
        }
        return true;
    }
}
