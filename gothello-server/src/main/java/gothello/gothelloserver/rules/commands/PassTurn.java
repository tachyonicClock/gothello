package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.GothelloState;

public class PassTurn extends GameCommand {
    Stone player;
    GameCommand subMove;


    public PassTurn(Stone player) {
        this.player = player;
    }

    @Override
    public GameCommand makeMove(GothelloState game) throws IllegalMove {
        if (game.activePlayer != player) {
            throw new IllegalMove("You cannot pass when it is not your turn");
        }

        if (!game.history.isEmpty() && game.history.peek() instanceof PassTurn ){
            subMove = new EndGame().makeMove(game);
        }else {
            subMove = new NextTurn().makeMove(game);
        }

        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.winner = Stone.NONE;
        subMove.unmakeMove(game);
    }
}
