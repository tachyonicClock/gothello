package gothello.gothelloserver.exceptions;

public class GameNotFound extends Exception {
    private static final long serialVersionUID = 1L;
    public GameNotFound(String s) {
        super(s);
    }
    public GameNotFound(){
        super();
    }
}