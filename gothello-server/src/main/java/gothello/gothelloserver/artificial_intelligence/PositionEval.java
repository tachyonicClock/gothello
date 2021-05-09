package gothello.gothelloserver.artificial_intelligence;

import javax.imageio.ImageIO;
import java.awt.image.Raster;

import gothello.gothelloserver.rules.Board;
import gothello.gothelloserver.rules.GothelloState;
import gothello.gothelloserver.rules.Stone;

public class PositionEval {
    // instance used in singleton
    private static final PositionEval instance = new PositionEval();

    // captureWeight assigns a weight to capturing stones. 0->255
    private static final int captureWeight = 125;

    // ScoreMap is a grey scale image representing the importance of tiles in
    // the game
    Raster scoreMap;
    
    // Construct PositionEval singleton 
    private PositionEval(){
        try {
            scoreMap = ImageIO.read(getClass().getClassLoader().getResource("score_map.png")).getRaster();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find score_map.png it should be in the resources file" + e);
        }
    }

    // Return singleton
    public static PositionEval getInstance(){
        return instance;
    }

    // Get a value from inside of the scoreMap
    private int getXY(int x, int y){
        int[] b = new int[1];
        scoreMap.getPixel(x, y, b);
        return b[0];
    }
    
    /**
     * Static evaluation of the board position. A mix of the game score and a
     * scoreMap heuristic used to assign value to particular "good" positions
     * 
     * @param state  The state to evaluate
     * @param player The perspective the evaluation shall be made from
     * @return A score for the current position
     */
    public int eval(GothelloState state, Stone player){
        Stone playerB = Stone.otherPlayer(player);

        int score = 0;
        for (int x = 0; x < Board.width; x++) {
            for (int y = 0; y < Board.height; y++) {
                Stone stone = state.board.get(x, y);
                if (stone == player) {
                    score += getXY(x, y);
                }else if (stone == playerB) {
                    score -= getXY(x, y);
                }
            }
        }

        // Add captures with weighting
        score += state.getCaptures(player) * captureWeight;
        score -= state.getCaptures(playerB) * captureWeight;
        return score;
    }
}
