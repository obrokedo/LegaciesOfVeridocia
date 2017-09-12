package mb.fc.game.trigger;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import org.newdawn.slick.util.Log;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleCondMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.LoadMapMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.message.ShowCinMessage;
import mb.fc.engine.message.StringMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.ai.AI;
import mb.fc.game.resource.HeroResource;
import mb.fc.game.resource.ItemResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.NPCSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.sprite.StaticSprite;
import mb.fc.game.text.Speech;
import mb.fc.map.MapObject;
import mb.fc.utils.StringUtils;

public class Trigger
{
	public static final int TRIGGER_NONE = -1;
	public static final int TRIGGER_ID_EXIT = -2;
	public static final int TRIGGER_SAVE = 3;

	private ArrayList<Triggerable> triggerables = new ArrayList<Triggerable>();

	private String name;
	private boolean retrigOnEnter;
	private boolean nonRetrig;
	private boolean triggerOnce;
	private boolean triggerImmediately;
	private boolean triggered = false;
	private String[] requires;
	private String[] excludes;
	private int id;
	
	public enum TriggerStatus {
		TRIGGERED,
		REQUIRED_QUEST_NOT_DONE,
		EXCLUDED_QUEST_DONE,
		NON_RETRIG,
		TRIGGER_ONCE,
		IS_IMMEDIATE
	}

	public Trigger(String name, int id, boolean retrigOnEnter, boolean nonRetrig,
			boolean triggerOnce, boolean triggerImmediately, String[] requires, String[] excludes) {
		super();
		this.name = name;
		this.retrigOnEnter = retrigOnEnter;
		this.nonRetrig = nonRetrig;
		this.triggerOnce = triggerOnce;
		this.triggerImmediately = triggerImmediately;
		this.id = id;
		this.requires = requires;
		this.excludes = excludes;
	}

	public void addTriggerable(Triggerable tt)
	{
		triggerables.add(tt);
	}

	public TriggerStatus perform(StateInfo stateInfo)
	{
		return perform(stateInfo, false);
	}

	public TriggerStatus perform(StateInfo stateInfo, boolean immediate)
	{
		Log.debug("Beginning Trigger Perform: " + this.id);
		if (triggerImmediately != immediate) {
			Log.debug("Trigger will not be executed, movement is immediate " + immediate + " trigger is immediate " + triggerImmediately);
			return TriggerStatus.IS_IMMEDIATE;
		}

		/* WHY IS THIS HERE?!??!?!?!
		if (!stateInfo.isInitialized() && this.id != 0) {
			Log.debug("Trigger will not be performed because the state has been changed");
			return;
		}
		*/

		// Check to see if this trigger meets all required quests
		if (requires != null)
		{
			for (String quest : requires)
			{
				if (StringUtils.isNotEmpty(quest) && !stateInfo.isQuestComplete(quest)) {
					Log.debug("Trigger will not be executed due to a failed requires " + quest);
					return TriggerStatus.REQUIRED_QUEST_NOT_DONE;
				}
			}
		}

		// Check to see if the excludes quests have been completed, if so
		// then we can't use this trigger
		if (excludes != null)
		{
			for (String quest : excludes)
			{
				if (StringUtils.isNotEmpty(quest) && stateInfo.isQuestComplete(quest)) {
					Log.debug("Trigger will not be executed due to a failed excludes " + quest);
					return TriggerStatus.EXCLUDED_QUEST_DONE;
				}
			}
		}

		if (nonRetrig)
		{
			if (stateInfo.getClientProgress().isNonretriggableTrigger(id))
			{
				// Check to see if this is a "on enter" perform for a retriggerable trigger that has already been triggered
				// If so we want this to be retriggered
				if (!stateInfo.isInitialized()) // && retrigOnEnter && stateInfo.getClientProgress().isPreviouslyTriggered(id))
				{
					Log.debug("Trigger will be performed on strange path");
					performTriggerImpl(stateInfo);
				}
				// The state has been changed and triggers should not be executed
				else
					Log.debug("Trigger will not be performed because the state has been changed on strange path");
				return TriggerStatus.NON_RETRIG;
			}
			else {
				stateInfo.getClientProgress().addNonretriggerableByMap(id);
			}
		}

		if (triggerOnce)
		{
			if (triggered) {
				Log.debug("Trigger will not be triggered as it has already been triggered once");
				return TriggerStatus.TRIGGER_ONCE;
			}
			else
				triggered = true;
		}

		if (retrigOnEnter && !stateInfo.getClientProgress().isPreviouslyTriggered(id))
		{
			stateInfo.getClientProgress().addRetriggerableByMap(id);
		}

		Log.debug("Trigger will be performed");
		performTriggerImpl(stateInfo);
		return TriggerStatus.TRIGGERED;
	}

