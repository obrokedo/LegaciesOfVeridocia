package mb.fc.game.trigger;

import mb.fc.engine.state.StateInfo;
import mb.fc.map.MapObject;

import org.newdawn.slick.geom.Shape;

public class Trigger 
{
	private Shape triggerLocation;
	private TriggerEvent event;
	
	public Trigger(StateInfo stateInfo, MapObject mo)
	{
		triggerLocation = mo.getShape();
		event = new TriggerEvent(-1, false, false);
		if (mo.getParam("enter") != null)
		{
			String map = mo.getParam("enter");		
			event.addTriggerType(event.new TriggerEnter(map, mo.getParam("exit")));
		}
		else if (mo.getParam("triggerid") != null)
		{
			event.addTriggerType(event.new TriggerById(Integer.parseInt(mo.getParam("triggerid"))));
		}		
	}
	
	public boolean contains(int mapX, int mapY)
	{
		return triggerLocation.contains(mapX + 1, mapY + 1);
	}
	
	public void perform(StateInfo stateInfo)
	{
		event.perform(stateInfo);
	}
}
