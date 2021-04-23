package gothello.gothelloserver.exceptions;

public class IllegalMove extends Exception {
    public IllegalMove(String s) {
        super(s);
    }
    public IllegalMove(){
        super();
    }
}