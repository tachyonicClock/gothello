package gothello.gothelloserver.messages;

import gothello.gothelloserver.rules.Rules;
import gothello.gothelloserver.rules.Stone;

public class Score {
    public class StoneScore {
        public final int captures;
        public final int territory;
        public final int overall;

        public StoneScore(int captures, int territory, int overall){
            this.captures = captures;
            this.territory = territory;
            this.overall = overall;
        }
    }

    public final StoneScore blackScore;
    public final StoneScore whiteScore;

    public Score(Stone player, Rules rules){
        this.blackScore = new StoneScore(
            rules.getCaptures(Stone.BLACK), 
            rules.getTerritory(Stone.BLACK), 
            rules.getScore(Stone.BLACK));
        this.whiteScore = new StoneScore(
                rules.getCaptures(Stone.WHITE), 
                rules.getTerritory(Stone.WHITE), 
                rules.getScore(Stone.WHITE));
    }
}
