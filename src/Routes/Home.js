import React, { useState } from 'react';
import logo from '../logo.svg'
import Grid from '@material-ui/core/Grid'
import { Button, TextField } from '@material-ui/core';
import { useHistory } from "react-router-dom";
import Axios from 'axios';
import { SERVER_URL } from '../Config';
import { withSnackbar } from 'notistack';

// Home is the landing page of the site
function Home(props) {
  const history = useHistory();
  var [gameId, setGameId] = useState("");

  function gotoGame(data) {
    if (data.messageType === "game") {
      history.push("/game/" + data.id)
    } else if (data.messageType === "error") {
      props.enqueueSnackbar(data.errorMessage, { variant: 'error' });
      console.warn(data)
    } else {
      console.error(data)
    }
  }

  function makeGame(gameType) {
    Axios.get(SERVER_URL + "/game/new?type=" + gameType).then(({ data }) => (gotoGame(data)))
  }

  function handleJoinGame() {
    Axios.get(SERVER_URL + "/game/join").then(({ data }) => {
      if (data.messageType === "status" && data.variant === "ERROR" ) {
        makeGame("public")
      } else {
        gotoGame(data)
      }
    })
  }

  function handleJoinById() {
    Axios.get(SERVER_URL + "/game/" + gameId).then(({ data }) => (gotoGame(data)))
  }

  return (
    <Grid container
      spacing={3}
      align="center"
      justify="center"
      direction="column">
      <Grid item>
        <Grid item xs={12} md={6}>
          <div className={"Splash"}>
            <img className={"Logo"} src={logo} alt="Gothello the game" width="100%"></img>
            <p>A legendary mashup of Go and Othello</p>
            <Button style={{ "margin": "5px" }} onClick={handleJoinGame} variant="outlined" color="secondary" >Join Game</Button>
            <Button style={{ "margin": "5px" }} onClick={() => { makeGame("private") }} variant="outlined" color="secondary" >Private Game</Button>
            <hr />
            <p>Join a private game by it's game ID</p>
            <TextField
              value={gameId}
              onChange={(e) => { setGameId(e.target.value) }}
              onKeyDown={(e) => { if (e.keyCode === 13) handleJoinById() }}
              label="Game id"
              type="number"
            />
            <Button style={{ "margin": "5px" }} onClick={handleJoinById} color="secondary" >Go</Button>
            <hr />
            <Button onClick={()=>(history.push("/about"))}>about game</Button>
          </div>
        </Grid>
      </Grid>
    </Grid>
  )
}

export default withSnackbar(Home);
