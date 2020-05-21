package gothello.gothelloserver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

	// allGames contains every game both public and private
	public static final ConcurrentHashMap<Integer, Game> allGames = new ConcurrentHashMap<Integer, Game>();

	// openGames is a list of games that are open for a client to connect to
	public static final ConcurrentLinkedQueue<Game> openGames = new ConcurrentLinkedQueue<Game>();

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}
