package mb.fc.engine.message;

public class AudioMessage extends Message
{
	private String audio;
	private float volume;
	private boolean loop;

	public AudioMessage(int messageType, String audio, float volume, boolean loop) {
		super(messageType);
		this.audio = audio;
		this.volume = volume;
		this.loop = loop;
	}

	public String getAudio() {
		return audio;
	}

	public float getVolume() {
		return volume;
	}

	public boolean isLoop() {
		return loop;
	}
}
