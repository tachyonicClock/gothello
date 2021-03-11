package gothello.gothelloserver.messages;

// Message is the parent class for all serialized and deserialized messages
public class Message {
	// Every message has a message type to differentiate them
	public final String messageType;

	public Message(String messageType) {
		this.messageType = messageType;
	}

	public Message() {
		this.messageType = "message";
	}
}