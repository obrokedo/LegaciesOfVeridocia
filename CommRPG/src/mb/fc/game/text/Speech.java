package mb.fc.game.text;

public class Speech 
{
	private String message;
	private int[] requires;
	private int[] excludes;
	private int triggerId;
	
	public Speech(String message, int[] requires, int[] excludes, int triggerId) {
		super();
		this.message = message;
		this.requires = requires;
		this.excludes = excludes;
		this.triggerId = triggerId;
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
}
