package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.Rules.Stone;
import gothello.gothelloserver.rules.GothelloRules;

public class Resign implements GameMove {

    Stone player;

    public Resign(Stone player) {
        this.player = player;
    }

    @Override
    public GameMove makeMove(GothelloRules game)  {
        game.winner = game.otherPlayer(player);
        return this;
    }

    @Override
    public void unmakeMove(GothelloRules game) {
        game.winner = Stone.NONE;
    }
}
