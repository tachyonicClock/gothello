import React from 'react';
import { Grid, Button } from '@material-ui/core';
import { useHistory } from 'react-router-dom';

function About(props) {
  const history = useHistory();

  return (
    <Grid container
      spacing={3}
      align="center"
      justify="center"
      direction="column">
      <Grid item>
        <Grid item xs={12} md={6} >
          <div className={"About"} style={{"textAlign": "left"}}>
            <h2>About</h2>
            <p>Gothello was a 6 week part time project for engineering and computer science students at the University of Waikato.  <a href="https://www.waikato.ac.nz/" className="url">https://www.waikato.ac.nz/</a>. </p>
            <p>It was created by a two person team with the oversight of a project manager:</p>
            <ul>
              <li>Anton Lee (Server, Client, Interface Design)</li>
              <li>Max Bracken (Game Logic)</li>
              <li>Blake Akapita (Project Manager)</li>
            </ul>
            <p>The main technologies used were:</p>
            <ul>
              <li>Client - React JS (<a href="https://reactjs.org/" className="url">https://reactjs.org/</a>)</li>
              <li>Server/Game Logic - Java (Spring, <a href="https://spring.io/" className="url">https://spring.io/</a>)</li>
            </ul>
            <p>&nbsp;</p>
            <h5>SFX</h5>
            <ul>
              <li><a href="https://freesound.org/s/437484/" className="url">https://freesound.org/s/437484/</a> Sound by BiancaBothaPure CC BY-NC 3.0</li>
            </ul>
            <Button onClick={()=>{history.push("/")}} >back</Button>
          </div>
        </Grid>
      </Grid>
    </Grid>
  )
}

export default About;
