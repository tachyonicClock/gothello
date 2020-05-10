import React, { useState } from 'react';
import Grid from '@material-ui/core/Grid';
import Board from '../Board/Board'
import GameMenu from '../GameMenu/GameMenu';


function Game() {
  // Initialize Game Board
  var [board, setBoard] = useState(
    [
    ['none', 'none', 'none', 'none','none', 'none', 'none', 'none'],
    ['none', 'black', 'white', 'none','none', 'none', 'none', 'none'],
    ['none', 'white', 'black', 'none','none', 'none', 'none', 'none'],
    ['none', 'none', 'none', 'none','none', 'none', 'none', 'none'],
    ['none', 'none', 'none', 'none','none', 'none', 'none', 'none'],
    ['none', 'none', 'none', 'none','none', 'white', 'black', 'none'],
    ['none', 'none', 'none', 'none','none', 'black', 'white', 'none'],
    ['none', 'none', 'none', 'none','none', 'none', 'none', 'none']
  ])

  function cellClick(e) {
    const stoneTypes = ['none', 'black', 'white']
    var newBoard = board.slice();
    var cell = newBoard[e.cell.x][e.cell.y]
    newBoard[e.cell.x][e.cell.y] = stoneTypes[(stoneTypes.indexOf(cell)+1) %3]
    setBoard(newBoard)
  }

  return (
    <Grid container spacing={3} justify='center'>
      <Grid item xs={12} md={4} lg={4}>
        <GameMenu></GameMenu>
      </Grid>
      <Grid item xs={12} sm={11} md={8} lg={6}>
        <Board gridSize={board.length} onClick={cellClick} board={board}></Board>
      </Grid>
    </Grid>
  );
}

export default Game;
