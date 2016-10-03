package mb.fc.game.trigger;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleCondMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.LoadMapMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.ai.AI;
import mb.fc.game.resource.HeroResource;
import mb.fc.game.resource.ItemResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.sprite.StaticSprite;
import mb.fc.game.text.Speech;

import org.newdawn.slick.util.Log;

public class TriggerEvent
{
	public static final int TRIGGER_ID_EXIT = -2;

	private ArrayList<TriggerType> triggerTypes = new ArrayList<TriggerType>();

	private boolean retrigOnEnter;
	private boolean nonRetrig;
	private boolean triggerOnce;
	private boolean triggerImmediately;
	private boolean triggered = false;
	private int[] requires;
	private int[] excludes;
	private int id;

	public TriggerEvent(int id, boolean retrigOnEnter, boolean nonRetrig,
			boolean triggerOnce, boolean triggerImmediately, int[] requires, int[] excludes) {
		super();
		this.retrigOnEnter = retrigOnEnter;
		this.nonRetrig = nonRetrig;
		this.triggerOnce = triggerOnce;
		this.triggerImmediately = triggerImmediately;
		this.id = id;
		this.requires = requires;
		this.excludes = excludes;
	}

	public void addTriggerType(TriggerType tt)
	{
		triggerTypes.add(tt);
	}

	public void perform(StateInfo stateInfo)
	{
		perform(stateInfo, false);
	}

