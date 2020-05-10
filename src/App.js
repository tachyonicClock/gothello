import React from 'react';
import {
  BrowserRouter as Router,
  Switch,
  Route,
} from "react-router-dom";
import Game from './Routes/Game';
import Home from './Routes/Home';
import { ThemeProvider } from '@material-ui/styles';
import { createMuiTheme } from '@material-ui/core/styles';

const theme = createMuiTheme({
  palette: {
    primary: {
      main: '#5A473A',
    },
    secondary: {
      light: '#0066ff',
      main: '#943C00',
      contrastText: '#ffcc00',
    },
    contrastThreshold: 3,
    tonalOffset: 0.2,
  },
  typography: {
    fontFamily: [
      '"Comfortaa"',
      "cursive"
    ]
  }
});

// Routes the user to the right place and applies the theme
function App() {
  return (
    <ThemeProvider theme={theme}>
      <Router>
        <Switch>
          <Route path="/game">
            <Game></Game>
          </Route>
          <Route path="/">
            <Home></Home>
          </Route>
        </Switch>
      </Router>
    </ThemeProvider>

  );
}

export default App;
