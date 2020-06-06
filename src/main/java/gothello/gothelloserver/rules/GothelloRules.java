package gothello.gothelloserver.rules;
import java.util.ArrayList;
/**
 * GothelloRules is an implementation of rules that implements the rules for
 * Gothello
 */
public class GothelloRules implements Rules {
  Stone[][] board = new Stone[8][8];
  private Stone currentTurn = Stone.BLACK;
  private int successivePasses = 0;
  private Stone winner = Stone.NONE;
  //Class to store the x,y coordinates on the grid
  private class Point{
    int x;
    int y;
    Point(int xCoord, int yCoord){
      x = xCoord;
      y = yCoord;
    }
  }
  //Set up lists used by methods
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

  // getWinner returns the player who has won or Stone.NONE
  public Stone getWinner() {
    return winner;
  }
  private Stone calculateWinner(){
    int WhiteScore = getScore(Stone.WHITE);
    int BlackScore = getScore(Stone.BLACK);
    if (WhiteScore > BlackScore) {
      return Stone.WHITE;
    }else if (WhiteScore < BlackScore){
      return Stone.BLACK;
    }else {
      return Stone.DRAW;
    }
  }

  // getScore returns the score of the specified player
  public int getScore(Stone player) {
    int score = 0;
    //For each square on the board
    for (int x = 0; x < board.length; x++) {
      for (int y = 0; y < board[x].length; y++) {
        //If the current square on the board is the same as the player add one to the score
        if(board[x][y] == player){
          score++;
        }
      }
    }
    if(player == Stone.BLACK){
      return score+blackCaptures;
    }
    else if(player == Stone.WHITE){
      return score+whiteCaptures;
    }
    else{
      return 0;
    }
  }

  // getBoardSize returns the size of a square board
  public int getBoardSize(){
    //Assuming board is square
    return board[0].length;
  }

