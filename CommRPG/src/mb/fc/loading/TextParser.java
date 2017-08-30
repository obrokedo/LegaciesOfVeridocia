package mb.fc.loading;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.newdawn.slick.SlickException;

import mb.fc.cinematic.Cinematic;
import mb.fc.cinematic.event.CinematicEvent;
import mb.fc.cinematic.event.CinematicEvent.CinematicEventType;
import mb.fc.game.exception.BadResourceException;
import mb.fc.game.text.Conversation;
import mb.fc.game.text.Speech;
import mb.fc.game.text.YesNoSpeech;
import mb.fc.game.trigger.Conditional;
import mb.fc.game.trigger.Trigger;
import mb.fc.game.trigger.TriggerCondition;
import mb.fc.game.trigger.TriggerCondition.EnemyInBattle;
import mb.fc.game.trigger.TriggerCondition.HeroEntersLocation;
import mb.fc.game.trigger.TriggerCondition.HeroInBattle;
import mb.fc.game.trigger.TriggerCondition.LocationContainsUnits;
import mb.fc.game.trigger.TriggerCondition.UnitDeath;
import mb.fc.game.trigger.Triggerable;
import mb.fc.utils.StringUtils;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;

public class TextParser
{
	public String parseText(String file, Hashtable<Integer, ArrayList<Speech>> speechesById,
			Hashtable<Integer, Trigger> triggerEventById, Hashtable<Integer, Cinematic> cinematicById,
			HashSet<TriggerCondition> conditions, FCResourceManager frm) throws IOException, SlickException
	{
		HashSet<String> animToLoad = new HashSet<String>();
		HashSet<String> soundToLoad = new HashSet<String>();
		HashSet<String> musicToLoad = new HashSet<String>();
		HashSet<String> spriteToLoad = new HashSet<String>();

		String mapName = null;
		
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
					String message = childTagArea.getAttribute("message");
					String requires = childTagArea.getAttribute("require");
					String excludes = childTagArea.getAttribute("exclude");
					String trigger = childTagArea.getAttribute("trigger");
					
					String triggerNo = null;
					if (childTagArea.getTagType().equalsIgnoreCase("yesno")) {
						trigger = childTagArea.getAttribute("triggeryes");
						triggerNo = childTagArea.getAttribute("triggerno");
					}
					
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
					
					if (childTagArea.getTagType().equalsIgnoreCase("string")) {
						String customAnim = childTagArea.getAttribute("animportrait");
						if (StringUtils.isEmpty(customAnim))
							customAnim = null;

						speeches.add(new Speech(message, requireIds, excludeIds, triggerId,
								Integer.parseInt(childTagArea.getAttribute("heroportrait")),
								Integer.parseInt(childTagArea.getAttribute("enemyportrait")),
								customAnim));
					} else if (childTagArea.getTagType().equalsIgnoreCase("yesno")) {
						String customAnim = childTagArea.getAttribute("animportrait");
						if (StringUtils.isEmpty(customAnim))
							customAnim = null;

						speeches.add(new YesNoSpeech(message, requireIds, excludeIds, triggerId, Integer.parseInt(triggerNo),
								Integer.parseInt(childTagArea.getAttribute("heroportrait")),
								Integer.parseInt(childTagArea.getAttribute("enemyportrait")),
								customAnim));
					} else if (childTagArea.getTagType().equalsIgnoreCase("conversation")) {
						String customAnim1 = childTagArea.getAttribute("animportrait1");
						if (StringUtils.isEmpty(customAnim1))
							customAnim1 = null;
						String customAnim2 = childTagArea.getAttribute("animportrait2");
						if (StringUtils.isEmpty(customAnim2))
							customAnim2 = null;
						speeches.add(new Conversation(message.split("<split>"), requireIds, excludeIds, triggerId, 
								Integer.parseInt(childTagArea.getAttribute("heroportrait1")),
								Integer.parseInt(childTagArea.getAttribute("enemyportrait1")),
								customAnim1,
								Integer.parseInt(childTagArea.getAttribute("heroportrait2")),
								Integer.parseInt(childTagArea.getAttribute("enemyportrait2")),
								customAnim2));
					}

					
				}
				speechesById.put(id, speeches);
			}
			else if (tagArea.getTagType().equalsIgnoreCase("trigger"))
			{
				parseTrigger(triggerEventById, soundToLoad, musicToLoad, spriteToLoad, tagArea);
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
			}
			else if (tagArea.getTagType().equalsIgnoreCase("condition"))
			{
				TriggerCondition condition = new TriggerCondition(tagArea.getIntAttribute("triggerid"),
						tagArea.getAttribute("description"));
				for (TagArea ta : tagArea.getChildren())
				{
					if (ta.getTagType().equalsIgnoreCase("enemydeath"))
					{
						int unitId = ta.getIntAttribute("unitid");
						condition.addCondition(new UnitDeath(unitId, true));
					}
					else if (ta.getTagType().equalsIgnoreCase("herodeath"))
					{
						int unitId = ta.getIntAttribute("unitid");
						condition.addCondition(new UnitDeath(unitId, false));
					}
					else if (ta.getTagType().equalsIgnoreCase("enterloc"))
					{
						String location = ta.getAttribute("location");
						condition.addCondition(new HeroEntersLocation(location, ta.getBoolAttribute("immediate")));
					}
					else if (ta.getTagType().equalsIgnoreCase("loccontains"))
					{
						condition.addCondition(new LocationContainsUnits(
								ta.getAttribute("location"), 
								ta.getBoolAttribute("enemy"), 
								ta.getIntAttribute("amount"),
								ta.getAttribute("operator")));
					}
					else if (ta.getTagType().equalsIgnoreCase("heroinbat"))
					{
						condition.addCondition(new HeroInBattle(ta.getIntAttribute("id")));
					}
					else if (ta.getTagType().equalsIgnoreCase("enemyinbat"))
					{
						condition.addCondition(new EnemyInBattle(ta.getIntAttribute("id")));
					}
					else
					{
						Conditional conditional = handleCustomCondition(ta.getTagType(), ta.getParams());
						if (conditional == null)
							throw new BadResourceException("Unable to parse conditional with key: " + ta.getTagType());
						else
							condition.addCondition(conditional);
					}
				}
				conditions.add(condition);
			} else if (tagArea.getTagType().equalsIgnoreCase("map")) {
				mapName = tagArea.getAttribute("file");
			}
		}
		
		if (mapName == null) {
			throw new BadResourceException("The selected mapdata does not have a map associated with it. Please assign one via the planner");
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
		
		return mapName;
	}

	private void parseTrigger(Hashtable<Integer, Trigger> triggerEventById, HashSet<String> soundToLoad,
			HashSet<String> musicToLoad, HashSet<String> spriteToLoad, TagArea tagArea) {
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

		Trigger te = new Trigger(id, retrigOnEnter, nonRetrig, triggerOnce, triggerImmediately, requireIds, excludeIds);
		if (tagArea.getChildren().size() > 0)
		{
			for (int k = 0; k < tagArea.getChildren().size(); k++)
			{
				Hashtable<String, String> actionParams = tagArea.getChildren().get(k).getParams();

				String tagType = tagArea.getChildren().get(k).getTagType();
				if (tagType.equalsIgnoreCase("completequest"))
				{
					te.addTriggerable(te.new TriggerCompleteQuest(Integer.parseInt(actionParams.get("questid"))));
				}
				else if (tagType.equalsIgnoreCase("startbattle"))
				{
					int battleBackgroundIndex = 0;

					if (actionParams.containsKey("battbg"))
						battleBackgroundIndex = Integer.parseInt(actionParams.get("battbg"));
					te.addTriggerable(te.new TriggerStartBattle(actionParams.get("battletriggers"),
							actionParams.get("entrance"), battleBackgroundIndex));
				}
				else if (tagType.equalsIgnoreCase("setbattlecond"))
				{
					te.addTriggerable(te.new TriggerBattleCond(
							parsePositiveMultiInt("templeader", actionParams),
							parsePositiveMultiInt("tempenemyleader", actionParams), 
							Boolean.parseBoolean(actionParams.get("allleaders"))));
				}
				else if (tagType.equalsIgnoreCase("loadmap"))
				{
					te.addTriggerable(te.new TriggerEnter(actionParams.get("mapdata"), actionParams.get("enter")));
				}
				else if (tagType.equalsIgnoreCase("showshop"))
				{
					te.addTriggerable(te.new TriggerShowShop(actionParams.get("buypercent"), actionParams.get("sellpercent"),
							parsePositiveMultiInt("itemssold", actionParams), actionParams.get("portrait")));
				}
				else if (tagType.equalsIgnoreCase("showpriest"))
				{
					te.addTriggerable(te.new TriggerShowPriest(actionParams.get("portrait")));
				}
				else if (tagType.equalsIgnoreCase("addhero"))
				{
					te.addTriggerable(te.new TriggerAddHero(Integer.parseInt(actionParams.get("heroid"))));
				}
				else if (tagType.equalsIgnoreCase("playmusic"))
				{
					te.addTriggerable(te.new TriggerPlayMusic(actionParams.get("music")));
					musicToLoad.add(actionParams.get("music"));
				}
				else if (tagType.equalsIgnoreCase("playsound"))
				{
					te.addTriggerable(te.new TriggerPlaySound(actionParams.get("sound"), Integer.parseInt(actionParams.get("volume"))));
					soundToLoad.add(actionParams.get("sound"));
				}
				else if (tagType.equalsIgnoreCase("changeai"))
				{
					te.addTriggerable(te.new TriggerChangeAI(actionParams.get("aitype"),
							actionParams.get("id"), actionParams.get("targetid"), actionParams.get("heroid"),
							actionParams.get("x"), actionParams.get("y")));
				}
				else if (tagType.equalsIgnoreCase("showtext"))
				{
					te.addTriggerable(te.new TriggerShowText(Integer.parseInt(actionParams.get("textid"))));
				}
				else if (tagType.equalsIgnoreCase("showcin"))
				{
					te.addTriggerable(te.new TriggerShowCinematic(Integer.parseInt(actionParams.get("cinid"))));
				}
				else if (tagType.equalsIgnoreCase("loadcin"))
				{
					te.addTriggerable(te.new TriggerLoadCinematic(actionParams.get("mapdata"), Integer.parseInt(actionParams.get("cinid"))));
				}
				else if (tagType.equalsIgnoreCase("showroof"))
				{
					te.addTriggerable(te.new TriggerToggleRoof(Integer.parseInt(actionParams.get("roofid")), true));
				}
				else if (tagType.equalsIgnoreCase("hideroof"))
				{
					te.addTriggerable(te.new TriggerToggleRoof(Integer.parseInt(actionParams.get("roofid")), false));
				}
				else if (tagType.equalsIgnoreCase("addnpc")) {
					Integer facing = null;
					Integer wander = null;
					Integer uniqueId = null;
					if (actionParams.containsKey("facing"))
						facing = Integer.parseInt(actionParams.get("facing"));
					if (actionParams.containsKey("wander"))
						wander = Integer.parseInt(actionParams.get("wander"));
					if (actionParams.containsKey("uniqueid"))
						uniqueId = Integer.parseInt(actionParams.get("uniqueid"));
					
					te.addTriggerable(te.new TriggerAddNpc(Integer.parseInt(actionParams.get("textid")), actionParams.get("name"), 
							actionParams.get("animation"), facing, wander, uniqueId, actionParams.get("location")));
				}
				else if (tagType.equalsIgnoreCase("changenpc")) {
					te.addTriggerable(te.new TriggerChangeNPCAnimation(actionParams.get("animation"), actionParams.get("name")));
				} else if (tagType.equalsIgnoreCase("addsprite")) {
					int[] searchTriggers = null;
					if (actionParams.containsKey("searchtrigger")) {
						int trig = Integer.parseInt(actionParams.get("searchtrigger"));
						if (trig >= 0) {
							searchTriggers = new int[] {trig};
						}
					}
					te.addTriggerable(te.new TriggerAddSprite(actionParams.get("name"), actionParams.get("image"), searchTriggers, actionParams.get("location")));
				}
				else if (tagType.equalsIgnoreCase("removesprite"))
				{
					te.addTriggerable(te.new TriggerRemoveSprite(actionParams.get("name")));
				}
				else if (tagType.equalsIgnoreCase("changesprite"))
				{
					te.addTriggerable(te.new TriggerChangeSprite(actionParams.get("name"), actionParams.get("image")));
					spriteToLoad.add(actionParams.get("image"));
				}
				else if (tagType.equalsIgnoreCase("additem"))
				{
					te.addTriggerable(te.new TriggerAddItem(Integer.parseInt(actionParams.get("itemid"))));
				} else if (tagType.equalsIgnoreCase("runtriggers")) {
					te.addTriggerable(te.new TriggerRunTriggers(parsePositiveMultiInt("triggers", actionParams)));
				}
				else if (tagType.equalsIgnoreCase("exit"))
				{
					te.addTriggerable(te.new TriggerExit());
				}
				else if (tagType.equalsIgnoreCase("reviveheroes"))
				{
					te.addTriggerable(te.new TriggerReviveHeroes());
				}
				else if (tagType.equalsIgnoreCase("killenemies"))
				{
					te.addTriggerable(te.new TriggerKillEnemies(Integer.parseInt(actionParams.get("unitid"))));
				}
				else
				{
					Triggerable trigger = handleCustomTrigger(tagType, actionParams);
					if (trigger == null)
						throw new BadResourceException("Unable to parse trigger with key " + tagType);
					else
						te.addTriggerable(trigger);
				}

			}
		}

		triggerEventById.put(id, te);
	}
	
	private static int[] parsePositiveMultiInt(String tag, Hashtable<String, String> actionParams)
	{
		String leaders = actionParams.get(tag);
		int[] leaderIds = new int[0];

		if (leaders != null)
		{
			String[] splitLeader = leaders.split(",");
			leaderIds = new int[splitLeader.length];
			for (int i = 0; i < splitLeader.length; i++)
				leaderIds[i] = Integer.parseInt(splitLeader[i]);
			
			if (leaderIds.length == 1 && leaderIds[0] == -1)
				leaderIds = new int[0];
		}
		
		return leaderIds;
	}

	public ArrayList<CinematicEvent> parseCinematicEvents(TagArea tagArea, ArrayList<CinematicEvent> initEvents,
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

	private CinematicEvent parseCinematicEvent(TagArea area, ArrayList<CinematicEvent> initEvents,
			HashSet<String> animToLoad, HashSet<String> musicToLoad, HashSet<String> soundToLoad)
	{
		String type = area.getTagType();

		if (type.equalsIgnoreCase("addactor"))
		{
			CinematicEvent ce = new CinematicEvent(CinematicEventType.ADD_ACTOR, Integer.parseInt(area.getAttribute("x")),
					Integer.parseInt(area.getAttribute("y")),
					area.getAttribute("name"), area.getAttribute("anim"),
					area.getAttribute("startanim"), Boolean.parseBoolean(area.getAttribute("visible")), Integer.parseInt(area.getAttribute("associatedhero")));
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
					Integer.parseInt(area.getAttribute("hero")), Integer.parseInt(area.getAttribute("enemyid")), Integer.parseInt(area.getAttribute("npcid")));
		else if (type.equalsIgnoreCase("camerafollow"))
			return new CinematicEvent(CinematicEventType.CAMERA_FOLLOW, area.getAttribute("name"));
		else if (type.equalsIgnoreCase("haltingmove"))
			return new CinematicEvent(CinematicEventType.HALTING_MOVE, Integer.parseInt(area.getAttribute("x")),
					Integer.parseInt(area.getAttribute("y")), Float.parseFloat(area.getAttribute("speed")), area.getAttribute("name"),
					Boolean.parseBoolean(area.getAttribute("movehor")), Boolean.parseBoolean(area.getAttribute("movediag")));
		else if (type.equalsIgnoreCase("haltingmovepath"))
			return new CinematicEvent(CinematicEventType.HALTING_MOVE_PATHFIND, Integer.parseInt(area.getAttribute("x")),
					Integer.parseInt(area.getAttribute("y")), Float.parseFloat(area.getAttribute("speed")), area.getAttribute("name"),
					false, false);
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
		else if (type.equalsIgnoreCase("cameramovetoactor"))
			return new CinematicEvent(CinematicEventType.CAMERA_MOVE_TO_ACTOR, area.getAttribute("actor"), Integer.parseInt(area.getAttribute("time")));
		else if (type.equalsIgnoreCase("speech"))
			return new CinematicEvent(CinematicEventType.SPEECH, area.getAttribute("text"),
					(area.getAttribute("heroportrait") == null ? -1 : Integer.parseInt(area.getAttribute("heroportrait"))),
							(area.getAttribute("enemyportrait") == null ? -1 : Integer.parseInt(area.getAttribute("enemyportrait"))),
							area.getAttribute("animportrait"));
		else if (type.equalsIgnoreCase("loadmap"))
			return new CinematicEvent(CinematicEventType.LOAD_MAP, area.getAttribute("mapdata"), area.getAttribute("enter"));
		else if (type.equalsIgnoreCase("loadbattle"))
			return new CinematicEvent(CinematicEventType.LOAD_BATTLE, area.getAttribute("mapdata"), area.getAttribute("entrance"), Integer.parseInt(area.getAttribute("battbg")));
		else if (type.equalsIgnoreCase("loadcin"))
			return new CinematicEvent(CinematicEventType.LOAD_CIN, area.getAttribute("mapdata"), Integer.parseInt(area.getAttribute("cinid")));
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
		else if (type.equalsIgnoreCase("addhero"))
		{
			CinematicEvent ce = new CinematicEvent(CinematicEventType.ADD_HERO, Integer.parseInt(area.getAttribute("heroid")));
			if (Boolean.parseBoolean(area.getAttribute("init")))
			{
				initEvents.add(ce);
				return null;
			}
			return ce;
		} else {
			CinematicEvent cinematicEvent = handleCustomCinematic(type, area.getParams());
			if (cinematicEvent == null)
				throw new BadResourceException("Unable to parse cinematic event with key " + type);
			else
				return cinematicEvent;
		}
	}
	
	public CinematicEvent handleCustomCinematic(String tag, Hashtable<String, String> params)
	{
		return null;
	}
	
	public Triggerable handleCustomTrigger(String tag, Hashtable<String, String> params)
	{
		return null;
	}
	
	public Conditional handleCustomCondition(String tag, Hashtable<String, String> params)
	{
		return null;
	}
}