package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Stone;

public class NextTurn extends GameCommand {

    @Override
    public GameCommand makeMove(GothelloState game) {
        game.activePlayer = Stone.otherPlayer(game.activePlayer);
        game.turnNumber ++;
        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.activePlayer = Stone.otherPlayer(game.activePlayer);
        game.turnNumber --;
    }

}
