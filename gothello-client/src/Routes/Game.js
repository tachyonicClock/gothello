import React, { useState, useEffect } from 'react';
import Grid from '@material-ui/core/Grid';
import Board from '../Board/Board'
import GameMenu from '../GameMenu/GameMenu';
import { useParams, useHistory } from 'react-router-dom';
import { SERVER_URL_WS } from '../Config';
import { withSnackbar } from 'notistack';

// https://freesound.org/s/437484/ Sound by BiancaBothaPure CC BY-NC 3.0
import placeSfx from '../Sound/place.mp3'
import useSound from 'use-sound';


function Game(props) {

  var { gameId } = useParams();
  const history = useHistory();
  var [ws, setWs] = useState(null)
  var [error, setError] = useState(false)
  var [loading, setLoading] = useState(true)
  var [gameState, setGameState] = useState(null)
  var [volume, setVolume] = useState(1)
  var [prompt, setPrompt] = useState("Waiting for player to join")
  const [playPlaceSfx] = useSound(placeSfx, {"volume": volume});
  
  // This is a react hook it takes care of the websocket and their messages
  useEffect(() => {
    if (ws == null) {
      setWs(new WebSocket(SERVER_URL_WS + "/game/" + gameId + "/socket"))
      return
    }
    // Report Error lets the user know that something went wrong
    var reportError = e => {
      if (error) return
      props.enqueueSnackbar("Connection failed, try refreshing", { variant: 'error' })
      setError(true)
    }
    var leaveGame = e => {
      props.enqueueSnackbar("Left game", { variant: 'info' })
    }
    ws.onerror = reportError
    ws.onclose = leaveGame

    // keepWSAlive keeps the websocket from disconnecting because of inactivity
    function keepWSAlive() {
      var timeout = 20000;  
      if (ws.readyState === ws.OPEN) ws.send('{"messageType":"keepAlive"}')
      setTimeout(keepWSAlive, timeout); 
    }
    keepWSAlive();

    // Handles the messages that get sent
    ws.onmessage = e => {
      var msg = JSON.parse(e.data)
      switch (msg.messageType) {

        // Something went wrong!
        case "status":
          props.enqueueSnackbar(msg.message, { variant: msg.variant.toLowerCase() });
          if (msg.variant === "ERROR") {
            console.error(msg.message)
            setError(true)
            ws.close()
          }
          break;

        // We set the local state to match that of the server
        case "state":
          setGameState(msg)
          playPlaceSfx()
          if (msg.yourTurn) {
            setPrompt("Your turn")
          } else {
            setPrompt("Their turn")
          }
          if (error) setError(false)
          if (loading) setLoading(false)
          break;
        // If the game is over we inform the winner
        case "gameOver":
          if (msg.isWinner) {
            setPrompt("You win, you did good")
          } else if (msg.winner === "DRAW") {
            setPrompt("Draw")
          } else {
            setPrompt("You lost, you'll get there")
          }
          break;
        default:
          console.warn(msg)
          break;
      }
    }

    // closes the websocket when they push the back button
    window.onpopstate  = (e) => {
      if (ws.readyState === ws.OPEN) ws.close()
    }
  }, [ws, gameId, props, loading, error, playPlaceSfx])


  // CellClick tells the server that the player wants to play a stone
  function cellClick(e) {
    if (ws.readyState === WebSocket.OPEN)
    ws.send(JSON.stringify(
      {
        "messageType": "playStone",
        "row": e.cell.y,
        "col": e.cell.x
      }))
  }

  // PassTurn tells the server that the player is passing there turn
  function passTurn() {
    console.log("Pass turn")
    if (ws.readyState === WebSocket.OPEN)
      ws.send(JSON.stringify({ "messageType": "pass" }))
  }

  // ResignGame tells the server that the player has forfeited
  function resignGame() {
    console.log("Resign Game")
    if (ws.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({ "messageType": "resign" }))
      ws.close();
    }
    history.push("/")
  }

  // Displays the game board and game menu using responsive design
  return (
    <Grid container spacing={3} justify='center'>
      <Grid item xs={12} md={4} lg={4}>
        <div className={"VerticalCenter"}>
          <GameMenu 
            volume={volume}
            volumeChange={(e, v)=>{setVolume(v)}}
            passTurn={passTurn}
            resignGame={resignGame}
            prompt={prompt}
            stone={(gameState? gameState.turn : null)}></GameMenu>
        </div>
      </Grid>
      <Grid item xs={12} sm={11} md={8} lg={6}>
        <div className={"VerticalCenter"}>
          <Board onClick={cellClick}
            board={(gameState? gameState.board : null)}
            lastMove={(gameState? gameState.lastMove : null)}
            loading={loading}
            error={error}></Board>
        </div>
      </Grid>
    </Grid>
  );
}

export default withSnackbar(Game);
