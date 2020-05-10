import React from 'react';
import logo from '../logo.svg'
import Grid from '@material-ui/core/Grid'
import { Button } from '@material-ui/core';
import {Link} from "react-router-dom";

// Home is the landing page of the site
function Home(props) {
  return (
    <Grid container
          spacing={3}
          align="center"
          justify="center"
          direction="column">
      <Grid item>
      <Grid item xs={6}>
        <div className={"Splash"}>
        <img className={"Logo"} src={logo} alt="Gothello the game" width="100%"></img>
        <p>A legendary mashup of Go and Othello</p>
        <Button component={Link} to="/game" variant="outlined" color="secondary" >Play game</Button>
        </div>
      </Grid>
      </Grid>
    </Grid>
  )
}

export default Home;