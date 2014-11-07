package mb.fc.engine.message;

/**
 * A message that indicates that a new map, battle or cinematic should be loaded
 *
 * @author Broked
 *
 */
public class LoadMapMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private String map;
	private String battle;
	private String location;
	private int cinematicID;
	private int battleBG;

	public LoadMapMessage(int messageType, String map, String battle, String location, int battleBG)
	{
		super(messageType);
		this.map = map;
		this.battle = battle;
		this.location = location;
		this.battleBG = battleBG;
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

	public int getBattleBG() {
		return battleBG;
	}
}
