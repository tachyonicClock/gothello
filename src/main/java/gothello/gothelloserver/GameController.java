package gothello.gothelloserver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {
	
	// allGames contains every game both public and private
	private static final ConcurrentHashMap<Long, Game> allGames = new ConcurrentHashMap<Long, Game>();
	
	// openGames is a list of games that are open for a client to connect to
	private static final ConcurrentLinkedQueue<Game> openGames = new ConcurrentLinkedQueue<Game>();
	
	/**
	* This endpoint will create a new game and return its game ID. This lets a
	* client create a game if no open game exists. If it is a public game then it
	* can be connected to through `/game/join` . If it is private the game link
	* must be shared manually. This lets users play with who they want to by
	* sharing the link.
	*/
	@GetMapping("/api/v0/game/new")
	public Response newGame(@RequestParam(value = "type", defaultValue = "public") String gameType) {
		Game game = new Game(Game.typeFromString(gameType));
		allGames.put(game.getId(), game);
		
		// Add game to open games if the game is open (public and not full)
		if (game.getOpen()) {
			openGames.add(game);
		}
		return game;
	}
	
	/**
	* This endpoint will return a game that is open. This allows the client to
	* connect with someone waiting for a game. If it does not find a game it
	* returns an error
	*/
	@GetMapping("/api/v0/game/join")
	public Response joinGame() {
		if (openGames.size() == 0) {
			return new JSONError("No game found");
		}
		// Move the top game to the back of the queue and return it
		Game game = openGames.remove();
		openGames.add(game);
		return game;
	}
}