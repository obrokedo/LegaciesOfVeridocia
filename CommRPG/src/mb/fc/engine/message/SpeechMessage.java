package mb.fc.engine.message;

import mb.fc.game.menu.Portrait;

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
	private Portrait portrait = null;

	public SpeechMessage(String text, int triggerId, Portrait portrait) {
		super(MessageType.SPEECH);
		this.text = text;
		this.triggerId = triggerId;
		this.portrait = portrait;
	}

	public String getText() {
		return text;
	}

	public int getTriggerId() {
		return triggerId;
	}

	public Portrait getPortrait() {
		return portrait;
	}
}
