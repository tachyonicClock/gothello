package gothello.gothelloserver.rules.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.security.auth.callback.LanguageCallback;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Placement;
import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Stone;

public class PlayStone extends GameMove {
    public Stack<GameMove> changes = new Stack<>();

    Placement placement;
    int previousPassCount = 0;

    public PlayStone(Point p, Stone player) {
        this.placement = new Placement(p, player);
    }

    private GameMove internalMakeMove(GothelloState game) throws IllegalMove {
        if (!game.inBounds(placement))
            throw new IllegalMove("Can't play a stone out of the game board");

        if (!game.isTurn(placement.stone))
            throw new IllegalMove("It must be your turn to play a stone");

        changes.push(new Place(placement).makeMove(game));

        // Flip each stone that has been flipped
        ArrayList<Point> flips = game.getOthelloFlips(placement, placement.stone);
        for (Point flip : flips) {
            changes.push(new Flip(flip).makeMove(game));
        }

        if (game.inOthelloQuad(placement) && flips.size() == 0)
            throw new IllegalMove("Stones played in the othello quadrant must flip at least one stone");

        // Apply go rules to every flipped stone
        for (Point flip : flips) {
            goCaptures(game, flip);
        }
        
        // Perform captures following the go rules
        if (game.inGoQuad(placement)) {
            goCaptures(game, placement);
        }

        if (game.inGoQuad(placement) && game.libertyCount(placement, placement.stone, new ArrayList<>()) == 0)
            throw new IllegalMove("Can't place a stone so that its group is surrounded");

        // if (game.inGoQuad(placement) && libertyCount() )
            // throw new IllegalMove("Can't place stone since the group would be captured");

        changes.push(new NextTurn().makeMove(game));
        return this;
    }

    @Override
    public GameMove makeMove(GothelloState game) throws IllegalMove {
        try {
            return internalMakeMove(game);
        } catch (IllegalMove e) {
            unmakeMove(game);
            throw e;
        }

    }

    @Override
    public void unmakeMove(GothelloState game) {
        while (!changes.empty()) {
            changes.pop().unmakeMove(game);
        }
    }

    private void goCaptures(GothelloState game, Point p) {
        Stone player = game.board.get(p);

        for (Point dir : GothelloState.cardinal) {
            Point adj = p.add(dir);
            Stone oppPlayer = game.otherPlayer(player) ;
            
            if (!game.inBounds(adj) || game.board.get(adj) != oppPlayer)
                continue;

            // Check the liberty of the adjacent pieces as the opponent
            ArrayList<Point> group = new ArrayList<>();

            // If the adjacent piece is a gothello piece and has 0 liberties
            if (game.libertyCount(adj, oppPlayer, group) == 0) {
                // Capture group without liberties
                for (Point stone : group) {
                    if (game.inGoQuad(stone) && oppPlayer == game.board.get(stone)) {
                        changes.push(new Capture(stone).makeMove(game));
                    }
                }
            }
        }
    }
}