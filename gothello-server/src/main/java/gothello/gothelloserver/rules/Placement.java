package gothello.gothelloserver.rules;

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
