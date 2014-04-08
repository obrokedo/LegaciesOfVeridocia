package mb.fc.engine.message;

public class SpeechMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private String text;
	private int triggerId = -1;
	private int portraitId = -1;
	
	public SpeechMessage(int messageType, String text, int triggerId, int portraitId) {
		super(messageType);
		this.text = text;
		this.triggerId = triggerId;
		this.portraitId = portraitId;
	}

	public String getText() {
		return text;
	}

	public int getTriggerId() {
		return triggerId;
	}

	public int getPortraitId() {
		return portraitId;
	}
}
