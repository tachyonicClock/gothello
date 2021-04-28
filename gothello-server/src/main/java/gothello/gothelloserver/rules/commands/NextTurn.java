package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloState;

public class NextTurn extends GameMove {

    @Override
    public GameMove makeMove(GothelloState game) {
        game.activePlayer = game.otherPlayer(game.activePlayer);
        game.turnNumber ++;
        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.activePlayer = game.otherPlayer(game.activePlayer);
        game.turnNumber --;
    }

}
