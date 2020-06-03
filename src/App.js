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
import { SnackbarProvider } from 'notistack';
import { SERVER_URL, SERVER_URL_WS } from './Config';

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
  console.log("Connecting to endpoints", SERVER_URL, SERVER_URL_WS)
  return (
    <SnackbarProvider>
      <ThemeProvider theme={theme}>
        <Router>
          <Switch>
            <Route path="/game/:gameId">
              <Game></Game>
            </Route>
            <Route path="/">
              <Home></Home>
            </Route>
          </Switch>
        </Router>
      </ThemeProvider>
    </SnackbarProvider>
  );
}

export default App;
