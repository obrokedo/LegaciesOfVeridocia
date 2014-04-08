package mb.fc.loading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import mb.fc.cinematic.Cinematic;
import mb.fc.cinematic.event.CinematicEvent;
import mb.fc.cinematic.event.CinematicEvent.CinematicEventType;
import mb.fc.game.text.Speech;
import mb.fc.game.trigger.TriggerEvent;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;

public class TextParser 
{
	public static void parseText(String file, Hashtable<Integer, ArrayList<Speech>> speechesById,
			Hashtable<Integer, TriggerEvent> triggerEventById, Hashtable<Integer, Cinematic> cinematicById) throws IOException
	{
		ArrayList<TagArea> tagAreas = XMLParser.process(file);
		for (TagArea tagArea : tagAreas)
		{
			if (tagArea.getTagType().equalsIgnoreCase("text"))
			{
				int id = Integer.parseInt(tagArea.getParams().get("id"));
				ArrayList<Speech> speeches = new ArrayList<Speech>();
				
				for (TagArea childTagArea : tagArea.getChildren())
				{
					int triggerId = -1;
					int portraitId = -1;
					String message = childTagArea.getParams().get("message");
					//String requires = childTagArea.getParams().get("require");
					//String excludes = childTagArea.getParams().get("exclude");
					int requires = Integer.parseInt(childTagArea.getParams().get("require"));
					int excludes = Integer.parseInt(childTagArea.getParams().get("exclude"));
					String trigger = childTagArea.getParams().get("trigger");
					String portrait = childTagArea.getParams().get("portrait");
					int[] requireIds = null;
					if (requires != -1)
						requireIds = new int[] {requires};
					/*
					if (requires != null)
					{
						String[] splitReq = requires.split(",");
						requireIds = new int[splitReq.length];
						for (int i = 0; i < splitReq.length; i++)
							requireIds[i] = Integer.parseInt(splitReq[i]);
					}
					*/
					
					int[] excludeIds = null;
					if (excludes != -1)
						excludeIds = new int[] {excludes};
					/*
					if (excludes != null)
					{
						String[] splitEx = excludes.split(",");
						excludeIds = new int[splitEx.length];
						for (int i = 0; i < splitEx.length; i++)
							excludeIds[i] = Integer.parseInt(splitEx[i]);
					}
					*/										
					
					if (trigger != null)
						triggerId = Integer.parseInt(trigger);
					
					if (portrait != null)
						portraitId = Integer.parseInt(portrait);
					
					speeches.add(new Speech(message, requireIds, excludeIds, triggerId, portraitId));
				}
				speechesById.put(id, speeches);
			}
			else if (tagArea.getTagType().equalsIgnoreCase("trigger"))
			{
				int id = Integer.parseInt(tagArea.getParams().get("id"));
				boolean nonRetrig = false;
				boolean retrigOnEnter = false;
				if (tagArea.getParams().containsKey("nonretrig"))
					nonRetrig = Boolean.parseBoolean(tagArea.getParams().get("nonretrig"));
				if (tagArea.getParams().containsKey("retrigonenter"))
					retrigOnEnter = Boolean.parseBoolean(tagArea.getParams().get("retrigonenter"));
				
				TriggerEvent te = new TriggerEvent(id, retrigOnEnter, nonRetrig);
				if (tagArea.getChildren().size() > 0)
				{
					for (int k = 0; k < tagArea.getChildren().size(); k++)
					{
						Hashtable<String, String> actionParams = tagArea.getChildren().get(k).getParams();
						
						if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("completequest"))
						{
							te.addTriggerType(te.new TriggerCompleteQuest(Integer.parseInt(actionParams.get("questid"))));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("startbattle"))
						{
							te.addTriggerType(te.new TriggerStartBattle(actionParams.get("battletriggers"), actionParams.get("battlemap")));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("loadmap"))
						{
							te.addTriggerType(te.new TriggerEnter(actionParams.get("map"), actionParams.get("enter")));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("showshop"))
						{
							te.addTriggerType(te.new TriggerShowShop(actionParams.get("shop")));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("showpriest"))
						{
							te.addTriggerType(te.new TriggerShowPriest());
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("addhero"))
						{
							te.addTriggerType(te.new TriggerAddHero(Integer.parseInt(actionParams.get("heroid"))));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("playmusic"))
						{
							te.addTriggerType(te.new TriggerPlayMusic(actionParams.get("music")));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("changeai"))
						{
							te.addTriggerType(te.new TriggerChangeAI(actionParams.get("aitype"), 
									actionParams.get("id"), actionParams.get("targetid"), actionParams.get("x"), actionParams.get("y")));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("showtext"))
						{
							te.addTriggerType(te.new TriggerShowText(actionParams.get("text")));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("cinematic"))
						{
							te.addTriggerType(te.new TriggerShowCinematic(Integer.parseInt(actionParams.get("cinematicid"))));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("showroof"))
						{
							te.addTriggerType(te.new TriggerToggleRoof(Integer.parseInt(actionParams.get("roofid")), true));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("hideroof"))
						{
							te.addTriggerType(te.new TriggerToggleRoof(Integer.parseInt(actionParams.get("roofid")), false));
						}
					}
				}				
				
				triggerEventById.put(id, te);
			}
			else if (tagArea.getTagType().equalsIgnoreCase("cinematic"))
			{
				int cinematicId = Integer.parseInt(tagArea.getParams().get("id"));
				int cameraX = Integer.parseInt(tagArea.getParams().get("camerax"));
				int cameraY = Integer.parseInt(tagArea.getParams().get("cameray"));
				
				ArrayList<CinematicEvent> initEvents = new ArrayList<CinematicEvent>();
				ArrayList<CinematicEvent> events = new ArrayList<CinematicEvent>();
				
				for (TagArea childArea : tagArea.getChildren())
				{					
					// Add the event
					events.add(parseCinematicEvent(childArea, initEvents));
				}
				
				cinematicById.put(cinematicId, new Cinematic(initEvents, events, cameraX, cameraY));
				System.out.println();
			}
		}
	}
	
	private static CinematicEvent parseCinematicEvent(TagArea area, ArrayList<CinematicEvent> initEvents)
	{
		String type = area.getTagType();
		
		if (type.equalsIgnoreCase("addactor"))
		{
			CinematicEvent ce = new CinematicEvent(CinematicEventType.ADD_ACTOR, Integer.parseInt(area.getParams().get("x")), 
					Integer.parseInt(area.getParams().get("y")), 
					area.getParams().get("name"), area.getParams().get("anim"), 
					area.getParams().get("startanim"), Boolean.parseBoolean(area.getParams().get("visible"))); 
			if (Boolean.parseBoolean(area.getParams().get("init")))
				initEvents.add(ce);				
			return ce;
		}
		else if (type.equalsIgnoreCase("camerafollow"))
			return new CinematicEvent(CinematicEventType.CAMERA_FOLLOW, area.getParams().get("name"));
		else if (type.equalsIgnoreCase("haltingmove"))
			return new CinematicEvent(CinematicEventType.HALTING_MOVE, Integer.parseInt(area.getParams().get("x")), 
					Integer.parseInt(area.getParams().get("y")), Integer.parseInt(area.getParams().get("speed")), area.getParams().get("name"));
		else if (type.equalsIgnoreCase("move"))
			return new CinematicEvent(CinematicEventType.MOVE, Integer.parseInt(area.getParams().get("x")), 
					Integer.parseInt(area.getParams().get("y")), Integer.parseInt(area.getParams().get("speed")), area.getParams().get("name"));
		else if (type.equalsIgnoreCase("forcedmove"))
			return new CinematicEvent(CinematicEventType.MOVE_ENFORCE_FACING, Integer.parseInt(area.getParams().get("x")), 
					Integer.parseInt(area.getParams().get("y")), Integer.parseInt(area.getParams().get("speed")), area.getParams().get("name"),
						Integer.parseInt(area.getParams().get("facing")));
		else if (type.equalsIgnoreCase("haltinganim"))
			return new CinematicEvent(CinematicEventType.HALTING_ANIMATION, area.getParams().get("name"), 
					area.getParams().get("anim"), Integer.parseInt(area.getParams().get("time")));
		else if (type.equalsIgnoreCase("anim"))
			return new CinematicEvent(CinematicEventType.ANIMATION, area.getParams().get("name"), 
					area.getParams().get("anim"), Integer.parseInt(area.getParams().get("time")), Boolean.parseBoolean(area.getParams().get("loops")));
		else if (type.equalsIgnoreCase("cameramove"))
			return new CinematicEvent(CinematicEventType.CAMERA_MOVE, Integer.parseInt(area.getParams().get("x")), 
					Integer.parseInt(area.getParams().get("y")), Integer.parseInt(area.getParams().get("time")));
		else if (type.equalsIgnoreCase("speech"))
			return new CinematicEvent(CinematicEventType.SPEECH, area.getParams().get("text"), Integer.parseInt(area.getParams().get("portrait")));
		else if (type.equalsIgnoreCase("loadmap"))
			return new CinematicEvent(CinematicEventType.LOAD_MAP, area.getParams().get("map"), area.getParams().get("enter"));
		else if (type.equalsIgnoreCase("wait"))
			return new CinematicEvent(CinematicEventType.WAIT, Integer.parseInt(area.getParams().get("time")));
		else if (type.equalsIgnoreCase("spin"))
			return new CinematicEvent(CinematicEventType.SPIN, area.getParams().get("name"), Integer.parseInt(area.getParams().get("speed")), 
					Integer.parseInt(area.getParams().get("time")));
		else if (type.equalsIgnoreCase("stopspin"))
			return new CinematicEvent(CinematicEventType.STOP_SPIN, area.getParams().get("name"));
		else if (type.equalsIgnoreCase("facing"))
			return new CinematicEvent(CinematicEventType.FACING, area.getParams().get("name"), Integer.parseInt(area.getParams().get("dir")));
		else if (type.equalsIgnoreCase("shrink"))
			return new CinematicEvent(CinematicEventType.SHRINK, area.getParams().get("name"), Integer.parseInt(area.getParams().get("time")));
		else if (type.equalsIgnoreCase("grow"))
			return new CinematicEvent(CinematicEventType.GROW, area.getParams().get("name"), Integer.parseInt(area.getParams().get("time")));
		else if (type.equalsIgnoreCase("stopse"))
			return new CinematicEvent(CinematicEventType.STOP_SE, area.getParams().get("name"));
		else if (type.equalsIgnoreCase("visible"))
			return new CinematicEvent(CinematicEventType.VISIBLE, area.getParams().get("name"), Boolean.parseBoolean(area.getParams().get("isvis")));
		else if (type.equalsIgnoreCase("removeactor"))
			return new CinematicEvent(CinematicEventType.REMOVE_ACTOR, area.getParams().get("name"));
		else if (type.equalsIgnoreCase("quiver"))
			return new CinematicEvent(CinematicEventType.QUIVER, area.getParams().get("name"));
		else if (type.equalsIgnoreCase("fallonface"))
			return new CinematicEvent(CinematicEventType.FALL_ON_FACE, area.getParams().get("name"));
		else if (type.equalsIgnoreCase("layonside"))
			return new CinematicEvent(CinematicEventType.LAY_ON_SIDE, area.getParams().get("name"));
		else if (type.equalsIgnoreCase("flash"))
			return new CinematicEvent(CinematicEventType.FLASH, area.getParams().get("name"), 
					Integer.parseInt(area.getParams().get("speed")), Integer.parseInt(area.getParams().get("time")));
		else if (type.equalsIgnoreCase("nod"))
			return new CinematicEvent(CinematicEventType.NOD, area.getParams().get("name"));
		else if (type.equalsIgnoreCase("shakehead"))
			return new CinematicEvent(CinematicEventType.HEAD_SHAKE, area.getParams().get("name"));
		else if (type.equalsIgnoreCase("loopmove"))
			return new CinematicEvent(CinematicEventType.LOOP_MOVE, area.getParams().get("name"), Integer.parseInt(area.getParams().get("x")), 
					Integer.parseInt(area.getParams().get("y")), Integer.parseInt(area.getParams().get("speed")));
		else if (type.equalsIgnoreCase("stoploopmove"))
			return new CinematicEvent(CinematicEventType.STOP_LOOP_MOVE, area.getParams().get("name"));
		else if (type.equalsIgnoreCase("camerashake"))
			return new CinematicEvent(CinematicEventType.CAMERA_SHAKE, Integer.parseInt(area.getParams().get("time")), Integer.parseInt(area.getParams().get("severity")));
		else if (type.equalsIgnoreCase("playmusic"))
			return new CinematicEvent(CinematicEventType.PLAY_MUSIC, area.getParams().get("music"), Integer.parseInt(area.getParams().get("volume")));
		else if (type.equalsIgnoreCase("pausemusic"))
			return new CinematicEvent(CinematicEventType.PAUSE_MUSIC);
		else if (type.equalsIgnoreCase("resumemusic"))
			return new CinematicEvent(CinematicEventType.RESUME_MUSIC);
		else if (type.equalsIgnoreCase("fademusic"))
			return new CinematicEvent(CinematicEventType.FADE_MUSIC, Integer.parseInt(area.getParams().get("duration")));
		else if (type.equalsIgnoreCase("playsound"))
			return new CinematicEvent(CinematicEventType.PLAY_SOUND, area.getParams().get("sound"), Integer.parseInt(area.getParams().get("volume")));
		return null;
	}
}