	public void perform(StateInfo stateInfo, boolean immediate)
	{
		Log.debug("Beginning Trigger Perform: " + this.id);
		if (triggerImmediately != immediate) {
			Log.debug("Trigger will not be executed, movement is immediate " + immediate + " trigger is immediate " + triggerImmediately);
			return;
		}

		if (!stateInfo.isInitialized() && this.id != 0) {
			Log.debug("Trigger will not be performed because the state has been changed");
			return;
		}

		// Check to see if this trigger meets all required quests
		if (requires != null)
		{
			for (int i : requires)
			{
				if (i != -1 && !stateInfo.isQuestComplete(i)) {
					Log.debug("Trigger will not be executed due to a failed requires " + i);
					return;
				}
			}
		}

		// Check to see if the excludes quests have been completed, if so
		// then we can't use this trigger
		if (excludes != null)
		{
			for (int i : excludes)
			{
				if (i != -1 && stateInfo.isQuestComplete(i)) {
					Log.debug("Trigger will not be executed due to a failed excludes " + i);
					return;
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
				return;
			}
			else {
				stateInfo.getClientProgress().addNonretriggerableByMap(id);
			}
		}

		if (triggerOnce)
		{
			if (triggered) {
				Log.debug("Trigger will not be triggered as it has already been triggered once");
				return;
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
	}

	private void performTriggerImpl(StateInfo stateInfo)
	{
		Iterator<TriggerType> tt = triggerTypes.iterator();

		while (tt.hasNext())
			if (tt.next().perform(stateInfo))
				tt.remove();
	}

	public class TriggerCompleteQuest extends TriggerType
	{
		private int questId;

		public TriggerCompleteQuest(int questId) {
			super();
			this.questId = questId;
		}

		public int getQuestId() {
			return questId;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			Log.debug("Completing Quest: " + questId);
			stateInfo.sendMessage(new IntMessage(MessageType.COMPLETE_QUEST, questId));
			return false;
		}
	}

	public class TriggerEnter extends TriggerType
	{
		private String map;
		private String location;

		public TriggerEnter(String map, String location) {
			super();
			this.map = map;
			this.location = location;
		}

		@Override
		public boolean perform(StateInfo stateInfo)
		{
			stateInfo.sendMessage(new LoadMapMessage(MessageType.LOAD_MAP, map, null, location, 0), true);
			return false;
		}
	}

	public class TriggerStartBattle extends TriggerType
	{
		private String battle;
		private String map;
		private String entrance;
		private int battleBG;

		public TriggerStartBattle(String battle, String map, String entrance, int battleBG) {
			super();
			this.battle = battle;
			this.map = map;
			this.entrance = entrance;
			this.battleBG = battleBG;
		}

		@Override
		public boolean perform(StateInfo stateInfo)
		{
			stateInfo.sendMessage(new LoadMapMessage(MessageType.START_BATTLE, map, battle, entrance, battleBG), true);
			return false;
		}
	}

	public class TriggerBattleCond extends TriggerType
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

	public class TriggerShowShop extends TriggerType
	{
		private double buyPercent;
		private double sellPercent;
		private int[] itemIds;

		public TriggerShowShop(String params)
		{
			String[] split = params.split(",");
			sellPercent = Double.parseDouble(split[0]);
			buyPercent = Double.parseDouble(split[1]);
			itemIds = new int[split.length - 2];

			for (int i = 2; i < split.length; i++)
			{
				itemIds[i - 2] = Integer.parseInt(split[i]);
			}
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(new ShopMessage(buyPercent, sellPercent, itemIds));
			return false;
		}
	}

	public class TriggerShowPriest extends TriggerType
	{
		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(MessageType.SHOW_PRIEST);
			return false;
		}
	}

	public class TriggerAddHero extends TriggerType
	{
		private int heroId;

		public TriggerAddHero(int heroId)
		{
			this.heroId = heroId;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.getPsi().getClientProfile().addHero(HeroResource.getHero(heroId));
			return false;
		}
	}

	public class TriggerById extends TriggerType
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

	public class TriggerPlayMusic extends TriggerType
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

	public class TriggerPlaySound extends TriggerType
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

	public class TriggerShowCinematic extends TriggerType
	{
		private int cinematicId;

		public TriggerShowCinematic(int id)
		{
			cinematicId = id;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(new IntMessage(MessageType.SHOW_CINEMATIC, cinematicId), true);
			return false;
		}
	}

	public class TriggerLoadCinematic extends TriggerType
	{
		private String map;
		private int cinematicId;
		public TriggerLoadCinematic(String map, int id)
		{
			this.map = map;
			cinematicId = id;
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			stateInfo.sendMessage(new LoadMapMessage(MessageType.LOAD_CINEMATIC, map, cinematicId), true);
			return false;
		}
	}

	public class TriggerShowText extends TriggerType
	{
		private int textId;

		public TriggerShowText(int textId)
		{
			this.textId = textId;
		}

		@Override
		public boolean perform(StateInfo stateInfo)
		{
			SPEECHLOOP: for (Speech s : stateInfo.getResourceManager().getSpeechesById(textId))
			{
				// Check to see if this mesage meets all required quests
				if (s.getRequires() != null && s.getRequires().length > 0)
				{
					for (int i : s.getRequires())
					{
						if (i != -1 && !stateInfo.isQuestComplete(i))
							continue SPEECHLOOP;
					}
				}

				// Check to see if the excludes quests have been completed, if so
				// then we can't use this message
				if (s.getExcludes() != null && s.getExcludes().length > 0)
				{
					for (int i : s.getExcludes())
					{
						if (i != -1 && stateInfo.isQuestComplete(i))
							continue SPEECHLOOP;
					}
				}

				stateInfo.sendMessage(new SpeechMessage(s.getMessage(), s.getTriggerId(), s.getPortrait(stateInfo)), true);
				break;
			}

			return false;
		}
	}

	public class TriggerChangeAI extends TriggerType
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
							if (heroTargetId < stateInfo.getHeroes().size() && stateInfo.getHeroes().get(heroTargetId).getCurrentHP() > 0)
							{
								s.getAi().setApproachType(speed, stateInfo.getHeroes().get(heroTargetId));
								Log.debug("Target sprite " + stateInfo.getHeroes().get(heroTargetId).getName());
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

	public class TriggerToggleRoof extends TriggerType
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

	public class TriggerRemoveSprite extends TriggerType
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

	public class TriggerChangeSprite extends TriggerType
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
					ss.setImage(stateInfo.getResourceManager().getImages().get(newImage));
					return false;
				}
			}
			return false;
		}
	}

	public class TriggerAddItem extends TriggerType
	{
		private int itemId;

		public TriggerAddItem(int itemId) {
			super();
		}

		@Override
		public boolean perform(StateInfo stateInfo) {
			for (CombatSprite hero : stateInfo.getClientProfile().getHeroes())
			{
				if (hero.getItemsSize() != 4)
				{
					hero.addItem(ItemResource.getItem(itemId, stateInfo));
					break;
				}
			}

			return false;
		}
	}

	public class TriggerExit extends TriggerType
	{
		@Override
		public boolean perform(StateInfo stateInfo) {
			System.exit(0);
			return false;
		}

	}
}
