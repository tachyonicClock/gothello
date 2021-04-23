package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Rules.Stone;

public class Flip implements GameMove {
    Point p;
    Stone stone;

    public Flip(Point p) {
        this.p = p;
    }

    @Override
    public GameMove makeMove(GothelloRules game) {
        stone = game.board.get(p);
        game.board.set(p, game.otherPlayer(stone));
        return this;
    }

    @Override
    public void unmakeMove(GothelloRules game) {
        game.board.set(p, stone);
    }
}