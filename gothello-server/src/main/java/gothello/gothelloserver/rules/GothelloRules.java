package gothello.gothelloserver.rules;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * GothelloRules is an implementation of rules that implements the rules for
 * Gothello
 */
public class GothelloRules implements Rules {
  Stone[][] board = new Stone[8][8];
  private Stone currentTurn = Stone.BLACK;
  private int successivePasses = 0;
  private Stone winner = Stone.NONE;
  private int turnNumber = 0;

  private class Point {
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

  private Point newPoint(int x, int y) {
    return new Point(x, y);
  }

  // Set up lists used by methods
  ArrayList<Point> toFlip = new ArrayList<Point>();
  ArrayList<Point> previousPieces = new ArrayList<Point>();
  int whiteCaptures = 0;
  int blackCaptures = 0;

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

  // the 4 main points of a compass
  private final Point[] cardinal = new Point[] { newPoint(0, 1), newPoint(1, 0), newPoint(0, -1), newPoint(-1, 0) };
  // principal points are the 8 points of a compass
  private final Point[] principalPoints = new Point[] { newPoint(0, 1), newPoint(1, 0), newPoint(0, -1),
      newPoint(-1, 0), newPoint(1, 1), newPoint(-1, 1), newPoint(1, -1), newPoint(-1, -1) };

  private boolean inOthelloQuad(int x, int y) {
    int b = getBoardSize();
    return (x < b / 2 && y < b / 2) || (x >= b / 2 && y >= b / 2);
  }

  private boolean inGoQuad(int x, int y) {
    int b = getBoardSize();
    return (x >= b / 2 && y < b / 2) || (x < b / 2 && y >= b / 2);
  }

  private Stone otherPlayer(Stone player) {
    if (player == Stone.WHITE)
      return Stone.BLACK;
    if (player == Stone.BLACK)
      return Stone.WHITE;
    return Stone.NONE;
  }

  private boolean isTurn(Stone player) {
    return currentTurn == player;
  }

  //
  // Get Game State
  //

  // getSquare returns the stone at (x,y) on the board
  public Stone getSquare(int x, int y) {
    return board[x][y];
  }

  // getTurn returns the player who's turn it is
  public Stone getTurn() {
    return currentTurn;
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
    int WhiteScore = getScore(Stone.WHITE);
    int BlackScore = getScore(Stone.BLACK);
    if (WhiteScore > BlackScore) {
      return Stone.WHITE;
    } else if (WhiteScore < BlackScore) {
      return Stone.BLACK;
    } else {
      return Stone.DRAW;
    }
  }

