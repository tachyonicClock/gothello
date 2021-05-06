package gothello.gothelloserver.messages;

import java.util.ArrayList;
import java.util.List;

import gothello.gothelloserver.rules.commands.Capture;
import gothello.gothelloserver.rules.commands.Flip;
import gothello.gothelloserver.rules.commands.GameCommand;
import gothello.gothelloserver.rules.commands.PassTurn;
import gothello.gothelloserver.rules.commands.Place;
import gothello.gothelloserver.rules.Placement;
import gothello.gothelloserver.rules.commands.PlayStone;

public class LastMove {
    public Boolean pass;
    public List<Placement> captures = new ArrayList<>();
    public List<Placement> flips = new ArrayList<>();
    public Placement placement;

    private void add(Capture capture) {
        captures.add(capture.captured);
    }

    private void add(Flip flip) {
        flips.add(flip.flip);
    }

    private void add(Place place) {
        placement = place.placement;
    }

    private void add(PlayStone playStone) {
        for (GameCommand move : playStone.changes) {
            if (move instanceof Capture) {
                add((Capture) move);
            }
            if (move instanceof Flip) {
                add((Flip) move);
            }
            if (move instanceof Place) {
                add((Place) move);
            }
        }
        pass = false;
    }

    private void add(PassTurn passTurn){
        pass = true;
    }

    LastMove(GameCommand move) {
        if (move instanceof PlayStone) {
            add((PlayStone) move);
        } else if (move instanceof PassTurn){
            add((PassTurn)move);
        }
    }
}
