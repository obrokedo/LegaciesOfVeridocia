package mb.fc.game.persist;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.concurrent.TimeUnit;

public class ClientProgress implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private HashSet<Integer> questsCompleted;
	private Hashtable<String, ArrayList<Integer>> retriggerablesPerMap;
	private Hashtable<String, ArrayList<Integer>> nonretriggerablesPerMap;
	private String map;
	private String location;
	private String name;
	private transient long lastSaveTime;
	private long timePlayed;
	
	public ClientProgress(String name)
	{
		questsCompleted = new HashSet<Integer>();
		retriggerablesPerMap = new Hashtable<String, ArrayList<Integer>>();
		nonretriggerablesPerMap = new Hashtable<String, ArrayList<Integer>>();
		this.name = name;
		lastSaveTime = System.currentTimeMillis();
	}
	
	public void setQuestComplete(int questId)
	{
		questsCompleted.add(questId);
	}
	
	public boolean isQuestComplete(int questId)
	{
		return questsCompleted.contains(questId);
	}
	
	public void serializeToFile(String map, String location)
	{
		this.map = map;
		this.location = location;
		this.timePlayed += (System.currentTimeMillis() - lastSaveTime);
		this.lastSaveTime = System.currentTimeMillis();
		try 
		{
			OutputStream file = new FileOutputStream(name + ".progress");
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeObject(this);
			output.flush();
			file.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static ClientProgress deserializeFromFile(String profile)
	{
	    try
	    {
	      InputStream file = new FileInputStream(profile);
	      InputStream buffer = new BufferedInputStream(file);
	      ObjectInput input = new ObjectInputStream (buffer);
	      
	      ClientProgress cp = (ClientProgress) input.readObject();
	      cp.lastSaveTime = System.currentTimeMillis();
	      file.close();
	      return cp;
	    }
	    catch (Exception ex)
	    {
	    	ex.printStackTrace();
	    }
	    
	    return null;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public String getLocation() {
		return location;
	}

	public String getTimePlayed() {
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(timePlayed),
	            TimeUnit.MILLISECONDS.toMinutes(timePlayed) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timePlayed)),
	            TimeUnit.MILLISECONDS.toSeconds(timePlayed) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timePlayed)));
	}
	
	public ArrayList<Integer> getRetriggerablesByMap()
	{
		if (!retriggerablesPerMap.containsKey(map))
			return null;
		return retriggerablesPerMap.get(map);
	}
	
	public void addRetriggerableByMap(int triggerId)
	{
		if (!retriggerablesPerMap.containsKey(map))
			retriggerablesPerMap.put(map, new ArrayList<Integer>());
		retriggerablesPerMap.get(map).add(triggerId);
	}
	
	public boolean isPreviouslyTriggered(int triggerId)
	{
		if (!retriggerablesPerMap.containsKey(map))
			return false;
		return retriggerablesPerMap.get(map).contains(triggerId);
	}
		
	public void addNonretriggerableByMap(int triggerId)
	{
		if (!nonretriggerablesPerMap.containsKey(map))
			nonretriggerablesPerMap.put(map, new ArrayList<Integer>());
		nonretriggerablesPerMap.get(map).add(triggerId);
	}
	
	public boolean isNonretriggableTrigger(int triggerId)
	{
		if (!nonretriggerablesPerMap.containsKey(map))
			return false;
		
		return nonretriggerablesPerMap.get(map).contains(triggerId);
	}
}
