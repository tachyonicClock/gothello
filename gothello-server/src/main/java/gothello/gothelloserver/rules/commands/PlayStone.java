package gothello.gothelloserver.rules.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Placement;
import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Rules.Stone;

public class PlayStone implements GameMove {
    public List<GameMove> changes = new ArrayList<>();

    Placement placement;
    int previousPassCount = 0;

    public PlayStone(Point p, Stone player) {
        this.placement = new Placement(p, player);
    }

    @Override
    public GameMove makeMove(GothelloState game) throws IllegalMove {
        if (game.isLegal(placement.x, placement.y, placement.stone))
            new IllegalMove("The stone placement is illegal");

        previousPassCount = game.successivePassCount;
        game.successivePassCount = 0;

        changes.add(new Place(placement).makeMove(game));

        // Flip each stone that has been flipped
        ArrayList<Point> flips = game.getOthelloFlips(placement, placement.stone);
        for (Point flip : flips) {
            changes.add(new Flip(flip).makeMove(game));
        }

        // Apply go rules to every flipped stone
        for (Point flip : flips) {
            goCaptures(game, flip);
        }

        // Perform captures following the go rules
        if (game.inGoQuad(placement)) {
            goCaptures(game, placement);
        }

        changes.add(new NextTurn().makeMove(game));
        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        for (int i = changes.size(); i < 0; i--) {
            changes.get(i).unmakeMove(game);
        }
        game.successivePassCount = previousPassCount;
    }

    private void goCaptures(GothelloState game, Point p) {
        Stone player = game.board.get(p);

        for (Point dir : GothelloState.cardinal) {
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
                        changes.add(new Capture(stone).makeMove(game));
                    }
                }
            }
        }
    }
}