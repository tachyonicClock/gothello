import React from 'react';
import Cell from './Cell'

function Board(props) {
  var gridSize = props.gridSize
  var board = props.board
  const displayGrid = []

  // Populate the board with cells
  for (let y = 0; y < gridSize; y++) {
    for (let x = 0; x < gridSize; x++) {
      var cellStyle = {
          width: "calc("+(100/gridSize) + "%)",
         paddingBottom: "calc("+(100/gridSize) + "% - 3px)"}

      // Hide border on edges. This gives a nicer looking border
      var transparent = "rgba(255,255,255,.5)"
      if (x===0) cellStyle.borderLeftColor = transparent
      if (y===0) cellStyle.borderTopColor = transparent
      if (x===gridSize-1) cellStyle.borderRightColor = transparent
      if (y===gridSize-1) cellStyle.borderBottomColor = transparent

      displayGrid.push(
      <Cell style={cellStyle} x={x} y={y} legal={true} stone={"black"} onclick={props.onclick}></Cell>)
    }
  }
  return (
  <div className="Board">
    <div className="Divider horizontal"/>
    <div className="Divider vertical"/>
    <div className="Padding">
    <div className="Inner">
      {displayGrid}
    </div>
    </div>
  </div>)
}

export default Board;