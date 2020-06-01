import React from 'react';
import { Paper, Button } from '@material-ui/core';
import logo from '../logo.svg'
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import SkipNext from '@material-ui/icons/SkipNext';
import ArrowBack from '@material-ui/icons/ArrowBack';
import VolumeOff from '@material-ui/icons/VolumeOff';
import { makeStyles } from '@material-ui/core/styles';
import Grid from '@material-ui/core/Grid';
import Typography from '@material-ui/core/Typography';
import Slider from '@material-ui/core/Slider';
import VolumeDown from '@material-ui/icons/VolumeDown';
import VolumeUp from '@material-ui/icons/VolumeUp';



function GameMenu(props) {
  return (
      <Paper className="GameMenu">
        <img className={"Logo"} src={logo} alt="Gothello the game" width="100%"></img>
        <Grid className={"Controls"} container spacing={2}>
          <Grid item xs={12} alignContent={"center"}>
            <h2 style={{"textAlign": "center"}}>Your turn</h2>
          </Grid>
        </Grid>
        <Grid className={"Controls"} container spacing={2}>
          <Grid item>
            <Button
              variant="contained"
              color="primary"
              startIcon={<ArrowBack/>}
            >
              Resign
            </Button>
          </Grid>
          <Grid item>
            <Button color="default" startIcon={<SkipNext/>}>
              Pass
            </Button>
          </Grid>

          <Grid item xs >
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