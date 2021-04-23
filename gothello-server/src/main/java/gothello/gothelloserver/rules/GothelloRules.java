package gothello.gothelloserver.rules;

import java.util.ArrayList;

/**
 * GothelloRules is an implementation of rules that implements the rules for
 * Gothello
 */
public class GothelloRules implements Rules {
  // Internal representation of the board
  private Stone[][] board = new Stone[8][8];
  private Stone activePlayer = Stone.BLACK;
  private int successivePassCount = 0;
  private Stone winner = Stone.NONE;
  private int turnNumber = 0;
  private int whiteCaptures = 0;
  private int blackCaptures = 0;

  private static class Point {
    final int x;
    final int y;

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

  private static Point newPoint(int x, int y) {
    return new Point(x, y);
  }

  // the 4 main points of a compass
  private static final Point[] cardinal = new Point[] { newPoint(0, 1), newPoint(1, 0), newPoint(0, -1),
      newPoint(-1, 0) };
  // principal points are the 8 points of a compass
  private static final Point[] principalPoints = new Point[] { newPoint(0, 1), newPoint(1, 0), newPoint(0, -1),
      newPoint(-1, 0), newPoint(1, 1), newPoint(-1, 1), newPoint(1, -1), newPoint(-1, -1) };

  public GothelloRules() {
    // Set board initial state
    for (int x = 0; x < board.length; x++) {
      for (int y = 0; y < board[x].length; y++) {
        board[x][y] = Stone.NONE;
      }
    }

    board[1][1] = Stone.WHITE;
    board[2][2] = Stone.WHITE;
    board[1][2] = Stone.BLACK;
    board[2][1] = Stone.BLACK;

    board[5][6] = Stone.WHITE;
    board[6][5] = Stone.WHITE;
    board[5][5] = Stone.BLACK;
    board[6][6] = Stone.BLACK;
  }

  private boolean inOthelloQuad(Point p) {
    int b = getBoardSize();
    return (p.x < b / 2 && p.y < b / 2) || (p.x >= b / 2 && p.y >= b / 2);
  }

  private boolean inGoQuad(Point p) {
    return !inOthelloQuad(p);
  }

  private Stone otherPlayer(Stone player) {
    switch (player) {
      case WHITE: return Stone.BLACK;
      case BLACK: return Stone.WHITE;
      default: return Stone.WHITE;
    }
  }

  private boolean isTurn(Stone player) {
    return activePlayer == player;
  }

  //
  // Get Game State
  //

  // getSquare returns the stone at (x,y) on the board
  public Stone getSquare(int x, int y) {
    return board[x][y];
  }

  private Stone getSquare(Point p) {
    return getSquare(p.x, p.y);
  }

  private void setSquare(Point p, Stone stone) {
    board[p.x][p.y] = stone;
  }

  // getTurn returns the player who's turn it is
  public Stone getTurn() {
    return activePlayer;
  };

  // getTurnNumber returns the number of turns that have been taken by both sides
  public int getTurnNumber() {
    return turnNumber;
  }

  // getWinner returns the player who has won or Stone.NONE
  public Stone getWinner() {
    return winner;
  }

  private Stone calculateWinner() {
    int whiteScore = getScore(Stone.WHITE);
    int blackScore = getScore(Stone.BLACK);
    if (whiteScore > blackScore) {
      return Stone.WHITE;
    } else if (whiteScore < blackScore) {
      return Stone.BLACK;
    } else {
      return Stone.DRAW;
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
        if (board[x][y] == player) {
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
  public int getBoardSize() {
    // Assuming board is square
    return board[0].length;
  }

  private ArrayList<Point> getOthelloFlips(Point p, Stone player) {
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
    Point p = newPoint(x, y);

    // basic reasons for a move to be illegal
    if (!inBounds(p) || !isTurn(player) || getSquare(p) != Stone.NONE) {
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

      if (!inBounds(adjacent) || getSquare(adjacent) == player)
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
    Stone currentStone = getSquare(p);

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
  private int libertyCount(Point p, Stone player, ArrayList<Point> searched) {
    int liberties = 0;
    // Add the current piece to previous pieces
    searched.add(p);

    for (Point dir : cardinal) {
      Point adjacent = p.add(dir);

      // Check that the square is in the grid
      if (!inBounds(p.add(dir)))
        continue;

      Stone adjacentStone = getSquare(adjacent);

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

  // Gets the amount of open spaces around the given stone

  // Returns true if the x and y values are in the board, false if not
  private boolean inBounds(int x, int y) {
    int boardSize = getBoardSize();
    if ((x >= 0 && x < boardSize) && (y >= 0 && y < boardSize)) {
      return true;
    } else {
      return false;
    }
  }

  private boolean inBounds(Point p) {
    return inBounds(p.x, p.y);
  }

  // isGameOver returns true if the game is finished
  public boolean isGameOver() {
    return (winner != Stone.NONE);
  }

  // addCaptures adds one to captures of the right colour based on piece being
  // captured
  private void trackCapture(Point p) {
    // If the piece getting captured is black
    if (getSquare(p) == Stone.BLACK) {
      // Add one to white captures
      whiteCaptures++;
    }
    //
    else {
      blackCaptures++;
    }
  }

  //
  // Change Game State
  //

  // pass skips a player's turn
  public void pass(Stone player) {
    if (activePlayer != player) {
      return;
    }
    successivePassCount++;
    if (successivePassCount == 2) {
      winner = calculateWinner();
    }
    nextTurn();
  }

  // resign forfeits the game
  public void resign(Stone player) {
    winner = (player == Stone.BLACK) ? Stone.WHITE : Stone.BLACK;
  }

  private void nextTurn() {
    activePlayer = (activePlayer == Stone.BLACK ? Stone.WHITE : Stone.BLACK);
  }

  private void goCaptures(Point p) {
    Stone player = getSquare(p);

    for (Point dir : cardinal) {
      Point adj = p.add(dir);

      if (!inBounds(adj) || getSquare(adj) == player)
        continue;

      // Check the liberty of the adjacent pieces as the opponent
      ArrayList<Point> group = new ArrayList<>();

      // If the adjacent piece is a gothello piece and has 0 liberties
      if (libertyCount(adj, otherPlayer(player), group) == 0) {
        Stone captColour = getSquare(adj);
        // Capture group without liberties
        for (Point stone : group) {
          if (inGoQuad(stone) && captColour == getSquare(stone)) {
            trackCapture(stone);
            setSquare(stone, Stone.NONE);
          }
        }
      }
    }
  }

  // playStone places a stone at (x,y)
  public boolean playStone(int x, int y, Stone player) {
    Point p = newPoint(x, y);

    if (!isLegal(x, y, player)) {
      return false;
    }

    turnNumber++;
    successivePassCount = 0;

    // Place stone
    setSquare(p, player);

    // Flip s
    ArrayList<Point> flips = getOthelloFlips(p, player);
    for (Point flip : flips) {
      setSquare(flip, player);
    }

    // Apply go rules to every flipped stone
    for (Point flip : flips) {
      goCaptures(flip);
    }

    // Perform captures following the go rules
    if (inGoQuad(p)) {
      goCaptures(p);
    }

    nextTurn();
    return true;
  }

}
