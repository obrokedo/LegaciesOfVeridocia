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
	private String mapData;
	private String location;
	private int cinematicID;
	private int battleBG;

	public LoadMapMessage(MessageType messageType, String map, String mapData, String location, int battleBG)
	{
		super(messageType);
		this.map = map;
		this.mapData = mapData;
		this.location = location;
		this.battleBG = battleBG;
	}

	public LoadMapMessage(MessageType messageType, String map, String mapData, int cinematicID)
	{
		super(messageType);
		this.map = map;
		this.mapData = mapData;
		this.cinematicID = cinematicID;
	}

	public String getMap() {
		return map;
	}

	public String getMapData() {
		return mapData;
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
