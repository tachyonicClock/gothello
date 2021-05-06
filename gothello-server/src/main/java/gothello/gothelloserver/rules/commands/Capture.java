package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.Placement;

public class Capture extends GameCommand {
    public Placement captured;

    public Capture(Point p) {
        captured = new Placement(p);
    }

    @Override
    public GameCommand makeMove(GothelloState game) {
        captured.stone = game.board.get(captured);
        game.board.set(captured, Stone.NONE);
        trackCapture(game, 1);
        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.board.set(captured, captured.stone);
        trackCapture(game, -1);
    }

    private void trackCapture(GothelloState game, int amount){
        if (captured.stone == Stone.WHITE) {
            game.blackCaptures += amount;
        }
        if (captured.stone == Stone.BLACK) {
            game.whiteCaptures += amount;
        }
    }

}
