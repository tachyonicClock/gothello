package gothello.gothelloserver;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gothello.gothelloserver.messages.Message;
import gothello.gothelloserver.exceptions.GameNotFound;
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
		switch (Game.GameType.fromString(gameType)) {
			case PRIVATE:
			    return MatchMaker.newPrivateGame();
			case PUBLIC:
				return MatchMaker.newPublicGame();
			case SINGLE_PLAYER:
				return MatchMaker.newSinglePlayerGame();
			default:
				return new ErrorMessage("Invalid type of game");
		}
	}

	/**
	 * This endpoint will return a game that is open. This allows the client to
	 * connect with someone waiting for a game. If it does not find a game it
	 * returns an error
	 */
	@GetMapping("/join")
	public Message joinGame() {
		try {
			return MatchMaker.getOpenGame();
		} catch (GameNotFound e) {
			return new ErrorMessage("Game not found, try making one");
		}
	}

	/**
	 * This endpoint will return a game that has a player that wants to vs a bot. 
	 * This allows a bot client to connect with someone waiting for a game. 
	 * If it does not find a game it returns an error
	 */
	@GetMapping("/botqueue")
	public Message botQueue() {
		try {
			return MatchMaker.getSinglePlayerGame();
		} catch (GameNotFound e) {
			return new ErrorMessage("No game found");
		}
	}

	// getGame gets the game based on its id
	@GetMapping("/{id}")
	public Message getGame(@PathVariable("id") int id) {
		try {
			return MatchMaker.getGame(id);
		} catch (GameNotFound e) {
			return new ErrorMessage("Game not found, did you use the correct ID?");
		}
	}

	// getState gets a game's state based on its id
	@GetMapping("/{id}/state")
	public Message getState(@PathVariable("id") int id) {
		try {
			return MatchMaker.getGame(id).gameState();
		} catch (GameNotFound e) {
			return new ErrorMessage("Game not found, did you use the correct ID?");
		}
	}
}
