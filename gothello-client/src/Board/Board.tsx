import React from 'react';
import { Cell, Stone, charToStone, StoneStyle } from './Cell'
import { CircularProgress, Box, Typography } from '@material-ui/core';
import { SystemUpdate } from '@material-ui/icons';
import { JsxElement } from 'typescript';

// function Board(props) {
//   var board = props.board
//   var isGameInProgress = !props.error && !props.loading && board != null

//   // Loading returns a JSX object that displays a loading icon
//   function Loading() {
//     return (
//       <Box alignItems={"center"} justifyContent={"center"} height={0} paddingTop={"calc(50% - 4vw)"} textAlign={"center"}>
//         <CircularProgress size={"5vw"} color="secondary" />
//         <Typography variant={"h5"} color={"textSecondary"}>
//           Waiting for another player to join. <br /> So you can just chill.
//         </Typography>
//       </Box>
//     )
//   }

//   // Error returns a JSX object that communicates that an error has occurred
//   function Error() {
//     return (
//       <Box alignItems={"center"} justifyContent={"center"} height={0} paddingTop={"calc(50% - 50px)"} textAlign={"center"}>
//         <Typography variant={"h5"} color={"textSecondary"}>
//           Something went wrong. <br /> Pobody's Nerfect <br />
//           ¯\_(ツ)_/¯
//         </Typography>
//       </Box>
//     )
//   }

//   // BoardGrid returns the game board once the game is in progress
//   function BoardGrid() {
//     const displayGrid = []
//     var gridSize = props.board.length

//     // Populate the board with cells
//     for (let y = 0; y < gridSize; y++) {
//       for (let x = 0; x < gridSize; x++) {
//         var cellStyle = {
//           width: "calc(" + (100 / gridSize) + "%)",
//           paddingBottom: "calc(" + (100 / gridSize) + "% - 3px)"
//         }

//         // Hide border on edges. This gives a nicer looking border
//         var transparent = "rgba(255,255,255,0)"
//         if (x === 0) cellStyle.borderLeftColor = transparent
//         if (y === 0) cellStyle.borderTopColor = transparent
//         if (x === gridSize - 1) cellStyle.borderRightColor = transparent
//         if (y === gridSize - 1) cellStyle.borderBottomColor = transparent

//         var legal = false;
//         var stone = "NONE";
//         switch (board[y][x]) {
//           case "L":
//             legal = true
//             break;
//           case "B":
//             stone = "BLACK"
//             break;
//           case "W":
//             stone = "WHITE"
//             break;
//         }

//         displayGrid.push(
//           <Cell key={y * gridSize + x} stoneClass={""} legal={legal} style={cellStyle} x={x} y={y} stone={stone} onClick={props.onClick}></Cell>)
//       }
//     }
//     return displayGrid;
//   }

//   function xyToIndex(x, y) {
//     return x + y * 8
//   }


//   if (isGameInProgress) {
//     var board = BoardGrid()
//     var {flips, captures} = props.lastMove

//     flips.forEach(flips => {
//       var cell = board[xyToIndex(flips.x, flips.y)]
//       cell.props.stoneClass = "highlight"
//       console.log(cell)
//     });
//   }

//   // If the game is in progress we display the board else we show a message
//   return (
//     <div className="Board">
//       {isGameInProgress &&
//         <>
//           <div className="Divider horizontal" />
//           <div className="Divider vertical" />
//         </>
//       }
//       <div className="Padding">
//         <div className="Inner">
//           {props.error && Error()}
//           {(props.loading && !props.error) && Loading()}
//           {isGameInProgress && board}
//         </div>
//       </div>
//     </div>)
// }

interface BoardProps {
  board: any;
  lastMove: any;
  loading: Boolean;
  error: Boolean;
  onClick: Function;
}

export default class Board extends React.Component<BoardProps, {}> {

  gridSize: number = 8;

  constructor(props: BoardProps) {
    super(props)

  }