  // isLegal returns true or false depending on if the square is a legal move
  // for the specified player
  public boolean isLegal(int x, int y, Stone player) {
    //Clear the toFlip list and previous pieces
    toFlip.clear();
    previousPieces.clear();
    if (isGameOver()) {
      return false;
    }
    int boardSize = getBoardSize();
    //If the point is in the board, the place is clear and it is the players turn
    if(inBounds(x,y) && board[x][y] == Stone.NONE && player == currentTurn){
      //Find all the pieces to be flipped
      //Dir stores the changes in x and y
      int[] dir = new int[2];
      //Left = 0, Left-Up = 1, Up = 2, Up-Right = 3, Right = 4, Down-Right = 5 Down = 6, Down-Left = 7
      for (int i = 0; i < 8; i++){
        //Finds the pieces to flip in the given direction
        switch(i){
            case 0:
              dir[0] = -1;
              dir[1] = 0;
              break;
            case 1:
              dir[0] = -1;
              dir[1] = -1;
              break;
            case 2:
              dir[0] = 0;
              dir[1] = -1;
              break;
            case 3:
              dir[0] = 1;
              dir[1] = -1;
              break;
            case 4:
              dir[0] = 1;
              dir[1] = 0;
              break;
            case 5:
              dir[0] = 1;
              dir[1] = 1;
              break;
            case 6:
              dir[0] = 0;
              dir[1] = 1;
              break;
            case 7:
              dir[0] = -1;
              dir[1] = 1;
              break;
          }
          //If new x and y are inside the board
          if (inBounds(x+dir[0], y+dir[1])){
            ArrayList<Point> checkResult = othelloCheck(x+dir[0], y+dir[1], dir, player);
            if (checkResult != null){
              toFlip.addAll(checkResult);   
            }           
          } 
      }
      //If point is in Othello Quadrant
      if ((x < boardSize/2 && y < boardSize/2) || (x >= boardSize/2 && y>= boardSize/2)){
        //If there are any pieces to flip
        if (toFlip.size() != 0){
          //Move is legal
          return true;
        }
      }
      //Else if piece is in Go quadrant
      else if((x >= boardSize/2 && y < boardSize/2) || (x < boardSize/2 && y >= boardSize/2)){
        //If the stone would have an open space
        previousPieces.clear();
        if (libertyCount(x, y, player) != 0){
          return true;
        }
        //If adjacent piece has 1 liberty then the move would cause that piece to be taken and become legal
        for (int i = 0; i < 4; i++){
          switch(i){
            case 0:
              dir[0] = -1;
              dir[1] = 0;
              break;
            case 1:
              dir[0] = 0;
              dir[1] = -1;
              break;
            case 2:
              dir[0] = 1;
              dir[1] = 0;
              break;
            case 3:
              dir[0] = 0;
              dir[1] = 1; 
              break;
          }
          //If adjacent piece is in the board
          if(inBounds(x+dir[0], y+dir[1])){
            previousPieces.clear();
            //If adjacent piece has one liberty and is opposing and is in go quadrant
            if ((getSquare(x + dir[0], y + dir[1]) != player) && libertyCount(x + dir[0], y + dir[1], ((player == Stone.BLACK) ? Stone.WHITE:Stone.BLACK)) == 1 && ((x+dir[0] >= boardSize/2 && y+dir[0] < boardSize/2) || (x+dir[0] < boardSize/2 && y+dir[1] >= boardSize/2))){
              return true;
            }
          }
        }
        //If one of the pieces that would make the move illegal would be othello flipped
        for (int i = 0; i < 4; i ++){
          switch(i){
            case 0:
              dir[0] = -1;
              dir[1] = 0;
              break;
            case 1:
              dir[0] = 0;
              dir[1] = -1;
              break;
            case 2:
              dir[0] = 1;
              dir[1] = 0;
              break;
            case 3:
              dir[0] = 0;
              dir[1] = 1;
              break;
          }
          if(inBounds(x+dir[0], y+dir[1])){
            //If adjacent piece is in Othello Quadrant and opposing
            if ((getSquare(x+dir[0], y+dir[1]) != player) && ((x+dir[0] < boardSize/2 && y+dir[1] < boardSize/2) || (x+dir[0] >= boardSize/2 && y+dir[1]>= boardSize/2))){
              //If the piece is in the toFlip list
              Point currentStoneCoords = new Point(x+dir[0], y+dir[1]);
              for(int k = 0; k < toFlip.size(); k++){
                if(toFlip.get(k).x == currentStoneCoords.x && toFlip.get(k).y == currentStoneCoords.y){
                  //Move is legal
                  return true;
                }
              }
            }
          }
        }
      }
    }
    //If the space if full already or isn't legal for another reason
    return false;
  }

  //Returns a list of the locations of the stones to be flipped
  //Passed the location to begin check, direction to check, and the current player
  private ArrayList<Point> othelloCheck(int x, int y, int[] dir, Stone player){
    ArrayList<Point> inBetween =  new ArrayList<Point>();
    int boardSize = getBoardSize();
    Stone currentStone = getSquare(x,y);
    //If player of square checking isn't current player
    if (currentStone != player && currentStone != Stone.NONE && inBounds(x+dir[0], y+dir[1])){
      ArrayList<Point> toAdd = new ArrayList<Point>();
      //Call the method again and set toAdd to the return of that method
      toAdd = othelloCheck(x+dir[0], y+dir[1], dir, player);
      //If method returned null
      if (toAdd == null){
          return null;
      }
      else{
        //If the current stone is flippable (in othello quadrant) add it to the list
        if((x < boardSize/2 && y < boardSize/2) || (x >= boardSize/2 && y>= boardSize/2)){
          //Create a point storing the x and y of stone to be added
          Point currentStoneCoords = new Point(x,y);
          inBetween.add(currentStoneCoords);
        }
        inBetween.addAll(toAdd);
        return inBetween;
      }
    }
    //If player of square checking is current player
    else if(currentStone == player){
      return inBetween;
    }
    //If square is empty
    else{
      return null;
    }
  }
  
