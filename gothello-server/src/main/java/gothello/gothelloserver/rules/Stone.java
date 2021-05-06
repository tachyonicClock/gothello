package gothello.gothelloserver.rules;

/**
 * Stone
 */
public enum Stone {

    BLACK("black", true),
    WHITE("white", true),
    DRAW("draw", false),
    SPECTATOR("spectator", false),
    NONE("none", false);

    private final String name;
    private final boolean playable;

    private Stone(String name, boolean playable) {
        this.name = name;
        this.playable = playable;
    }

    public String toString() {
        return name;
    }

    public Boolean isPlayable() {
        return playable;
    }

    public static Stone otherPlayer(Stone player) {
        switch (player) {
            case WHITE:
                return Stone.BLACK;
            case BLACK:
                return Stone.WHITE;
            default:
                return Stone.WHITE;
        }
    }

}