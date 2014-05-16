package mb.fc.engine.message;

public class LoadMapMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private String map;
	private String battle;
	private String location;
	private int cinematicID;

	public LoadMapMessage(int messageType, String map, String battle, String location)
	{
		super(messageType);
		this.map = map;
		this.battle = battle;
		this.location = location;
	}

	public LoadMapMessage(int messageType, String map, int cinematicID)
	{
		super(messageType);
		this.map = map;
		this.cinematicID = cinematicID;
	}

	public String getMap() {
		return map;
	}

	public String getBattle() {
		return battle;
	}

	public String getLocation() {
		return location;
	}

	public int getCinematicID() {
		return cinematicID;
	}
}