  //Gets the amount of open spaces around the given stone 
  private int libertyCount(int x, int y, Stone player){
    int[] dir = new int[2];
    int liberties = 0;
    int numDifferent = 0;
    //Add the current piece to previous pieces
    previousPieces.add(new Point(x,y));
    //For each adjacent piece Left = 0, Up = 1, Right = 2, Down = 3
    for (int i = 0; i < 4; i++){
      switch(i){
        case 0:
          dir[0] = -1;
          dir[1] = 0;
          break;
        case 1:
          dir[0] = 0;
          dir[1] = -1;
          break;
        case 2:
          dir[0] = 1;
          dir[1] = 0;
          break;
        case 3:
          dir[0] = 0;
          dir[1] = 1;
          break;
      }
      //Check that the square is in the grid
      if (inBounds(x+dir[0], y+dir[1])){
        Stone currentStone = getSquare(x+dir[0], y+dir[1]);
        Point currentStoneCoords = new Point(x+dir[0],y+dir[1]);
        //Check the .x and .y of each point in previousPieces to see if they match the new one 
        numDifferent = 0;
        for (int k = 0; k < previousPieces.size(); k++){
          //Write the coordinates as strings to be compared
          String prevPiecesTogether = String.valueOf(previousPieces.get(k).x) + String.valueOf(previousPieces.get(k).y);
          String currentCoordTogether = String.valueOf(currentStoneCoords.x) + String.valueOf(currentStoneCoords.y);
          //if ((previousPieces.get(k).x != currentStoneCoords.x) && (previousPieces.get(k).y != currentStoneCoords.y)){
          if (!prevPiecesTogether.equals(currentCoordTogether)){
            numDifferent++;
          }
          else{
            break;
          }
        }
        //If none of the stones has same coords as ones in previous pieces
        if(currentStone == Stone.NONE && numDifferent == previousPieces.size()){
          //If the piece is blank
          liberties += 1;
          previousPieces.add(currentStoneCoords);
        }
        //If the stone is the same as the current player
        else if (currentStone == player && numDifferent == previousPieces.size()){
          //If the piece is allied and not in previous pieces
          previousPieces.add(currentStoneCoords);
          //Add the liberties of that piece to this one
          liberties += libertyCount(currentStoneCoords.x, currentStoneCoords.y, player);
        }
      } 
    }
    return liberties;
  }

  //Returns true if the x and y values are in the board, false if not
  private boolean inBounds(int x, int y){
    int boardSize = getBoardSize();
    if ((x >= 0 && x < boardSize) && (y >= 0 && y < boardSize)){
      return true;
    }
    else{
      return false;
    }
  }

  // isGameOver returns true if the game is finished
  public boolean isGameOver() {
    return (winner != Stone.NONE);
  }

