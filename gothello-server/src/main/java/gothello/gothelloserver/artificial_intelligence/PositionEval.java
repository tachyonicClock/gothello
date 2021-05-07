package gothello.gothelloserver.artificial_intelligence;

import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Stone;

public class PositionEval {
    
    public PositionEval(){

    }

    public int eval(GothelloState state, Stone player){
        return state.getScore(player) - state.getScore(Stone.otherPlayer(player));
    }
}
