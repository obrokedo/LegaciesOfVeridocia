package mb.fc.game.trigger;

import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.map.MapObject;

import org.newdawn.slick.geom.Shape;

public class TriggerLocation
{
	private Shape triggerLocation;
	private ArrayList<TriggerEvent> events;

	public TriggerLocation(StateInfo stateInfo, MapObject mo)
	{
		triggerLocation = mo.getShape();
		TriggerEvent event = null;
		if (mo.getParam("enter") != null)
		{
			String map = mo.getParam("enter");
			event = new TriggerEvent(-1, false, false, false, false, null, null);
			event.addTriggerType(event.new TriggerEnter(map, mo.getParam("exit")));
			events = new ArrayList<TriggerEvent>();
			events.add(event);
		}
		else if (mo.getParam("triggerid") != null)
		{
			events = new ArrayList<TriggerEvent>();

			for (String trig : mo.getParam("triggerid").split(","))
			{
				TriggerEvent eventToTrigger = stateInfo.getResourceManager().getTriggerEventById(Integer.parseInt(trig));

				// event = new TriggerEvent(-1, false, false, false, eventToTrigger., null, null);
				// event.addTriggerType(event.new TriggerById(Integer.parseInt(trig)));
				events.add(eventToTrigger);
			}
		}
	}

	public boolean contains(int mapX, int mapY)
	{
		return triggerLocation.contains(mapX + 1, mapY + 1);
	}

	public void perform(StateInfo stateInfo, boolean immediate)
	{
		for (TriggerEvent ev : events)
			ev.perform(stateInfo, immediate);
	}
}