  //addCaptures adds one to captures of the right colour based on piece being captured
  private void addCaptures(int x, int y){
    //If the piece getting captured is black
    if(getSquare(x, y) == Stone.BLACK){
      //Add one to white captures
      whiteCaptures++;
    }
    //
    else{
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
    successivePasses ++;
    if (successivePasses == 2) {
      winner = calculateWinner();
    }
    nextTurn();
  }

  // resign forfeits the game
  public void resign(Stone player) {
    winner = (player == Stone.BLACK)? Stone.WHITE : Stone.BLACK; 
  }

  private void nextTurn(){
    currentTurn = (currentTurn == Stone.BLACK ? Stone.WHITE : Stone.BLACK);
  }
  // playStone places a stone at (x,y)
  public boolean playStone(int x, int y, Stone player) {
    int[] dir = new int[2];
    int boardSize = getBoardSize();
    if (!isLegal(x, y, player)){
      return false;
    }
    successivePasses = 0;
    board[x][y] = player;
    //For each adjacent piece
    for(int i = 0; i<4; i++){
      switch(i){
        case 0:
          dir[0] = -1;
          dir[1] = 0;
          break;
        case 1:
          dir[0] = 0;
          dir[1] = -1;
          break;
        case 2:
          dir[0] = 1;
          dir[1] = 0;
          break;
        case 3:
          dir[0] = 0;
          dir[1] = 1;
          break;
      }
      if(inBounds(x+dir[0],y+dir[1])){
        //Check the liberty of the adjacent pieces as the opponent
        previousPieces.clear();
        //If the adjacent piece is a gothello piece and has 0 liberties
        if (((x+dir[0] >= boardSize/2 && y+dir[1] < boardSize/2) || (x+dir[0] < boardSize/2 && y+dir[1]>= boardSize/2)) && (getSquare(x+dir[0], y+dir[1]) != player) && (libertyCount(x+dir[0], y+dir[1], ((player == Stone.BLACK) ? Stone.WHITE:Stone.BLACK)) == 0)){
          //For each piece that the stone being removed checked
          for(int k = 1; k < previousPieces.size()-1; k++){
            //If it is the same type as the stone being removed and is in go quadrant as well
            if(((previousPieces.get(k).x >= boardSize/2 && previousPieces.get(k).y < boardSize/2) || (previousPieces.get(k).x < boardSize/2 && previousPieces.get(k).y >= boardSize/2)) && getSquare(x+dir[0],y+dir[1]) == getSquare(previousPieces.get(k).x,previousPieces.get(k).y)){
              addCaptures(previousPieces.get(k).x, previousPieces.get(k).y);
              //Remove the stone
              board[previousPieces.get(k).x][previousPieces.get(k).y] = Stone.NONE;
            }
          }
          addCaptures(x+dir[0],y+dir[1]);
          board[x+dir[0]][y+dir[1]] = Stone.NONE;
        }

      }
    }
    //For each value in the toFlip list
    for(int i = 0; i < toFlip.size(); i++){
      if(player == Stone.WHITE){
        board[toFlip.get(i).x][toFlip.get(i).y] = Stone.WHITE;
      }
      else if (player == Stone.BLACK){
        board[toFlip.get(i).x][toFlip.get(i).y] = Stone.BLACK;
      }
      //For each piece adjacent to the piece being flipped
      for (int k = 0; k < 4; k++){
        switch(k){
          case 0:
            dir[0] = -1;
            dir[1] = 0;
            break;
          case 1:
            dir[0] = 0;
            dir[1] = -1;
            break;
          case 2:
            dir[0] = 1;
            dir[1] = 0;
            break;
          case 3:
            dir[0] = 0;
            dir[1] = 1;
            break;
        }
        if(inBounds(toFlip.get(i).x+dir[0],toFlip.get(i).y+dir[1])){
          //If the piece has no liberties
          previousPieces.clear();
          if((libertyCount((toFlip.get(i).x)+dir[0], (toFlip.get(i).y)+dir[1], (player == Stone.BLACK) ? Stone.WHITE:Stone.BLACK) == 0)){
            //For each piece that the stone shares a colour with
            for(int j = 1; j < previousPieces.size()-1; j++){
              //If the current stone shares a colour with the stone to be removed and is in the go quadrant
              if(((previousPieces.get(j).x >= boardSize/2 && previousPieces.get(j).y < boardSize/2) || (previousPieces.get(j).x < boardSize/2 && previousPieces.get(j).y >= boardSize/2)) && getSquare(previousPieces.get(j).x, previousPieces.get(j).y) == getSquare(toFlip.get(i).x+dir[0], toFlip.get(i).y+dir[1])){
                //Add one to the captures
                addCaptures(previousPieces.get(j).x,previousPieces.get(j).y);
                //Remove the the piece
                board[previousPieces.get(j).x][previousPieces.get(j).y] = Stone.NONE;
              }
            }
            //If the stone is in the go quadrant
            if (((toFlip.get(i).x+dir[0] >= boardSize/2 && toFlip.get(i).y+dir[1] < boardSize/2) || (toFlip.get(i).x+dir[0] < boardSize/2 && toFlip.get(i).y+dir[1] >= boardSize/2))){
              addCaptures(toFlip.get(i).x+dir[0], toFlip.get(i).y+dir[1]);
              board[toFlip.get(i).x+dir[0]][toFlip.get(i).y+dir[1]] = Stone.NONE;
            }
          }
        }
      }
    }
    nextTurn();
    return true;
  } 

}
