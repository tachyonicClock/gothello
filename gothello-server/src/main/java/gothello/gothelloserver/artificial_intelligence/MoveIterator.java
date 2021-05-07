package gothello.gothelloserver.artificial_intelligence;

import java.security.InvalidParameterException;
import java.util.Iterator;

import gothello.gothelloserver.rules.Point;
import gothello.gothelloserver.rules.Stone;
import gothello.gothelloserver.rules.commands.GameCommand;
import gothello.gothelloserver.rules.commands.PassTurn;
import gothello.gothelloserver.rules.commands.PlayStone;

// Iterate over all possible moves except resignation. Illegal moves are included
// along with legal ones
public class MoveIterator implements Iterator<GameCommand> {
    // game to generate moves for
    Stone player;
    int index;

    MoveIterator(Stone player) {
        this.player = player;
        if (!player.isPlayable())
            throw new InvalidParameterException("player must be a playable stone");
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < 65;
    }

    @Override
    public GameCommand next() {
        GameCommand cmd = (index < 64) ? new PlayStone(new Point(index), player) : new PassTurn(player);
        index++;
        return cmd;
    }

}
