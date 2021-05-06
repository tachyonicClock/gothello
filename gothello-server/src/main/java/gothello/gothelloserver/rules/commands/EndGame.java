package gothello.gothelloserver.rules.commands;

import gothello.gothelloserver.exceptions.IllegalMove;
import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Stone;

// EndGame will end the game
public class EndGame extends GameCommand {

    @Override
    public GameCommand makeMove(GothelloState game) throws IllegalMove {
        int whiteScore = game.getScore(Stone.WHITE);
        int blackScore = game.getScore(Stone.BLACK);

        if (whiteScore > blackScore)
            game.winner = Stone.WHITE;
        else if (whiteScore < blackScore)
            game.winner = Stone.BLACK;
        else
            game.winner = Stone.DRAW;

        return this;
    }

    @Override
    public void unmakeMove(GothelloState game) {
        game.winner = Stone.NONE;
    }    
}
