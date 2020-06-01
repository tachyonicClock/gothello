import React from 'react';
import { Paper, Button } from '@material-ui/core';
import logo from '../logo.svg'
import SkipNext from '@material-ui/icons/SkipNext';
import ArrowBack from '@material-ui/icons/ArrowBack';
import Grid from '@material-ui/core/Grid';
import Slider from '@material-ui/core/Slider';
import VolumeDown from '@material-ui/icons/VolumeDown';
import VolumeUp from '@material-ui/icons/VolumeUp';

// GameMenu offers controls and displays a prompt
function GameMenu(props) {
  return (
    <Paper className="GameMenu">
      {/* Logo */}
      <img className={"Logo"} src={logo} alt="Gothello the game" width="100%" />

      {/* Prompt */}
      <Grid container spacing={2}>
        <Grid item xs={12}>
          <h2 style={{ "textAlign": "center" }}>{props.prompt}</h2>
        </Grid>
      </Grid>

      {/* Controls */}
      <Grid className={"Controls"} container spacing={2}>
        <Grid item>
          <Button
            onClick={props.resignGame}
            variant="contained"
            color="primary"
            startIcon={<ArrowBack />}>
            Resign
          </Button>
        </Grid>
        <Grid item>
          <Button onClick={props.passTurn} color="default" startIcon={<SkipNext />}>
            Pass
          </Button>
        </Grid>
        <Grid item xs md={6} >
          <Grid item container spacing={2}>
            <Grid item>
              <VolumeDown />
            </Grid>
            <Grid item xs>
              <Slider aria-labelledby="continuous-slider" />
            </Grid>
            <Grid item>
              <VolumeUp />
            </Grid>
          </Grid>
        </Grid>
      </Grid>
    </Paper>)
}

export default GameMenu;