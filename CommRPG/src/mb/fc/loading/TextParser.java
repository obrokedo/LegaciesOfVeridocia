package mb.fc.loading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import mb.fc.cinematic.Cinematic;
import mb.fc.cinematic.event.CinematicEvent;
import mb.fc.cinematic.event.CinematicEvent.CinematicEventType;
import mb.fc.game.text.Speech;
import mb.fc.game.trigger.TriggerEvent;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;

import org.newdawn.slick.SlickException;

public class TextParser
{
	public static void parseText(String file, Hashtable<Integer, ArrayList<Speech>> speechesById,
			Hashtable<Integer, TriggerEvent> triggerEventById, Hashtable<Integer, Cinematic> cinematicById,
			FCResourceManager frm) throws IOException, SlickException
	{
		HashSet<String> animToLoad = new HashSet<String>();
		HashSet<String> soundToLoad = new HashSet<String>();
		HashSet<String> musicToLoad = new HashSet<String>();
		HashSet<String> spriteToLoad = new HashSet<String>();

		ArrayList<TagArea> tagAreas = XMLParser.process(file);
		for (TagArea tagArea : tagAreas)
		{
			if (tagArea.getTagType().equalsIgnoreCase("text"))
			{
				int id = Integer.parseInt(tagArea.getAttribute("id"));
				ArrayList<Speech> speeches = new ArrayList<Speech>();

				for (TagArea childTagArea : tagArea.getChildren())
				{
					int triggerId = -1;
					int portraitId = -1;
					String message = childTagArea.getAttribute("message");
					String requires = childTagArea.getAttribute("require");
					String excludes = childTagArea.getAttribute("exclude");
					String trigger = childTagArea.getAttribute("trigger");
					String portrait = childTagArea.getAttribute("portrait");
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

					if (portrait != null)
						portraitId = Integer.parseInt(portrait);

					speeches.add(new Speech(message, requireIds, excludeIds, triggerId, portraitId));
				}
				speechesById.put(id, speeches);
			}
			else if (tagArea.getTagType().equalsIgnoreCase("trigger"))
			{
				int id = Integer.parseInt(tagArea.getAttribute("id"));
				boolean nonRetrig = false;
				boolean retrigOnEnter = false;
				boolean triggerOnce = false;
				boolean triggerImmediately = false;
				int[] requireIds = null;
				int[] excludeIds = null;
				if (tagArea.getAttribute("nonretrig") != null)
					nonRetrig = Boolean.parseBoolean(tagArea.getAttribute("nonretrig"));
				if (tagArea.getAttribute("retrigonenter") != null)
					retrigOnEnter = Boolean.parseBoolean(tagArea.getAttribute("retrigonenter"));
				if (tagArea.getAttribute("triggeronce") != null)
					triggerOnce = Boolean.parseBoolean(tagArea.getAttribute("triggeronce"));
				if (tagArea.getAttribute("triggerimmediately") != null)
					triggerImmediately = Boolean.parseBoolean(tagArea.getAttribute("triggerimmediately"));

				String requires = tagArea.getAttribute("require");
				String excludes = tagArea.getAttribute("exclude");

				if (requires != null)
				{
					String[] splitReq = requires.split(",");
					requireIds = new int[splitReq.length];
					for (int i = 0; i < splitReq.length; i++)
						requireIds[i] = Integer.parseInt(splitReq[i]);
				}

				if (excludes != null)
				{
					String[] splitEx = excludes.split(",");
					excludeIds = new int[splitEx.length];
					for (int i = 0; i < splitEx.length; i++)
						excludeIds[i] = Integer.parseInt(splitEx[i]);
				}

				TriggerEvent te = new TriggerEvent(id, retrigOnEnter, nonRetrig, triggerOnce, triggerImmediately, requireIds, excludeIds);
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
							int battleBackgroundIndex = 0;

							if (actionParams.containsKey("battbg"))
								battleBackgroundIndex = Integer.parseInt(actionParams.get("battbg"));
							te.addTriggerType(te.new TriggerStartBattle(actionParams.get("battletriggers"),
									actionParams.get("battlemap"), actionParams.get("entrance"), battleBackgroundIndex));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("setbattlecond"))
						{
							String leaders = actionParams.get("templeader");
							int[] leaderIds = null;

							if (leaders != null)
							{
								String[] splitLeader = leaders.split(",");
								leaderIds = new int[splitLeader.length];
								for (int i = 0; i < splitLeader.length; i++)
									leaderIds[i] = Integer.parseInt(splitLeader[i]);
							}

							te.addTriggerType(te.new TriggerBattleCond(leaderIds, Boolean.parseBoolean(actionParams.get("allleaders"))));
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
							musicToLoad.add(actionParams.get("music"));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("playsound"))
						{
							te.addTriggerType(te.new TriggerPlaySound(actionParams.get("sound"), Integer.parseInt(actionParams.get("volume"))));
							soundToLoad.add(actionParams.get("sound"));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("changeai"))
						{
							te.addTriggerType(te.new TriggerChangeAI(actionParams.get("aitype"),
									actionParams.get("id"), actionParams.get("targetid"), actionParams.get("heroid"),
									actionParams.get("x"), actionParams.get("y")));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("showtext"))
						{
							te.addTriggerType(te.new TriggerShowText(Integer.parseInt(actionParams.get("textid"))));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("showcin"))
						{
							te.addTriggerType(te.new TriggerShowCinematic(Integer.parseInt(actionParams.get("cinid"))));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("loadcin"))
						{
							te.addTriggerType(te.new TriggerLoadCinematic(actionParams.get("map"), Integer.parseInt(actionParams.get("cinid"))));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("showroof"))
						{
							te.addTriggerType(te.new TriggerToggleRoof(Integer.parseInt(actionParams.get("roofid")), true));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("hideroof"))
						{
							te.addTriggerType(te.new TriggerToggleRoof(Integer.parseInt(actionParams.get("roofid")), false));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("removesprite"))
						{
							te.addTriggerType(te.new TriggerRemoveSprite(actionParams.get("name")));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("changesprite"))
						{
							te.addTriggerType(te.new TriggerChangeSprite(actionParams.get("name"), actionParams.get("image")));
							spriteToLoad.add(actionParams.get("image"));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("additem"))
						{
							te.addTriggerType(te.new TriggerAddItem(Integer.parseInt(actionParams.get("itemid"))));
						}
						else if (tagArea.getChildren().get(k).getTagType().equalsIgnoreCase("exit"))
						{
							te.addTriggerType(te.new TriggerExit());
						}

					}
				}

