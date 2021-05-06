package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.GothelloState;

public class Resign extends GameCommand {

    Stone player;

    public Resign(Stone player) {
        this.player = player;
    }

    @Override
    public GameCommand makeMove(GothelloState game)  {
        game.winner = Stone.otherPlayer(player);
        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.winner = Stone.NONE;
    }
}
