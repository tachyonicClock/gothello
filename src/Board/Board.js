import React from 'react';
import Cell from './Cell'
import { CircularProgress, Box, Typography } from '@material-ui/core';

function Board(props) {
  var board = props.board
  var isGameInProgress = !props.error && !props.loading && board != null

  // Loading returns a JSX object that displays a loading icon
  function Loading() {
    return (
      <Box alignItems={"center"} justifyContent={"center"} height={0} paddingTop={"calc(50% - 4vw)"} textAlign={"center"}>
        <CircularProgress size={"5vw"} color="secondary" />
        <Typography variant={"h5"} color={"textSecondary"}>
          Waiting for another player to join. <br /> So you can just chill.
        </Typography>
      </Box>
    )
  }

  // Error returns a JSX object that communicates that an error has occurred
  function Error() {
    return (
      <Box alignItems={"center"} justifyContent={"center"} height={0} paddingTop={"calc(50% - 50px)"} textAlign={"center"}>
        <Typography variant={"h5"} color={"textSecondary"}>
          Something went wrong. <br /> Pobody's Nerfect <br />
          ¯\_(ツ)_/¯
        </Typography>
      </Box>
    )
  }

  // BoardGrid returns the game board once the game is in progress
  function BoardGrid() {
    const displayGrid = []
    var gridSize = props.board.length

    // Populate the board with cells
    for (let y = 0; y < gridSize; y++) {
      for (let x = 0; x < gridSize; x++) {
        var cellStyle = {
          width: "calc(" + (100 / gridSize) + "%)",
          paddingBottom: "calc(" + (100 / gridSize) + "% - 3px)"
        }

        // Hide border on edges. This gives a nicer looking border
        var transparent = "rgba(255,255,255,0)"
        if (x === 0) cellStyle.borderLeftColor = transparent
        if (y === 0) cellStyle.borderTopColor = transparent
        if (x === gridSize - 1) cellStyle.borderRightColor = transparent
        if (y === gridSize - 1) cellStyle.borderBottomColor = transparent

        displayGrid.push(
          <Cell key={y * gridSize + x} style={cellStyle} x={x} y={y} stone={board[y][x]} onClick={props.onClick}></Cell>)
      }
    }
    return displayGrid;
  }

  // If the game is in progress we display the board else we show a message
  return (
    <div className="Board">
      {isGameInProgress &&
        <>
          <div className="Divider horizontal" />
          <div className="Divider vertical" />
        </>
      }
      <div className="Padding">
        <div className="Inner">
          {props.error && Error()}
          {(props.loading && !props.error) && Loading()}
          {isGameInProgress && BoardGrid()}
        </div>
      </div>
    </div>)
}

export default Board;