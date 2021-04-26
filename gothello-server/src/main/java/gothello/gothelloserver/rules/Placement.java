package gothello.gothelloserver.rules;

import gothello.gothelloserver.rules.Rules.Stone;

public class Placement extends Point{
    public Stone stone;

    public Placement(Point point, Stone stone){
        super(point.x, point.y);
        this.stone = stone;
    }

    public Placement(Point point){
        super(point.x, point.y);
        this.stone = Stone.NONE;
    }
}
