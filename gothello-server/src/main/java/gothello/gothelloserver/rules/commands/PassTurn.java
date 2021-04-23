package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.Rules.Stone;
import gothello.gothelloserver.rules.GothelloRules;

public class PassTurn implements GameMove {
    Stone player;
    NextTurn nextTurn = new NextTurn();

    public PassTurn(Stone player) {
        this.player = player;
    }

    @Override
    public GameMove makeMove(GothelloRules game) throws IllegalMove {
        if (game.activePlayer != player) {
            throw new IllegalMove("You cannot pass when it is not your turn");
        }

        game.successivePassCount ++;
        if (game.successivePassCount == 2) {
            game.winner = calculateWinner(game);
        }

        nextTurn.makeMove(game);
        return this;
    }

    @Override
    public void unmakeMove(GothelloRules game) {
        game.winner = Stone.NONE;
        game.successivePassCount --;
        nextTurn.unmakeMove(game);
    }

    private Stone calculateWinner(GothelloRules game) {
        int whiteScore = game.getScore(Stone.WHITE);
        int blackScore = game.getScore(Stone.BLACK);
        if (whiteScore > blackScore) {
            return Stone.WHITE;
        } else if (whiteScore < blackScore) {
            return Stone.BLACK;
        } else {
            return Stone.DRAW;
        }
    }
}
