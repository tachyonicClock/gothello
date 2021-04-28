package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.GothelloState;

public class PassTurn extends GameMove {
    Stone player;
    NextTurn nextTurn = new NextTurn();

    public PassTurn(Stone player) {
        this.player = player;
    }

    @Override
    public GameMove makeMove(GothelloState game) throws IllegalMove {
        if (game.activePlayer != player) {
            throw new IllegalMove("You cannot pass when it is not your turn");
        }

        nextTurn.makeMove(game);
        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.winner = Stone.NONE;
        nextTurn.unmakeMove(game);
    }

    @Override
    public boolean isPass() {
        return true;
    }
}
