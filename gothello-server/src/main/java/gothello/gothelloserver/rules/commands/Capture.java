package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.Placement;

public class Capture implements GameMove {
    public Placement captured;

    public Capture(Point p) {
        captured = new Placement(p);
    }

    @Override
    public GameMove makeMove(GothelloState game) {
        captured.stone = game.board.get(captured);
        trackCapture(game, 1);
        game.board.set(captured, Stone.NONE);
        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.board.set(captured, captured.stone);
        trackCapture(game, -1);
    }

    private void trackCapture(GothelloState game, int amount){
        if (game.board.get(captured) != Stone.BLACK) {
            game.blackCaptures += amount;
        } else {
            game.whiteCaptures += amount;
        }
    }

}
