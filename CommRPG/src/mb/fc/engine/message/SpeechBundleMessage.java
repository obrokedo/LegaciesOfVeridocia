package mb.fc.engine.message;

/**
 * A message that indicates that a speech menu should be displayed with the given
 * text and portrait
 *
 * @author Broked
 *
 */
public class SpeechBundleMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private int speechId;
	private int speechIndex;
	
	public SpeechBundleMessage(int speechId, int speechIndex) {
		super(MessageType.SPEECH);
		this.speechId = speechId;
		this.speechIndex = speechIndex;
	}

	public int getSpeechId() {
		return speechId;
	}

	public int getSpeechIndex() {
		return speechIndex;
	}
}