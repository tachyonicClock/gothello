package gothello.gothelloserver;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gothello.gothelloserver.messages.Message;
import gothello.gothelloserver.messages.ErrorMessage;

import org.slf4j.Logger;

@CrossOrigin
@RestController
@RequestMapping("/api/v0/game")
public class HTTPHandler {
	Logger log = LoggerFactory.getLogger(HTTPHandler.class);

	/**
	 * This endpoint will create a new game and return its game ID. This lets a
	 * client create a game if no open game exists. If it is a public game then it
	 * can be connected to through `/game/join` . If it is private the game link
	 * must be shared manually. This lets users play with who they want to by
	 * sharing the link.
	 */
	@GetMapping("/new")
	public Message newGame(@RequestParam(value = "type", defaultValue = "public") String gameType) {
		Game game = new Game(Game.typeFromString(gameType));
		App.allGames.put(game.id, game);

		// Add game to open games if the game is open (public and not full)
		if (game.getOpen()) {
			App.openGames.add(game);
		}

		log.info("[{}] '/game/new' create game", game.id);
		return game;
	}

	/**
	 * This endpoint will return a game that is open. This allows the client to
	 * connect with someone waiting for a game. If it does not find a game it
	 * returns an error
	 */
	@GetMapping("/join")
	public Message joinGame() {
		if (App.openGames.size() == 0) {
			log.warn("'/game/join' Game not found, try making one");
			return new ErrorMessage("Game not found, try making one");
		}

		Game game = App.openGames.remove();
		if (!game.getOpen()) {
			return joinGame();
		}
		// Move the top game to the back of the queue and return it
		App.openGames.add(game);

		log.info("[{}] '/game/join' join game", game.id);
		return game;
	}

	// getGame gets the game based on its id
	@GetMapping("/{id}")
	public Message getGame(@PathVariable("id") int id) {
		Game game  = App.allGames.get(id);
		if (game == null) {
			return new ErrorMessage("Game not found, did you use the correct ID?");
		}
		return game;
	}

	// getState gets a game's state based on its id
	@GetMapping("/{id}/state")
	public Message getState(@PathVariable("id") int id) {
		Game game  = App.allGames.get(id);
		if (game == null) {
			return new ErrorMessage("Game not found, did you use the correct ID?");
		}
		return game.gameState();
	}
}
