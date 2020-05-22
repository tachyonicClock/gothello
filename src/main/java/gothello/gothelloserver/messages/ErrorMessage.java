package gothello.gothelloserver.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Error Response is an error response that is returned in JSON so that the
 * client can easily understand.
 */
public class ErrorMessage extends Message {
    public String errorMessage;

    public ErrorMessage(@JsonProperty("messageType") String messageType,
            @JsonProperty("errorMessage") String errorMessage) {
        super(messageType);
        this.errorMessage = errorMessage;
    }

    public ErrorMessage(String errorMessage) {
        super("error");
        this.errorMessage = errorMessage;
    }
}