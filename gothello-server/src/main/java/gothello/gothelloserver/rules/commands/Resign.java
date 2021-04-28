package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.GothelloState;

public class Resign extends GameMove {

    Stone player;

    public Resign(Stone player) {
        this.player = player;
    }

    @Override
    public GameMove makeMove(GothelloState game)  {
        game.winner = game.otherPlayer(player);
        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.winner = Stone.NONE;
    }
}
