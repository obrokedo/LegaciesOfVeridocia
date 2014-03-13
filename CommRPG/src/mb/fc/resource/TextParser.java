package mb.fc.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import mb.fc.game.text.Speech;
import mb.fc.game.trigger.TriggerEvent;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;

public class TextParser 
{
	public static void parseText(String file, Hashtable<Integer, ArrayList<Speech>> speechesById,
			Hashtable<Integer, TriggerEvent> triggerEventById, Class<?> cl) throws IOException
	{
		ArrayList<TagArea> tagAreas = XMLParser.process(file, cl);
		for (TagArea tagArea : tagAreas)
		{
			if (tagArea.getTagType().equalsIgnoreCase("text"))
			{
				int id = Integer.parseInt(tagArea.getParams().get("textid"));
				ArrayList<Speech> speeches = new ArrayList<Speech>();
				
				for (TagArea childTagArea : tagArea.getChildren())
				{
					int triggerId = -1;
					String message = childTagArea.getParams().get("message");
					String requires = childTagArea.getParams().get("require");
					String excludes = childTagArea.getParams().get("exclude");
					String trigger = childTagArea.getParams().get("trigger");
					int[] requireIds = null;
					if (requires != null)
					{
						String[] splitReq = requires.split(",");
						requireIds = new int[splitReq.length];
						for (int i = 0; i < splitReq.length; i++)
							requireIds[i] = Integer.parseInt(splitReq[i]);
					}
					
					int[] excludeIds = null;
					if (excludes != null)
					{
						String[] splitEx = excludes.split(",");
						excludeIds = new int[splitEx.length];
						for (int i = 0; i < splitEx.length; i++)
							excludeIds[i] = Integer.parseInt(splitEx[i]);
					}										
					
					if (trigger != null)
						triggerId = Integer.parseInt(trigger);
					
					speeches.add(new Speech(message, requireIds, excludeIds, triggerId));
				}
				speechesById.put(id, speeches);
			}
			else if (tagArea.getTagType().equalsIgnoreCase("trigger"))
			{
				int id = Integer.parseInt(tagArea.getParams().get("triggerid"));
				boolean nonRetrig = false;
				boolean retrigOnEnter = false;
				if (tagArea.getParams().containsKey("nonretrig"))
					nonRetrig=true;
				if (tagArea.getParams().containsKey("retrigonenter"))
					retrigOnEnter=true;
				
				TriggerEvent te = new TriggerEvent(id, retrigOnEnter, nonRetrig);
				if (tagArea.getChildren().size() > 0)
				{
					for (int k = 0; k < tagArea.getChildren().size(); k++)
					{
						Hashtable<String, String> actionParams = tagArea.getChildren().get(k).getParams();
						
						if (actionParams.containsKey("completequest"))
						{
							te.addTriggerType(te.new TriggerCompleteQuest(Integer.parseInt(actionParams.get("completequest"))));
						}
						else if (actionParams.containsKey("startbattle"))
						{
							te.addTriggerType(te.new TriggerStartBattle(actionParams.get("startbattle"), actionParams.get("battlemap")));
						}
						else if (actionParams.containsKey("loadmap"))
						{
							te.addTriggerType(te.new TriggerEnter(actionParams.get("loadmap"), actionParams.get("exit")));
						}
						else if (actionParams.containsKey("showshop"))
						{
							te.addTriggerType(te.new TriggerShowShop(actionParams.get("showshop")));
						}
						else if (actionParams.containsKey("showpriest"))
						{
							te.addTriggerType(te.new TriggerShowPriest());
						}
						else if (actionParams.containsKey("addhero"))
						{
							te.addTriggerType(te.new TriggerAddHero(Integer.parseInt(actionParams.get("addhero"))));
						}
						else if (actionParams.containsKey("playmusic"))
						{
							te.addTriggerType(te.new TriggerPlayMusic(actionParams.get("playmusic")));
						}
						else if (actionParams.containsKey("changeai"))
						{
							te.addTriggerType(te.new TriggerChangeAI(actionParams.get("changeai"), 
									actionParams.get("id"), actionParams.get("targetid"), actionParams.get("x"), actionParams.get("y")));
						}
						else if (actionParams.containsKey("text"))
						{
							te.addTriggerType(te.new TriggerShowText(actionParams.get("text")));
						}
					}
				}
				
				triggerEventById.put(id, te);
			}
		}
	}
}
