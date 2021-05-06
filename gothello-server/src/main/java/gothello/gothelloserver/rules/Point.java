package gothello.gothelloserver.rules;

public class Point {

  // the 4 main points of a compass
  public static final Point[] cardinalDirections = new Point[] { new Point(0, 1), new Point(1, 0), new Point(0, -1),
      new Point(-1, 0) };

  // principal points are the 8 points of a compass
  public static final Point[] principalDirections = new Point[] { new Point(0, 1), new Point(1, 0), new Point(0, -1),
    new Point(-1, 0), new Point(1, 1), new Point(-1, 1), new Point(1, -1), new Point(-1, -1) };

  public final int x;
  public final int y;

  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public boolean equals(Point p) {
    return x == p.x && y == p.y;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Point) {
      return equals((Point) obj);
    }
    return super.equals(obj);
  }

  @Override
  public String toString() {
    return String.format("(%d %d)", x, y);
  }

  public Point add(Point p) {
    return new Point(x + p.x, y + p.y);
  }
}
