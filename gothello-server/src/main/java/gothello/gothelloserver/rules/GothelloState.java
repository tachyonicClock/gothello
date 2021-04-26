package gothello.gothelloserver.rules;

import java.util.ArrayList;
import gothello.gothelloserver.rules.Rules.Stone;

/**
 * GothelloRules is an implementation of rules that implements the rules for
 * Gothello
 */
public class GothelloState {
  // Internal representation of the board
  public Board board = new Board();
  public Stone activePlayer = Stone.BLACK;
  public int successivePassCount = 0;
  public Stone winner = Stone.NONE;
  public int turnNumber = 0;
  public int whiteCaptures = 0;
  public int blackCaptures = 0;

  // the 4 main points of a compass
  public static final Point[] cardinal = new Point[] { new Point(0, 1), new Point(1, 0), new Point(0, -1),
      new Point(-1, 0) };
  // principal points are the 8 points of a compass
  public static final Point[] principalPoints = new Point[] { new Point(0, 1), new Point(1, 0), new Point(0, -1),
      new Point(-1, 0), new Point(1, 1), new Point(-1, 1), new Point(1, -1), new Point(-1, -1) };

  public GothelloState() {
    // Set board initial state
    board.set(1, 1, Stone.WHITE);
    board.set(2, 2, Stone.WHITE);
    board.set(1, 2, Stone.BLACK);
    board.set(2, 1, Stone.BLACK);
    board.set(5, 6, Stone.WHITE);
    board.set(6, 5, Stone.WHITE);
    board.set(5, 5, Stone.BLACK);
    board.set(6, 6, Stone.BLACK);
  }

  private boolean inOthelloQuad(Point p) {
    int b = getBoardSize();
    return (p.x < b / 2 && p.y < b / 2) || (p.x >= b / 2 && p.y >= b / 2);
  }

  public boolean inGoQuad(Point p) {
    return !inOthelloQuad(p);
  }

  // Returns true if the x and y values are in the board, false if not
  public boolean inBounds(Point p) {
    int boardSize = getBoardSize();
    return (p.x >= 0 && p.x < boardSize) && (p.y >= 0 && p.y < boardSize);
  }

  private boolean isTurn(Stone player) {
    return activePlayer == player;
  }

  public int getBoardSize() {
    return 8;
  }

  public Stone otherPlayer(Stone player) {
    switch (player) {
    case WHITE:
      return Stone.BLACK;
    case BLACK:
      return Stone.WHITE;
    default:
      return Stone.WHITE;
    }
  }

  // getScore returns the score of the specified player
  public int getScore(Stone player) {
    int score = 0;
    // For each square on the board
    for (int x = 0; x < getBoardSize(); x++) {
      for (int y = 0; y < getBoardSize(); y++) {
        // If the current square on the board is the same as the player add one to the
        // score
        if (board.get(x, y) == player) {
          score++;
        }
      }
    }
    if (player == Stone.BLACK) {
      return score + blackCaptures;
    } else if (player == Stone.WHITE) {
      return score + whiteCaptures;
    } else {
      return 0;
    }
  }

  // getBoardSize returns the size of a square board

  public ArrayList<Point> getOthelloFlips(Point p, Stone player) {
    ArrayList<Point> flips = new ArrayList<>();
    for (Point dir : principalPoints) {
      if (!inBounds(p.add(dir)))
        continue;

      ArrayList<Point> check = othelloCheck(p.add(dir), dir, player);
      if (check != null) {
        flips.addAll(check);
      }
    }
    return flips;
  }

  // isLegal returns true or false depending on if the square is a legal move
  // for the specified player
  public boolean isLegal(int x, int y, Stone player) {
    Point p = new Point(x, y);

    // basic reasons for a move to be illegal
    if (!inBounds(p) || !isTurn(player) || board.get(p) != Stone.NONE) {
      return false;
    }

    // Find all the pieces to be flipped
    ArrayList<Point> flips = getOthelloFlips(p, player);

    // If point is in Othello Quadrant
    if (inOthelloQuad(p)) {
      // Othello moves are legal if they flip at least one piece
      return (flips.size() != 0) ? true : false;
    }

    // If the stone would have an open space
    if (libertyCount(p, player, new ArrayList<Point>()) != 0) {
      return true;
    }

    // If adjacent piece has 1 liberty then the move would cause that piece to be
    // taken and become legal
    for (Point dir : cardinal) {
      Point adjacent = p.add(dir);

      if (!inBounds(adjacent) || board.get(adjacent) == player)
        continue;

      // If adjacent piece is in Othello Quadrant and opposing
      if (inOthelloQuad(adjacent)) {
        // If the piece is in the toFlip list
        if (flips.contains(adjacent)) {
          // Move is legal
          return true;
        }
      }

      // Add in the pieces to be flipped so that libertyCount acts as though they
      // already have been flipped
      // If adjacent piece has one liberty and is opposing and is in go quadrant
      if (inGoQuad(adjacent) && libertyCount(adjacent, otherPlayer(player), new ArrayList<Point>(flips)) == 1) {
        return true;
      }

    }
    return false;
  }

  // Recursively search along a direction. Returns all stones that match the
  // opponents stones in a line (if they are flip-able in the othello quad)
  private ArrayList<Point> othelloCheck(Point p, Point dir, Stone player) {
    Stone currentStone = board.get(p);

    // The line has ended
    if (currentStone == player) {
      return new ArrayList<Point>();
    }

    // The line has gone off the edge or reached empty space
    if (!inBounds(p.add(dir)) || currentStone == Stone.NONE) {
      return null;
    }

    // Recursively search in the direction
    ArrayList<Point> line = othelloCheck(p.add(dir), dir, player);
    if (line == null) {
      return null;
    }

    // If the current stone is flippable (in othello quadrant) add it to the list
    if (inOthelloQuad(p)) {
      line.add(p);
    }

    return line;
  }

  // private int libertyCheck
  public int libertyCount(Point p, Stone player, ArrayList<Point> searched) {
    int liberties = 0;
    // Add the current piece to previous pieces
    searched.add(p);

    for (Point dir : cardinal) {
      Point adjacent = p.add(dir);

      // Check that the square is in the grid
      if (!inBounds(p.add(dir)))
        continue;

      Stone adjacentStone = board.get(adjacent);

      // Check the .x and .y of each point in searched to see if they match the
      // new one
      if (searched.contains(adjacent)) {
        continue;
      }

      // If none of the stones has same coords as ones in previous pieces
      if (adjacentStone == Stone.NONE) {
        // If the piece is blank
        searched.add(adjacent);
        liberties += 1;
      }
      // If the stone is the same as the current player
      else if (adjacentStone == player) {
        // If the piece is allied and not in previous pieces
        // Add the liberties of that piece to this one
        searched.add(adjacent);
        liberties += libertyCount(adjacent, player, searched);
      }
    }
    return liberties;
  }
}
