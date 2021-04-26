package gothello.gothelloserver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.LoggerFactory;

import gothello.gothelloserver.Game.GameType;
import gothello.gothelloserver.exceptions.GameNotFound;

import org.slf4j.Logger;

public class MatchMaker {

    public static final AtomicInteger totalGames = new AtomicInteger();
    public static final Logger log = LoggerFactory.getLogger(MatchMaker.class);
    public static final ConcurrentHashMap<Integer, Game> allGames = new ConcurrentHashMap<Integer, Game>();
    public static final ConcurrentLinkedQueue<Game> openGames = new ConcurrentLinkedQueue<Game>();
    public static final ConcurrentLinkedQueue<Game> singlePlayerGames = new ConcurrentLinkedQueue<Game>();

    public static Game newPrivateGame() {
        totalGames.incrementAndGet();
        Game game = new Game(GameType.PRIVATE);
        allGames.put(game.id, game);
        log.info("[{}] New Private Game", game.id);
        printStats();
        return game;
    }

    public static Game newPublicGame() {
        totalGames.incrementAndGet();
        Game game = new Game(GameType.PUBLIC);
        allGames.put(game.id, game);
        openGames.add(game);
        log.info("[{}] New Public Game", game.id);
        printStats();
        return game;
    }

    public static Game newSinglePlayerGame() {
        totalGames.incrementAndGet();
        Game game = new Game(GameType.SINGLE_PLAYER);
        allGames.put(game.id, game);
        singlePlayerGames.add(game);
        log.info("[{}] New Single Player Game", game.id);
        printStats();
        return game;
    }

    public static Game getGame(int id) throws GameNotFound {
        Game game = allGames.get(id);
        if (game == null) {
            throw new GameNotFound();
        }
        return game;
    }

    public static void removeFromQueue(int id) {
        try {
            Game game = getGame(id);
            switch (game.gameType) {
                case PUBLIC:
                    openGames.remove(game);
                    break;
                case SINGLE_PLAYER:
                    singlePlayerGames.remove(game);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // This simply means they have already been removed
        }
        log.info("[{}] Removed game from queue", id);

    }

    public static void deleteGame(int id) {
        try {
            Game game = getGame(id);
            switch (game.gameType) {
                case PUBLIC:
                    openGames.remove(game);
                    break;
                case SINGLE_PLAYER:
                    singlePlayerGames.remove(game);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // This simply means they have already been removed
        }

        allGames.remove(id);
        log.info("[{}] Removed Game", id);
    }

    public static Game getSinglePlayerGame() throws GameNotFound {
        if (singlePlayerGames.size() == 0)
            throw new GameNotFound();
        return singlePlayerGames.remove();
    }

    public static Game getOpenGame() throws GameNotFound {
        // Game not found
        if (openGames.size() == 0)
            throw new GameNotFound();

        Game game = openGames.remove();
        // Move the top game to the back of the queue for the next person to join
        openGames.add(game);
        return game;
    }

    public static void printStats() {
        log.info("Total Games: {}, Current Games: {}, Open Public Games: {}", totalGames, allGames.size(),
                openGames.size());
    }

}