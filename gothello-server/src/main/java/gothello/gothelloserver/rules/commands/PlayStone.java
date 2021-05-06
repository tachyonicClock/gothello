package gothello.gothelloserver.rules.commands;

import java.util.ArrayList;
import java.util.Stack;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.Board;
import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Placement;
import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Stone;

public class PlayStone extends GameCommand {
    public Stack<GameCommand> changes = new Stack<>();
    private Placement placement;

    public PlayStone(Point p, Stone player) {
        this.placement = new Placement(p, player);
    }

    private GameCommand internalMakeMove(GothelloState game) throws IllegalMove {
        Board board = game.board;

        if (!board.inBounds(placement))
            throw new IllegalMove("Can't play a stone out of the game board");

        if (!game.isTurn(placement.stone))
            throw new IllegalMove("It must be your turn to play a stone");

        changes.push(new Place(placement).makeMove(game));

        // Flip each stone that has been flipped
        ArrayList<Point> flips = getOthelloFlips(board, placement, placement.stone);
        for (Point flip : flips) {
            changes.push(new Flip(flip).makeMove(game));
        }

        if (board.inOthelloQuad(placement) && flips.size() == 0)
            throw new IllegalMove("Stones played in the othello quadrant must flip at least one stone");

        // Apply go rules to every flipped stone
        for (Point flip : flips) {
            goCaptures(game, flip);
        }

        // Perform captures following the go rules
        if (board.inGoQuad(placement)) {
            goCaptures(game, placement);
        }

        if (board.inGoQuad(placement) && libertyCount(board, placement, placement.stone, new ArrayList<>()) == 0)
            throw new IllegalMove("Can't place a stone so that its group is surrounded");

        changes.push(new NextTurn().makeMove(game));
        return this;
    }

    @Override
    public GameCommand makeMove(GothelloState game) throws IllegalMove {
        try {
            internalMakeMove(game);
        } catch (IllegalMove e) {
            internalUnmakeMove(game);
            throw e;
        }

        if (game.previousStates.contains(game.board.zKey())){
            internalUnmakeMove(game);
            throw new IllegalMove("Breaks the ko-rule");
        }

        game.previousStates.add(game.board.zKey());
        return this;
    }

    private void internalUnmakeMove(GothelloState game){
        while (!changes.empty()) {
            changes.pop().unmakeMove(game);
        }
    }
    
    @Override
    public void unmakeMove(GothelloState game) {
        game.previousStates.remove(game.board.zKey());
        internalUnmakeMove(game);
    }

    private void goCaptures(GothelloState game, Point p) {
        Board board = game.board;
        Stone player = game.board.get(p);

        for (Point dir : Point.cardinalDirections) {
            Point adj = p.add(dir);
            Stone oppPlayer = Stone.otherPlayer(player);

            if (!board.inBounds(adj) || board.get(adj) != oppPlayer)
                continue;

            // Check the liberty of the adjacent pieces as the opponent
            ArrayList<Point> group = new ArrayList<>();

            // If the adjacent piece is a gothello piece and has 0 liberties
            if (libertyCount(board, adj, oppPlayer, group) == 0) {
                // Capture group without liberties
                for (Point stone : group) {
                    if (board.inGoQuad(stone) && oppPlayer == board.get(stone)) {
                        changes.push(new Capture(stone).makeMove(game));
                    }
                }
            }
        }
    }

    // getBoardSize returns the size of a square board
    private ArrayList<Point> getOthelloFlips(Board board, Point p, Stone player) {
        ArrayList<Point> flips = new ArrayList<>();
        for (Point dir : Point.principalDirections) {
            if (!board.inBounds(p.add(dir)))
                continue;

            ArrayList<Point> check = othelloCheck(board, p.add(dir), dir, player);
            if (check != null) {
                flips.addAll(check);
            }
        }
        return flips;
    }

    // Recursively search along a direction. Returns all stones that match the
    // opponents stones in a line (if they are flip-able in the othello quad)
    private ArrayList<Point> othelloCheck(Board board, Point p, Point dir, Stone player) {
        Stone currentStone = board.get(p);

        // The line has ended
        if (currentStone == player) {
            return new ArrayList<Point>();
        }

        // The line has gone off the edge or reached empty space
        if (!board.inBounds(p.add(dir)) || currentStone == Stone.NONE) {
            return null;
        }

        // Recursively search in the direction
        ArrayList<Point> line = othelloCheck(board, p.add(dir), dir, player);
        if (line == null) {
            return null;
        }

        // If the current stone is flippable (in othello quadrant) add it to the list
        if (board.inOthelloQuad(p)) {
            line.add(p);
        }

        return line;
    }

    // private int libertyCheck
    public int libertyCount(Board board, Point p, Stone player, ArrayList<Point> searched) {
        int liberties = 0;
        // Add the current piece to previous pieces
        searched.add(p);

        for (Point dir : Point.cardinalDirections) {
            Point adjacent = p.add(dir);

            // Check that the square is in the grid
            if (!board.inBounds(p.add(dir)))
                continue;

            Stone adjacentStone = board.get(adjacent);

            // Check the .x and .y of each point in searched to see if they match the
            // new one
            if (searched.contains(adjacent)) {
                continue;
            }

            // If none of the stones has same coords as ones in previous pieces
            if (adjacentStone == Stone.NONE) {
                // If the piece is blank
                searched.add(adjacent);
                liberties += 1;
            }
            // If the stone is the same as the current player
            else if (adjacentStone == player) {
                // If the piece is allied and not in previous pieces
                // Add the liberties of that piece to this one
                searched.add(adjacent);
                liberties += libertyCount(board, adjacent, player, searched);
            }
        }
        return liberties;
    }
}