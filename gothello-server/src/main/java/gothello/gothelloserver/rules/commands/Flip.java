package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Placement;
import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Stone;

public class Flip extends GameCommand {
    public Placement flip;

    public Flip(Point p) {
        flip = new Placement(p);
    }

    @Override
    public GameCommand makeMove(GothelloState game) {
        flip.stone = game.board.get(flip);
        game.board.set(flip, Stone.otherPlayer(flip.stone));
        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.board.set(flip, flip.stone);
    }
}