package mb.fc.game.text;

public class Speech 
{
	private String message;
	private int[] requires;
	private int[] excludes;
	private int triggerId;
	private int portraitId;
	
	public Speech(String message, int[] requires, int[] excludes, int triggerId, int portraitId) {
		super();
		this.message = message;
		this.requires = requires;
		this.excludes = excludes;
		this.triggerId = triggerId;
		this.portraitId = portraitId;
	}

	public String getMessage() {
		return message;
	}

	public int[] getRequires() {
		return requires;
	}

	public int[] getExcludes() {
		return excludes;
	}

	public int getTriggerId() {
		return triggerId;
	}

	public int getPortraitId() {
		return portraitId;
	}
}