  renderOnLoading(): React.ReactNode {
    return (
      <Box alignItems={"center"} justifyContent={"center"} height={0} paddingTop={"calc(50% - 4vw)"} textAlign={"center"}>
        <CircularProgress size={"5vw"} color="secondary" />
        <Typography variant={"h5"} color={"textSecondary"}>
          Waiting for another player to join. <br /> So you can just chill.
        </Typography>
      </Box>
    )
  }

  renderOnError(): React.ReactNode {
    return (
      <Box alignItems={"center"} justifyContent={"center"} height={0} paddingTop={"calc(50% - 50px)"} textAlign={"center"}>
        <Typography variant={"h5"} color={"textSecondary"}>
          Something went wrong. <br /> Pobody's Nerfect <br />
          ¯\_(ツ)_/¯
        </Typography>
      </Box>
    )
  }

  xyToIndex(x: number, y: number): number {
    return x + y * 8
  }

  renderGrid(boardStones: Array<Stone>, stoneStyles: Array<StoneStyle>, legalSquares: Array<boolean>): Array<JSX.Element> {

    var grid: Array<JSX.Element> = []
    // Populate the board with cells
    for (let y = 0; y < this.gridSize; y++) {
      for (let x = 0; x < this.gridSize; x++) {
        var i = this.xyToIndex(x, y)
        var cellStyle: any = {
          width: "calc(" + (100 / this.gridSize) + "%)",
          paddingBottom: "calc(" + (100 / this.gridSize) + "% - 3px)"
        }

        // Hide border on edges. This gives a nicer looking border
        var transparent = "rgba(255,255,255,0)"
        if (x === 0) cellStyle.borderLeftColor = transparent
        if (y === 0) cellStyle.borderTopColor = transparent
        if (x === this.gridSize - 1) cellStyle.borderRightColor = transparent
        if (y === this.gridSize - 1) cellStyle.borderBottomColor = transparent

        grid.push(
          <Cell key={i} x={x} y={y}
            style={cellStyle}
            stone={boardStones[i]}
            legal={legalSquares[i]}
            stoneStyle={stoneStyles[i]}
            onClick={this.props.onClick} />)
      }
    }
    return grid;
  }

  renderGame(): React.ReactNode {
    const displayGrid = []
    var { board, lastMove } = this.props
    var boardStones: Array<Stone> = []
    var stoneStyle: Array<StoneStyle> = []
    var legalSquares: Array<boolean> = []

    // Populate the board with cells
    for (let x = 0; x < this.gridSize; x++) {
      for (let y = 0; y < this.gridSize; y++) {
        var legal = board[x][y] === "L";
        var stone = charToStone(board[x][y])
        boardStones.push(stone)
        legalSquares.push(legal)
        stoneStyle.push(StoneStyle.Normal)
      }
    }

    // Apply style to show last captures
    console.log(lastMove.flips)

    lastMove.flips.forEach( (flip:{x: number, y: number, stone: Stone}) => {
      var i = this.xyToIndex(flip.x, flip.y)
      stoneStyle[i] = StoneStyle.Highlight
    });

    lastMove.captures.forEach( (capture:{x: number, y: number, stone: Stone}) => {
      var i = this.xyToIndex(capture.x, capture.y)
      stoneStyle[i] = StoneStyle.Ghost
      boardStones[i] = capture.stone
    });

    var placed = lastMove.placement 
    if (placed) {
      var i = this.xyToIndex(placed.x, placed.y)
      stoneStyle[i] = StoneStyle.Glow
    }
    
    return this.renderGrid(boardStones, stoneStyle, legalSquares)

  }

  renderOuterBoard(inProgress: boolean, innerBoard: React.ReactNode): React.ReactNode {
    return (
      <div className="Board">
        {inProgress &&
          <>
            <div className="Divider horizontal" />
            <div className="Divider vertical" />
          </>
        }
        <div className="Padding">
          <div className="Inner">
            {innerBoard}
          </div>
        </div>
      </div>)
  }


  render(): React.ReactNode {
    var { error, loading } = this.props
    if (error) {
      return this.renderOuterBoard(false, this.renderOnError())
    } else if (loading) {
      return this.renderOuterBoard(false, this.renderOnLoading())
    } else {
      return this.renderOuterBoard(true, this.renderGame())
    }
  }
}