package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.GothelloState;

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
            game.playMove(this);
        } catch (IllegalMove e) {
            return false;
        }
        return true;
    }
}
