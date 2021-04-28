package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Placement;
import gothello.gothelloserver.rules.Stone;

public class Place extends GameMove {
    public Placement placement;

    public Place(Placement placement) {
        this.placement = placement;
    }

    @Override
    public GameMove makeMove(GothelloState game) {
        game.board.set(placement, placement.stone);
        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.board.set(placement, Stone.NONE);
    }
}