package mb.fc.engine.message;

public class ChatMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private String sender;
	private String text;
	
	public ChatMessage(int messageType, String sender, String text) {
		super(messageType);
		this.sender = sender;
		this.text = text;
	}

	public String getSender() {
		return sender;
	}

	public String getText() {
		return text;
	}
}
