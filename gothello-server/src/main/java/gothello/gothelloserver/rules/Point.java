package gothello.gothelloserver.rules;

public class Point {
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
