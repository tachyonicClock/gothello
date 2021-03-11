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
      <img className={"Logo"} src={logo} alt="Gothello the game" />

      {/* Prompt */}
      <Grid container spacing={2}>
        <Grid item xs={12}>
            <h2 
            className={"Prompt " + (props.stone? props.stone.toLowerCase() : "")}
            style={{ "textAlign": "center" }}>{props.prompt}</h2>
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
              <Slider step={0.1} min={0} max={1} value={props.volume} onChange={props.volumeChange} aria-labelledby="continuous-slider" />
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