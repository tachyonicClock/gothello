package gothello.gothelloserver.rules.commands;

import java.util.ArrayList;
import java.util.Stack;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.GothelloRules;
import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Rules.Stone;

public class PlayStone implements GameMove {
    Point placement;
    Stone player;
    Stack<GameMove> childCmds = new Stack<>();
    int previousPassCount = 0;

    public PlayStone(Point p, Stone player) {
        this.placement = p;
        this.player = player;
    }

    @Override
    public GameMove makeMove(GothelloRules game) throws IllegalMove {
        if (game.isLegal(placement.x, placement.y, player))
            new IllegalMove("The stone placement is illegal");

        previousPassCount = game.successivePassCount;
        game.successivePassCount = 0;

        childCmds.push(new Place(placement, player).makeMove(game));

        // Flip each stone that has been flipped
        ArrayList<Point> flips = game.getOthelloFlips(placement, player);
        for (Point flip : flips) {
            childCmds.push(new Flip(flip).makeMove(game));
        }

        // Apply go rules to every flipped stone
        for (Point flip : flips) {
            goCaptures(game, flip);
        }

        // Perform captures following the go rules
        if (game.inGoQuad(placement)) {
            goCaptures(game, placement);
        }

        childCmds.push(new NextTurn().makeMove(game));
        return this;
    }

    @Override
    public void unmakeMove(GothelloRules game) {
        while (!childCmds.empty()) {
            childCmds.pop().unmakeMove(game);
        }
        game.successivePassCount = previousPassCount;
    }

    private void goCaptures(GothelloRules game, Point p) {
        Stone player = game.board.get(p);

        for (Point dir : GothelloRules.cardinal) {
            Point adj = p.add(dir);

            if (!game.inBounds(adj) || game.board.get(adj) == player)
                continue;

            // Check the liberty of the adjacent pieces as the opponent
            ArrayList<Point> group = new ArrayList<>();

            // If the adjacent piece is a gothello piece and has 0 liberties
            if (game.libertyCount(adj, game.otherPlayer(player), group) == 0) {
                Stone captColour = game.board.get(adj);
                // Capture group without liberties
                for (Point stone : group) {
                    if (game.inGoQuad(stone) && captColour == game.board.get(stone)) {
                        childCmds.push(new Capture(stone).makeMove(game));
                    }
                }
            }
        }
    }
}