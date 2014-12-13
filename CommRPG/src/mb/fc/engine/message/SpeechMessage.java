package mb.fc.engine.message;

/**
 * A message that indicates that a speech menu should be displayed with the given
 * text and portrait
 *
 * @author Broked
 *
 */
public class SpeechMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private String text;
	private int triggerId = -1;
	private int portraitId = -1;

	public SpeechMessage(String text, int triggerId, int portraitId) {
		super(MessageType.SPEECH);
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
