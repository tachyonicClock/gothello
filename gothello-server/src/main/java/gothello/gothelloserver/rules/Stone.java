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

    // public String toString

    private final String name;
    private final boolean playable;

    private Stone(String name, boolean playable){
        this.name = name;
        this.playable = playable;
    }

    public String toString(){
        return name;
    }

    public Boolean isPlayable(){
        return playable;
    }

}