package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Rules.Stone;

public class Capture implements GameMove {
    Point p;
    Stone stone;

    public Capture(Point p) {
        this.p = p;
    }

    @Override
    public GameMove makeMove(GothelloRules game) {
        stone = game.board.get(p);
        trackCapture(game, 1);
        game.board.set(p, Stone.NONE);
        return this;
    }

    @Override
    public void unmakeMove(GothelloRules game) {
        game.board.set(p, stone);
        trackCapture(game, -1);
    }

    private void trackCapture(GothelloRules game, int amount){
        if (game.board.get(p) != Stone.BLACK) {
            game.blackCaptures += amount;
        } else {
            game.whiteCaptures += amount;
        }
    }

}