				triggerEventById.put(id, te);
			}
			else if (tagArea.getTagType().equalsIgnoreCase("cinematic"))
			{
				int cinematicId = Integer.parseInt(tagArea.getAttribute("id"));
				int cameraX = Integer.parseInt(tagArea.getAttribute("camerax"));
				int cameraY = Integer.parseInt(tagArea.getAttribute("cameray"));

				ArrayList<CinematicEvent> initEvents = new ArrayList<CinematicEvent>();
				ArrayList<CinematicEvent> events = parseCinematicEvents(tagArea, initEvents,
						animToLoad, musicToLoad, soundToLoad);


				cinematicById.put(cinematicId, new Cinematic(initEvents, events, cameraX, cameraY));
				System.out.println();
			}
		}

		/*
		for (String resource : spriteToLoad)
			frm.addSpriteResource(resource);
		for (String resource : animToLoad)
			frm.addAnimResource(resource);
		for (String resource : musicToLoad)
			frm.addMusicResource(resource);
		for (String resource : soundToLoad)
			frm.addSoundResource(resource);
			*/
	}

	public static ArrayList<CinematicEvent> parseCinematicEvents(TagArea tagArea, ArrayList<CinematicEvent> initEvents,
			HashSet<String> animToLoad, HashSet<String> musicToLoad, HashSet<String> soundToLoad)
	{
		ArrayList<CinematicEvent> events = new ArrayList<CinematicEvent>();

		for (TagArea childArea : tagArea.getChildren())
		{
			// Add the event
			CinematicEvent ce = parseCinematicEvent(childArea, initEvents, animToLoad, musicToLoad, soundToLoad);
			if (ce != null)
				events.add(ce);
		}

		return events;
	}

	private static CinematicEvent parseCinematicEvent(TagArea area, ArrayList<CinematicEvent> initEvents,
			HashSet<String> animToLoad, HashSet<String> musicToLoad, HashSet<String> soundToLoad)
	{
		String type = area.getTagType();

		if (type.equalsIgnoreCase("addactor"))
		{
			CinematicEvent ce = new CinematicEvent(CinematicEventType.ADD_ACTOR, Integer.parseInt(area.getAttribute("x")),
					Integer.parseInt(area.getAttribute("y")),
					area.getAttribute("name"), area.getAttribute("anim"),
					area.getAttribute("startanim"), Boolean.parseBoolean(area.getAttribute("visible")));
			animToLoad.add(area.getAttribute("anim"));
			if (Boolean.parseBoolean(area.getAttribute("init")))
			{
				initEvents.add(ce);
				return null;
			}
			return ce;
		}
		else if (type.equalsIgnoreCase("addstatic"))
			return new CinematicEvent(CinematicEventType.ADD_STATIC_SPRITE, Integer.parseInt(area.getAttribute("x")),
					Integer.parseInt(area.getAttribute("y")), area.getAttribute("spriteid"), area.getAttribute("spriteim"));
		else if (type.equalsIgnoreCase("remstatic"))
			return new CinematicEvent(CinematicEventType.REMOVE_STATIC_SPRITE, area.getAttribute("spriteid"));
		else if (type.equalsIgnoreCase("assactor"))
			return new CinematicEvent(CinematicEventType.ASSOCIATE_AS_ACTOR, area.getAttribute("name"),
					Integer.parseInt(area.getAttribute("npcid")), Boolean.parseBoolean(area.getAttribute("hero")));
		else if (type.equalsIgnoreCase("camerafollow"))
			return new CinematicEvent(CinematicEventType.CAMERA_FOLLOW, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("haltingmove"))
			return new CinematicEvent(CinematicEventType.HALTING_MOVE, Integer.parseInt(area.getAttribute("x")),
					Integer.parseInt(area.getAttribute("y")), Float.parseFloat(area.getAttribute("speed")), area.getAttribute("name"),
					Boolean.parseBoolean(area.getAttribute("movehor")), Boolean.parseBoolean(area.getAttribute("movediag")));
		else if (type.equalsIgnoreCase("move"))
			return new CinematicEvent(CinematicEventType.MOVE, Integer.parseInt(area.getAttribute("x")),
					Integer.parseInt(area.getAttribute("y")), Float.parseFloat(area.getAttribute("speed")), area.getAttribute("name"),
					Boolean.parseBoolean(area.getAttribute("movehor")), Boolean.parseBoolean(area.getAttribute("movediag")));
		else if (type.equalsIgnoreCase("forcedmove"))
			return new CinematicEvent(CinematicEventType.MOVE_ENFORCE_FACING, Integer.parseInt(area.getAttribute("x")),
					Integer.parseInt(area.getAttribute("y")), Float.parseFloat(area.getAttribute("speed")), area.getAttribute("name"),
						Integer.parseInt(area.getAttribute("facing")),
						Boolean.parseBoolean(area.getAttribute("movehor")), Boolean.parseBoolean(area.getAttribute("movediag")));
		else if (type.equalsIgnoreCase("haltinganim"))
			return new CinematicEvent(CinematicEventType.HALTING_ANIMATION, area.getAttribute("name"),
					area.getAttribute("anim"), Integer.parseInt(area.getAttribute("time")));
		else if (type.equalsIgnoreCase("anim"))
			return new CinematicEvent(CinematicEventType.ANIMATION, area.getAttribute("name"),
					area.getAttribute("anim"), Integer.parseInt(area.getAttribute("time")),
					Boolean.parseBoolean(area.getAttribute("loops")));
		else if (type.equalsIgnoreCase("stopanim"))
			return new CinematicEvent(CinematicEventType.STOP_ANIMATION, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("cameramove"))
			return new CinematicEvent(CinematicEventType.CAMERA_MOVE, Integer.parseInt(area.getAttribute("x")),
					Integer.parseInt(area.getAttribute("y")), Integer.parseInt(area.getAttribute("time")));
		else if (type.equalsIgnoreCase("speech"))
			return new CinematicEvent(CinematicEventType.SPEECH, area.getAttribute("text"), Integer.parseInt(area.getAttribute("portrait")));
		else if (type.equalsIgnoreCase("loadmap"))
			return new CinematicEvent(CinematicEventType.LOAD_MAP, area.getAttribute("map"), area.getAttribute("enter"));
		else if (type.equalsIgnoreCase("loadbattle"))
			return new CinematicEvent(CinematicEventType.LOAD_BATTLE, area.getAttribute("textfile"), area.getAttribute("map"), area.getAttribute("entrance"), Integer.parseInt(area.getAttribute("battbg")));
		else if (type.equalsIgnoreCase("loadcin"))
			return new CinematicEvent(CinematicEventType.LOAD_CIN, area.getAttribute("map"), Integer.parseInt(area.getAttribute("cinid")));
		else if (type.equalsIgnoreCase("wait"))
			return new CinematicEvent(CinematicEventType.WAIT, Integer.parseInt(area.getAttribute("time")));
		else if (type.equalsIgnoreCase("spin"))
			return new CinematicEvent(CinematicEventType.SPIN, area.getAttribute("name"), Integer.parseInt(area.getAttribute("speed")),
					Integer.parseInt(area.getAttribute("time")));
		else if (type.equalsIgnoreCase("stopspin"))
			return new CinematicEvent(CinematicEventType.STOP_SPIN, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("facing"))
			return new CinematicEvent(CinematicEventType.FACING, area.getAttribute("name"), Integer.parseInt(area.getAttribute("dir")));
		else if (type.equalsIgnoreCase("shrink"))
			return new CinematicEvent(CinematicEventType.SHRINK, area.getAttribute("name"), Integer.parseInt(area.getAttribute("time")));
		else if (type.equalsIgnoreCase("grow"))
			return new CinematicEvent(CinematicEventType.GROW, area.getAttribute("name"), Integer.parseInt(area.getAttribute("time")));
		else if (type.equalsIgnoreCase("stopse"))
			return new CinematicEvent(CinematicEventType.STOP_SE, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("visible"))
			return new CinematicEvent(CinematicEventType.VISIBLE, area.getAttribute("name"), Boolean.parseBoolean(area.getAttribute("isvis")));
		else if (type.equalsIgnoreCase("removeactor"))
			return new CinematicEvent(CinematicEventType.REMOVE_ACTOR, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("quiver"))
			return new CinematicEvent(CinematicEventType.QUIVER, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("tremble"))
			return new CinematicEvent(CinematicEventType.TREMBLE, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("fallonface"))
			return new CinematicEvent(CinematicEventType.FALL_ON_FACE, area.getAttribute("name"), Integer.parseInt(area.getAttribute("dir")));
		else if (type.equalsIgnoreCase("layonsideright"))
			return new CinematicEvent(CinematicEventType.LAY_ON_SIDE_RIGHT, area.getAttribute("name"), Integer.parseInt(area.getAttribute("dir")));
		else if (type.equalsIgnoreCase("layonsideleft"))
			return new CinematicEvent(CinematicEventType.LAY_ON_SIDE_LEFT, area.getAttribute("name"), Integer.parseInt(area.getAttribute("dir")));
		else if (type.equalsIgnoreCase("layonback"))
			return new CinematicEvent(CinematicEventType.LAY_ON_BACK, area.getAttribute("name"), Integer.parseInt(area.getAttribute("dir")));
		else if (type.equalsIgnoreCase("flash"))
			return new CinematicEvent(CinematicEventType.FLASH, area.getAttribute("name"),
					Integer.parseInt(area.getAttribute("speed")), Integer.parseInt(area.getAttribute("time")));
		else if (type.equalsIgnoreCase("nod"))
			return new CinematicEvent(CinematicEventType.NOD, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("shakehead"))
			return new CinematicEvent(CinematicEventType.HEAD_SHAKE, area.getAttribute("name"), Integer.parseInt(area.getAttribute("time")));
		else if (type.equalsIgnoreCase("loopmove"))
			return new CinematicEvent(CinematicEventType.LOOP_MOVE, area.getAttribute("name"), Integer.parseInt(area.getAttribute("x")),
					Integer.parseInt(area.getAttribute("y")), Float.parseFloat(area.getAttribute("speed")));
		else if (type.equalsIgnoreCase("stoploopmove"))
			return new CinematicEvent(CinematicEventType.STOP_LOOP_MOVE, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("camerashake"))
			return new CinematicEvent(CinematicEventType.CAMERA_SHAKE, Integer.parseInt(area.getAttribute("time")), Integer.parseInt(area.getAttribute("severity")));
		else if (type.equalsIgnoreCase("playmusic"))
		{
			musicToLoad.add(area.getAttribute("music"));
			return new CinematicEvent(CinematicEventType.PLAY_MUSIC, area.getAttribute("music"), Integer.parseInt(area.getAttribute("volume")));
		}
		else if (type.equalsIgnoreCase("pausemusic"))
			return new CinematicEvent(CinematicEventType.PAUSE_MUSIC);
		else if (type.equalsIgnoreCase("resumemusic"))
			return new CinematicEvent(CinematicEventType.RESUME_MUSIC);
		else if (type.equalsIgnoreCase("fademusic"))
			return new CinematicEvent(CinematicEventType.FADE_MUSIC, Integer.parseInt(area.getAttribute("duration")));
		else if (type.equalsIgnoreCase("playsound"))
		{
			soundToLoad.add(area.getAttribute("sound"));
			return new CinematicEvent(CinematicEventType.PLAY_SOUND, area.getAttribute("sound"), Integer.parseInt(area.getAttribute("volume")));
		}
		else if (type.equalsIgnoreCase("fadein"))
		{
			if (Boolean.parseBoolean(area.getAttribute("init")))
			{
				initEvents.add(new CinematicEvent(CinematicEventType.FADE_FROM_BLACK, Integer.parseInt(area.getAttribute("time")), Boolean.parseBoolean(area.getAttribute("halting")), true));
				// return null;
			}
			return new CinematicEvent(CinematicEventType.FADE_FROM_BLACK, Integer.parseInt(area.getAttribute("time")), Boolean.parseBoolean(area.getAttribute("halting")), false);
		}
		else if (type.equalsIgnoreCase("fadeout"))
			return new CinematicEvent(CinematicEventType.FADE_TO_BLACK, Integer.parseInt(area.getAttribute("time")), Boolean.parseBoolean(area.getAttribute("halting")));
		else if (type.equalsIgnoreCase("flashscreen"))
			return new CinematicEvent(CinematicEventType.FLASH_SCREEN, Integer.parseInt(area.getAttribute("time")));
		else if (type.equalsIgnoreCase("rendertop"))
			return new CinematicEvent(CinematicEventType.MOVE_TO_FOREFRONT, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("rendernormal"))
			return new CinematicEvent(CinematicEventType.MOVE_FROM_FOREFRONT, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("exit"))
			return new CinematicEvent(CinematicEventType.EXIT_GAME);
		return null;
	}
}