package gothello.gothelloserver;
/**
* Error Response is an error response that is returned in JSON so that the client
* can easily understand.
*/
public class ErrorResponse extends Response {
    public final String errorMessage;

    @Override
    public String getType() {
        return "error";
    }
    @Override
    public Boolean getOk() {
        return false;
    }

    ErrorResponse(String err){
        errorMessage = err;
    }
}