	private void performTriggerImpl(StateInfo stateInfo)
	{
		Iterator<Triggerable> tt = triggerables.iterator();

		while (tt.hasNext())
			// I wonder why we remove certain triggerables... (Currently just AI changes?)
			if (tt.next().perform(stateInfo))
				tt.remove();
	}

	public class TriggerCompleteQuest implements Triggerable
	{
		private String questId;

		public TriggerCompleteQuest(String questId) {
			super();
			this.questId = questId;
		}
		
		@Override
		public boolean perform(StateInfo stateInfo) {
			Log.debug("Completing Quest: " + questId);
			stateInfo.sendMessage(new StringMessage(MessageType.COMPLETE_QUEST, questId));
			return false;
		}
	}

	public class TriggerEnter implements Triggerable
	{
		private String mapData;
		private String location;

		public TriggerEnter(String mapData, String location) {
			super();
			this.location = location;
			this.mapData = mapData;
		}

		@Override
		public boolean perform(StateInfo stateInfo)
		{
			stateInfo.sendMessage(new LoadMapMessage(MessageType.LOAD_MAP, mapData, location, 0), true);
			return false;
		}
	}

	public class TriggerStartBattle implements Triggerable
	{
		private String battle;
		private String entrance;
		private int battleBG;

		public TriggerStartBattle(String battle, String entrance, int battleBG) {
			super();
			this.battle = battle;
			this.entrance = entrance;
			this.battleBG = battleBG;
		}

		@Override
		public boolean perform(StateInfo stateInfo)
		{
			stateInfo.sendMessage(new LoadMapMessage(MessageType.START_BATTLE, battle, entrance, battleBG), true);
			return false;
		}
	}

	public class TriggerBattleCond implements Triggerable
	{
		private int[] leaderIds;
		private int[] enemyLeaderIds;
		private boolean killAllLeaders;

		public TriggerBattleCond(int[] leaderIds, int[] enemyLeaderIds, boolean killAllLeaders) {
			super();
			this.leaderIds = leaderIds;
			this.killAllLeaders = killAllLeaders;
			this.enemyLeaderIds = enemyLeaderIds;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(new BattleCondMessage(leaderIds, enemyLeaderIds, killAllLeaders), true);
			return false;
		}
	}

	public class TriggerShowShop implements Triggerable
	{
		private double buyPercent;
		private double sellPercent;
		private int[] itemIds;
		private String anims;

		public TriggerShowShop(String buyPercent, String sellPercent, int[] itemIds, String anim)
		{
			this.sellPercent = Double.parseDouble(sellPercent);
			this.buyPercent = Double.parseDouble(buyPercent);
			this.itemIds = itemIds;
			this.anims = anim;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(new ShopMessage(buyPercent, sellPercent, itemIds, anims));
			return false;
		}
	}

	public class TriggerShowPriest implements Triggerable
	{
		private String anim;
		
		
		
		public TriggerShowPriest(String anim) {
			super();
			this.anim = anim;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(new StringMessage(MessageType.SHOW_PRIEST, anim));
			return false;
		}
	}

	public class TriggerAddHero implements Triggerable
	{
		private int heroId;

		public TriggerAddHero(int heroId)
		{
			this.heroId = heroId;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.getPersistentStateInfo().getClientProfile().addHero(HeroResource.getHero(heroId));
			return false;
		}
	}

	public class TriggerById implements Triggerable
	{
		private int triggerId;

		public TriggerById(int triggerId)
		{
			this.triggerId = triggerId;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.getResourceManager().getTriggerEventById(triggerId).perform(stateInfo);
			return false;
		}
	}

	public class TriggerPlayMusic implements Triggerable
	{
		private String song;

		public TriggerPlayMusic(String song)
		{
			this.song = song;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(new AudioMessage(MessageType.PLAY_MUSIC, song, .5f, true));
			return false;
		}
	}

	public class TriggerPlaySound implements Triggerable
	{
		private String song;
		private int volume;

