package mb.fc.engine.message;

public class SpeechMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private String text;
	private int triggerId = -1;
	
	public SpeechMessage(int messageType, String text, int triggerId) {
		super(messageType);
		this.text = text;
		this.triggerId = triggerId;
	}

	public String getText() {
		return text;
	}

	public int getTriggerId() {
		return triggerId;
	}
}
