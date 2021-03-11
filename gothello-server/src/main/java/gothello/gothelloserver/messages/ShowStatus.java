package gothello.gothelloserver.messages;

// Status is used to display non-error messages in the client
public class ShowStatus extends Message {
  public enum Variant {
    DEFAULT, SUCCESS, ERROR, WARNING, INFO
  }
  public final Variant variant;
  public final String message;
  public ShowStatus(Variant variant, String message){
    super("status");
    this.variant = variant;
    this.message = message;
  }
}