		public TriggerPlaySound(String song, int volume)
		{
			this.song = song;
			this.volume = volume;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, song, volume / 100.0f, false));
			return false;
		}
	}

	public class TriggerShowCinematic implements Triggerable
	{
		private int cinematicId;
		private int exitTriggerId;

		public TriggerShowCinematic(int id, int exitTriggerId)
		{
			cinematicId = id;
			this.exitTriggerId = exitTriggerId;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(new ShowCinMessage(cinematicId, exitTriggerId), true);
			return false;
		}
	}

	public class TriggerLoadCinematic implements Triggerable
	{
		private String mapData;
		private int cinematicId;
		public TriggerLoadCinematic(String mapData, int id)
		{
			this.mapData = mapData;
			cinematicId = id;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(new LoadMapMessage(MessageType.LOAD_CINEMATIC, mapData, cinematicId), true);
			return false;
		}
	}

	public class TriggerShowText implements Triggerable
	{
		private int textId;

		public TriggerShowText(int textId)
		{
			this.textId = textId;
		}

		@Override
		public boolean perform(StateInfo stateInfo)
		{
			Speech.showFirstSpeechMeetsReqs(textId, stateInfo);
			return false;
		}
	}

	public class TriggerChangeAI implements Triggerable
	{
		private int speed;
		private int id;
		private int targetId;
		private int heroTargetId;
		private Point p = null;

		public TriggerChangeAI(String speed, String id, String targetId, String heroTargetId, String x, String y)
		{
			this.id = Integer.parseInt(id);
			if (targetId != null)
				this.targetId = Integer.parseInt(targetId);
			if (heroTargetId != null)
				this.heroTargetId = Integer.parseInt(heroTargetId);

			if (x != null && y != null)
			{
				p = new Point(Integer.parseInt(x), Integer.parseInt(y));
			}

			try
			{
				this.speed = Integer.parseInt(speed);
			}
			catch (NumberFormatException ex)
			{
				if (speed.equalsIgnoreCase("fast"))
					this.speed = AI.APPROACH_KAMIKAZEE;
				else if (speed.equalsIgnoreCase("slow"))
					this.speed = AI.APPROACH_HESITANT;
				else if (speed.equalsIgnoreCase("wait"))
					this.speed = AI.APPROACH_REACTIVE;
				else if (speed.equalsIgnoreCase("follow"))
					this.speed = AI.APPROACH_FOLLOW;
				else if (speed.equalsIgnoreCase("moveto"))
					this.speed = AI.APPROACH_MOVE_TO_POINT;
			}
		}

		@Override
		public boolean perform(StateInfo stateInfo)
		{
			for (CombatSprite s : stateInfo.getCombatSprites())
			{
				if (s.getUniqueEnemyId() == id)
				{
					switch (speed)
					{
						case AI.APPROACH_FOLLOW:
							CombatSprite targetSprite = null;
							for (CombatSprite ts : stateInfo.getCombatSprites())
							{
								if (ts.getUniqueEnemyId() == targetId && ts.getCurrentHP() > 0)
								{
									targetSprite = ts;
									break;
								}
							}

							if (targetSprite != null)
							{
								s.getAi().setApproachType(speed, targetSprite);
								Log.debug("Follow sprite " + targetSprite.getName());
							}

							break;
							// TODO MAY NEED TO COME BACK TO THIS AS THE PLANNER ALLOWS AN ARBITRARY NUMBER HERE, HOW USUABLE IS THIS?
						case AI.APPROACH_TARGET:
							CombatSprite target = stateInfo.getHeroById(heroTargetId);
							if (target.getCurrentHP() > 0)
							{
								s.getAi().setApproachType(speed, target);
								Log.debug("Target sprite " + target.getName());
							}
							break;
						case AI.APPROACH_MOVE_TO_POINT:
							Log.debug("Move to point " + p);
							s.getAi().setApproachType(speed, new Point(p.x * stateInfo.getTileWidth(), p.y * stateInfo.getTileHeight()));
							break;
						default:
							s.getAi().setApproachType(speed);
							break;
					}
				}
			}

			return true;
		}
	}

	public class TriggerToggleRoof implements Triggerable
	{
		private int roofId;
		private boolean show;

		public TriggerToggleRoof(int id, boolean showRoof)
		{
			roofId = id;
			this.show = showRoof;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.getResourceManager().getMap().getRoofById(roofId).setVisible(show);
			return false;
		}
	}
	
	public class TriggerRunTriggers implements Triggerable {
		private int[] triggers;

		public TriggerRunTriggers(int[] triggers) {
			super();
			this.triggers = triggers;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			for (int trig : triggers) {
				stateInfo.getResourceManager().getTriggerEventById(trig).perform(stateInfo);
			}
			return false;
		}
	}
	
	public class TriggerAddSprite implements Triggerable
	{
		private String spriteName;
		private String image;
		private int[] searchTriggers;
		private String locationName;

		public TriggerAddSprite(String spriteName, String image, int[] searchTriggers, String locationName) {
			super();
			this.spriteName = spriteName;
			this.image = image;
			this.searchTriggers = searchTriggers;
			this.locationName = locationName;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			for (MapObject mo : stateInfo.getCurrentMap().getMapObjects()) {
				if (locationName.equalsIgnoreCase(mo.getName())) {
					stateInfo.addSprite(mo.getSprite(spriteName, image, searchTriggers, stateInfo.getResourceManager()));
					break;
				}
			}
			return false;
		}
	}
	
	public class TriggerChangeNPCAnimation implements Triggerable {
		private String animation;
		private String npcName;
		
		public TriggerChangeNPCAnimation(String animation, String npcName) {
			super();
			this.animation = animation;
			this.npcName = npcName;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			for (Sprite sprite : stateInfo.getSprites()) {
				if (sprite.getSpriteType() == Sprite.TYPE_NPC &&
						npcName.equalsIgnoreCase(sprite.getName())) {
					NPCSprite npc = (NPCSprite) sprite;
					npc.setSpriteAnims(stateInfo.getResourceManager().getSpriteAnimation(animation));
					npc.setFacing(npc.getFacing());
					break;
				}
			}
				
			return false;
		}
		
	}
	
	public class TriggerAddNpc implements Triggerable
	{
		private int textId;
		private String name;
		private String animation;
		private Integer facing;
		private Integer wander;
		private Integer npcId;
		private String locationName;

		public TriggerAddNpc(int textId, String name, String animation, Integer facing, Integer wander, Integer npcId,
				String locationName) {
			super();
			this.textId = textId;
			this.name = name;
			this.animation = animation;
			this.facing = facing;
			this.wander = wander;
			this.npcId = npcId;
			this.locationName = locationName;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			for (MapObject mo : stateInfo.getCurrentMap().getMapObjects()) {
				if (locationName.equalsIgnoreCase(mo.getName())) {
					stateInfo.addSprite(mo.getNPC(textId, name, animation, facing, wander, npcId, stateInfo.getResourceManager()));
					break;
				}
			}
			return false;
		}
	}

	public class TriggerRemoveSprite implements Triggerable
	{
		private String spriteName;

		public TriggerRemoveSprite(String spriteName) {
			super();
			this.spriteName = spriteName;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			Iterator<Sprite> spriteIt = stateInfo.getSpriteIterator();
			while (spriteIt.hasNext())
			{
				Sprite s = spriteIt.next();

				if (s.getName() != null && s.getName().equalsIgnoreCase(spriteName))
				{
					spriteIt.remove();
					return false;
				}
			}
			return false;
		}
	}

	public class TriggerChangeSprite implements Triggerable
	{
		private String spriteName;
		private String newImage;

		public TriggerChangeSprite(String spriteName, String newImage) {
			super();
			this.spriteName = spriteName;
			this.newImage = newImage;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			Iterator<Sprite> spriteIt = stateInfo.getSpriteIterator();
			while (spriteIt.hasNext())
			{
				Sprite s = spriteIt.next();

				if (s.getName() != null && s.getName().equalsIgnoreCase(spriteName))
				{
					StaticSprite ss = (StaticSprite) s;
					ss.setImage(stateInfo.getResourceManager().getImage(newImage));
					return false;
				}
			}
			return false;
		}
	}

	public class TriggerAddItem implements Triggerable
	{
		private int itemId;

		public TriggerAddItem(int itemId) {
			super();
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			for (CombatSprite hero : stateInfo.getAllHeroes())
			{
				if (hero.getItemsSize() != 4)
				{
					hero.addItem(ItemResource.getItem(itemId, stateInfo.getResourceManager()));
					break;
				}
			}

			return false;
		}
	}

	public class TriggerExit implements Triggerable
	{
		@Override
		public boolean perform(StateInfo stateInfo) {
			System.exit(0);
			return false;
		}

	}
	
	public class TriggerReviveHeroes implements Triggerable
	{
		@Override
		public boolean perform(StateInfo stateInfo) {
			for (CombatSprite cs : stateInfo.getAllHeroes())
				cs.setCurrentHP(cs.getMaxHP());
			return false;
		}
		
	}
	
	public class TriggerKillEnemies implements Triggerable
	{
		private int unitId;
		
		public TriggerKillEnemies(int unitId) {
			this.unitId = unitId;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			for (CombatSprite cs : stateInfo.getCombatSprites()) {
				if (!cs.isHero() && cs.getUniqueEnemyId() == unitId) {
					cs.setCurrentHP(-1);
				}
			}
			
			return false;
		}
		
	}

	public String getName() {
		return name;
	}
}
