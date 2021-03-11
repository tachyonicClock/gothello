package gothello.gothelloserver.messages;

/**
 * Error Response is an error response that is returned in JSON so that the
 * client can easily understand.
 */
public class ErrorMessage extends ShowStatus {
    public ErrorMessage(String errorMessage) {
        super(Variant.ERROR, errorMessage);
    }
}