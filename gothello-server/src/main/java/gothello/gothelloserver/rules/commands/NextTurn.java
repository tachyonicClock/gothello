package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloRules;

public class NextTurn implements GameMove {

    @Override
    public GameMove makeMove(GothelloRules game) {
        game.activePlayer = game.otherPlayer(game.activePlayer);
        game.turnNumber ++;
        return this;
    }

    @Override
    public void unmakeMove(GothelloRules game) {
        game.activePlayer = game.otherPlayer(game.activePlayer);
        game.turnNumber --;
    }

}
