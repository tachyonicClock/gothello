import React from 'react';
import Paper from '@material-ui/core/Paper';
import Grid from '@material-ui/core/Grid';
import logo from './logo.svg';
import Board from './Board/Board'
import './App.css';
import GameMenu from './GameMenu/GameMenu';

function App() {
  return (
    <div>
      <Grid container spacing={3} justify='center'>
        <Grid item xs={12} md={4} lg={4}>
          <GameMenu></GameMenu>
        </Grid>
        <Grid item xs={12} sm={11} md={8} lg={6}>
          <Board gridSize={8}></Board>
        </Grid>
      </Grid>
    </div>
  );
}

export default App;
