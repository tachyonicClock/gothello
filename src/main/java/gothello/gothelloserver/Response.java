package gothello.gothelloserver;

// Response is the parent class for all resource responses
public class Response {
	public Boolean getOk() {
		return true;
	}
	
	public String getType() {
		return "response";
	}
}