  // getScore returns the score of the specified player
  public int getScore(Stone player) {
    int score = 0;
    // For each square on the board
    for (int x = 0; x < board.length; x++) {
      for (int y = 0; y < board[x].length; y++) {
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

  // isLegal returns true or false depending on if the square is a legal move
  // for the specified player
  public boolean isLegal(int x, int y, Stone player) {
    // basic reasons for a move to be illegal
    if (!inBounds(x, y) || !isTurn(player) || board[x][y] != Stone.NONE) {
      return false;
    }
    // Clear the toFlip list and previous pieces
    toFlip.clear();

    // Find all the pieces to be flipped
    for (Point dir : principalPoints) {
      if (!inBounds(x + dir.x, y + dir.y))
        continue;

      ArrayList<Point> checkResult = othelloCheck(x + dir.x, y + dir.y, dir, player);
      if (checkResult != null) {
        toFlip.addAll(checkResult);
      }
    }

    // If point is in Othello Quadrant
    if (inOthelloQuad(x, y)) {
      // Othello moves are legal if they flip at least one piece
      return (toFlip.size() != 0) ? true : false;
    }

    // If the stone would have an open space
    // previousPieces.clear();
    if (libertyCount(x, y, player, new ArrayList<Point>()) != 0) {
      return true;
    }
    // If adjacent piece has 1 liberty then the move would cause that piece to be
    // taken and become legal
    for (Point dir : cardinal) {
      Point adjacent = new Point(x + dir.x, y + dir.y);

      if (!inBounds(x + dir.x, y + dir.y) || getSquare(x + dir.x, y + dir.y) == player)
        continue;

      // If adjacent piece is in Othello Quadrant and opposing
      if (inOthelloQuad(x + dir.x, y + dir.y)) {
        // If the piece is in the toFlip list
        Point currentStoneCoords = new Point(x + dir.x, y + dir.y);
        for (int k = 0; k < toFlip.size(); k++) {
          if (toFlip.get(k).equals(currentStoneCoords)) {
            // Move is legal
            return true;
          }
        }
      }

      // Add in the pieces to be flipped so that libertyCount acts as though they
      // already have been flipped, for accuracy
      // previousPieces.clear();
      // previousPieces.addAll(toFlip);

      // If adjacent piece has one liberty and is opposing and is in go quadrant
      if (inGoQuad(x + dir.x, y + dir.y)
          && libertyCount(x + dir.x, y + dir.y, otherPlayer(player), new ArrayList<Point>(toFlip)) == 1) {
        return true;
      }

    }
    return false;
  }

  // Recursively search along a direction. Returns all stones that match the
  // opponents stones in a line (if they are flip-able in the othello quad)
  private ArrayList<Point> othelloCheck(int x, int y, Point dir, Stone player) {
    Stone currentStone = getSquare(x, y);

    // The line has ended
    if (currentStone == player) {
      return new ArrayList<Point>();
    }

    // The line has gone off the edge or reached empty space
    if (!inBounds(x + dir.x, y + dir.y) || currentStone == Stone.NONE) {
      return null;
    }

    // Recursively search in the direction
    ArrayList<Point> line = othelloCheck(x + dir.x, y + dir.y, dir, player);
    if (line == null) {
      return null;
    }

    // If the current stone is flippable (in othello quadrant) add it to the list
    if (inOthelloQuad(x, y)) {
      line.add(new Point(x, y));
    }

    return line;
  }

  // private int libertyCheck
  private int libertyCount(int x, int y, Stone player, ArrayList<Point> searched) {
    int liberties = 0;
    // Add the current piece to previous pieces
    searched.add(new Point(x, y));

    for (Point dir : cardinal) {
      if (!inBounds(x + dir.x, y + dir.y))
        continue;

      // Check that the square is in the grid
      Stone currentStone = getSquare(x + dir.x, y + dir.y);
      Point currentStoneCoords = new Point(x + dir.x, y + dir.y);
      // Check the .x and .y of each point in searched to see if they match the
      // new one
      if (searched.contains(currentStoneCoords)) {
        continue;
      }

      // If none of the stones has same coords as ones in previous pieces
      if (currentStone == Stone.NONE) {
        // If the piece is blank
        searched.add(currentStoneCoords);
        liberties += 1;
      }
      // If the stone is the same as the current player
      else if (currentStone == player) {
        // If the piece is allied and not in previous pieces
        // Add the liberties of that piece to this one
        searched.add(currentStoneCoords);
        liberties += libertyCount(currentStoneCoords.x, currentStoneCoords.y, player, searched);
      }
    }
    return liberties;
  }

  // Gets the amount of open spaces around the given stone
  private int libertyCount(int x, int y, Stone player) {
    return libertyCount(x, y, player, previousPieces);
  }

  // Returns true if the x and y values are in the board, false if not
  private boolean inBounds(int x, int y) {
    int boardSize = getBoardSize();
    if ((x >= 0 && x < boardSize) && (y >= 0 && y < boardSize)) {
      return true;
    } else {
      return false;
    }
  }

  // isGameOver returns true if the game is finished
  public boolean isGameOver() {
    return (winner != Stone.NONE);
  }

  // addCaptures adds one to captures of the right colour based on piece being
  // captured
  private void addCaptures(int x, int y) {
    // If the piece getting captured is black
    if (getSquare(x, y) == Stone.BLACK) {
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
    if (currentTurn != player) {
      return;
    }
    successivePasses++;
    if (successivePasses == 2) {
      winner = calculateWinner();
    }
    nextTurn();
  }

  // resign forfeits the game
  public void resign(Stone player) {
    winner = (player == Stone.BLACK) ? Stone.WHITE : Stone.BLACK;
  }

  private void nextTurn() {
    currentTurn = (currentTurn == Stone.BLACK ? Stone.WHITE : Stone.BLACK);
  }

  // playStone places a stone at (x,y)
  public boolean playStone(int x, int y, Stone player) {
    if (!isLegal(x, y, player)) {
      return false;
    }

    turnNumber++;
    successivePasses = 0;
    board[x][y] = player;
    // For each adjacent piece
    for (Point dir : cardinal) {
      if (!inBounds(x + dir.x, y + dir.y))
        continue;
      // Check the liberty of the adjacent pieces as the opponent
      ArrayList<Point> group = new ArrayList<>();
      // If the adjacent piece is a gothello piece and has 0 liberties
      if (inGoQuad(x + dir.x, y + dir.y) && getSquare(x + dir.x, y + dir.y) != player
          && (libertyCount(x + dir.x, y + dir.y, otherPlayer(player), group) == 0)) {
        // For each piece that the stone being removed checked

        // for (Point capture : captures) {

        Stone colourCaptured = getSquare(x + dir.x, y + dir.y);
        for (Point stone : group) {
          if (inGoQuad(stone.x, stone.y) && colourCaptured == getSquare(stone.x, stone.y)) {
            addCaptures(stone.x, stone.y);
            // Remove the stone
            board[stone.x][stone.y] = Stone.NONE;
          }
        }
      }

    }

    // For each value in the toFlip list
    for (Point stone : toFlip) {
      board[stone.x][stone.y] = player;

      // For each piece adjacent to the piece being flipped
      for (Point dir : cardinal) {
        if (!inBounds(stone.x + dir.x, stone.y + dir.y))
          continue;

        // If the piece has no liberties
        ArrayList<Point> group = new ArrayList<>();
        if ((libertyCount(stone.x + dir.x, stone.y + dir.y, otherPlayer(player), group) == 0)) {

          Stone colourCaptured = getSquare(stone.x + dir.x, stone.y + dir.y);
          // For each piece that the stone shares a colour with
          for (int j = 1; j < group.size(); j++) {
            Point groupStone = group.get(j);
            // If the current stone shares a colour with the stone to be removed and is in
            // the go quadrant
            if (inGoQuad(groupStone.x, groupStone.y) && getSquare(groupStone.x, groupStone.y) == colourCaptured) {
              // Add one to the captures
              addCaptures(groupStone.x, groupStone.y);
              // Remove the the piece
              board[groupStone.x][groupStone.y] = Stone.NONE;
            }
          }
          // If the stone is in the go quadr ant and isn't the same as the player
          if (inGoQuad(stone.x + dir.x, stone.y + dir.y) && (getSquare(stone.x + dir.x, stone.y + dir.y) != player)) {
            addCaptures(stone.x + dir.x, stone.y + dir.y);
            board[stone.x + dir.x][stone.y + dir.y] = Stone.NONE;
          }
        }
      }

    }
    nextTurn();
    return true;
  }

}
