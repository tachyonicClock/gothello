package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Rules.Stone;

public class Place implements GameMove {
    Point p;
    Stone stone;

    public Place(Point p, Stone stone) {
        this.p = p;
        this.stone = stone;
    }

    @Override
    public GameMove makeMove(GothelloRules game) {
        game.board.set(p, stone);
        return this;
    }

    @Override
    public void unmakeMove(GothelloRules game) {
        game.board.set(p, Stone.NONE);
    }
}