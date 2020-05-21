package gothello.gothelloserver;

/**
* JSON Error is an error response that is returned in JSON so that the client
* can easily understand.
*/
public class JSONError extends Response {
	private final String message;
	
	@Override
	public String getType(){
		return "error";
	}
	
	@Override
	public Boolean getOk(){
		return false;
	}
	
	public String getMessage(){
		return message;
	}
	
	JSONError(String message){
		this.message = message;
	}
}