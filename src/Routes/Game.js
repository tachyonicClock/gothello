import React, { useState, useEffect } from 'react';
import Grid from '@material-ui/core/Grid';
import Board from '../Board/Board'
import GameMenu from '../GameMenu/GameMenu';
import { useParams } from 'react-router-dom';
import { SERVER_URL_WS } from '../Config';
import { withSnackbar } from 'notistack';


function Game(props) {
  let { gameId } = useParams();
  let [ws, setWs] = useState(null)
  let [error, setError] = useState(false)
  let [loading, setLoading] = useState(true)
  var [board, setBoard] = useState(null)

  // This is a react hook it takes care of the websocket and their messages
  useEffect(() => {
    if (ws == null) {
      setWs(new WebSocket(SERVER_URL_WS + "/game/" + gameId + "/socket"))
      return
    }
    // Report Error lets the user know that something went wrong
    var reportError = e => {
      props.enqueueSnackbar("Connection failed, try refreshing", { variant: 'error' })
      setError(true)
    }
    ws.onclose = reportError
    ws.onerror = reportError

    // Handles the messages that get sent
    ws.onmessage = e => {
      var msg = JSON.parse(e.data)
      switch (msg.messageType) {
        // Something went wrong!
        case "error":
          props.enqueueSnackbar(msg.errorMessage, { variant: 'error' });
          setError(true)
          break;

        // We set the local state to match that of the server
        case "state":
          setBoard(msg.board)
          if (error) setError(false)
          if (loading) setLoading(false)
          break;

        default:
          console.warn(msg)
          break;
      }
    }
  }, [ws, gameId, props, loading, error])

  // CellClick tells the server that the player wants to play a stone
  function cellClick(e) {
    ws.send(JSON.stringify(
      {
        "messageType": "playStone",
        "row": e.cell.y,
        "col": e.cell.x
      }))
  }

  // Displays the game board and game menu using responsive design
  return (
    <Grid container spacing={3} justify='center'>
      <Grid item xs={12} md={4} lg={4}>
        <GameMenu></GameMenu>
      </Grid>
      <Grid item xs={12} sm={11} md={8} lg={6}>
        <Board onClick={cellClick}
          board={board}
          loading={loading}
          error={error}></Board>
      </Grid>
    </Grid>
  );
}

export default withSnackbar(Game);
