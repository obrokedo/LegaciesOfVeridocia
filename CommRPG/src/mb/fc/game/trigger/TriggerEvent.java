package mb.fc.game.trigger;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.LoadMapMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.ai.AI;
import mb.fc.game.resource.ItemResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.sprite.StaticSprite;
import mb.fc.game.text.Speech;

public class TriggerEvent 
{
	private ArrayList<TriggerType> triggerTypes = new ArrayList<TriggerType>();
	private boolean retrigOnEnter;
	private boolean nonRetrig;
	private int id;
	
	public TriggerEvent(int id, boolean retrigOnEnter, boolean nonRetrig) {
		super();
		this.retrigOnEnter = retrigOnEnter;
		this.nonRetrig = nonRetrig;
		this.id = id;
	}

	public void addTriggerType(TriggerType tt)
	{
		triggerTypes.add(tt);
	}
	
	public void perform(StateInfo stateInfo)
	{
		if (nonRetrig)
		{
			if (stateInfo.getClientProgress().isNonretriggableTrigger(id))
			{
				// Check to see if this is a "on enter" perform for a retriggerable trigger that has already been triggered
				// If so we want this to be retriggered
				if (!stateInfo.isInitialized()) // && retrigOnEnter && stateInfo.getClientProgress().isPreviouslyTriggered(id))
				{
					performTriggerImpl(stateInfo);
				}
				return;
			}
			else
				stateInfo.getClientProgress().addNonretriggerableByMap(id);
		}
		
		if (retrigOnEnter && !stateInfo.getClientProgress().isPreviouslyTriggered(id))
			stateInfo.getClientProgress().addRetriggerableByMap(id);
		
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
			stateInfo.sendMessage(new IntMessage(Message.MESSAGE_COMPLETE_QUEST, questId));
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
			stateInfo.sendMessage(new LoadMapMessage(Message.MESSAGE_LOAD_MAP, map, null, location));
			return false;
		}
	}
	
	public class TriggerStartBattle extends TriggerType
	{
		private String battle;
		private String map;
		
		public TriggerStartBattle(String battle, String map) {
			super();
			this.battle = battle;
			this.map = map;
		}

		@Override
		public boolean perform(StateInfo stateInfo) 
		{
			stateInfo.sendMessage(new LoadMapMessage(Message.MESSAGE_START_BATTLE, map, battle, null));
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
			stateInfo.sendMessage(Message.MESSAGE_SHOW_PRIEST);
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
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_PLAY_MUSIC, song, .5f, true));
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
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, song, volume / 100.0f, false));
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
			stateInfo.sendMessage(new IntMessage(Message.MESSAGE_SHOW_CINEMATIC, cinematicId));
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
						if (!stateInfo.isQuestComplete(i))
							continue SPEECHLOOP;
					}
				}
				
				// Check to see if the excludes quests have been completed, if so
				// then we can't use this message
				if (s.getExcludes() != null && s.getExcludes().length > 0)
				{
					for (int i : s.getExcludes())
					{						
						if (stateInfo.isQuestComplete(i))
							continue SPEECHLOOP;
					}
				}
				
				stateInfo.sendMessage(new SpeechMessage(Message.MESSAGE_SPEECH, s.getMessage(), s.getTriggerId(), s.getPortraitId()));
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
		private Point p = null;
		
		public TriggerChangeAI(String speed, String id, String targetId, String x, String y)
		{
			this.id = Integer.parseInt(id);			
			if (targetId != null)
				this.targetId = Integer.parseInt(targetId);
			
			if (x != null && y != null)
			{
				p = new Point(Integer.parseInt(x), Integer.parseInt(y));
			}
			
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
								if (ts.getUniqueEnemyId() == targetId)
								{
									targetSprite = ts;
									break;
								}
							}
							
							if (targetSprite != null)
							{
								s.getAi().setApproachType(speed, targetSprite);
								System.out.println("Follow sprite " + targetSprite.getName());
							}
							
							break;
						case AI.APPROACH_MOVE_TO_POINT:
							System.out.println("Move to point " + p);
							s.getAi().setApproachType(speed, new Point(p.x * stateInfo.getTileWidth(), p.y * stateInfo.getTileHeight()));
							break;
						default:
							s.getAi().setApproachType(speed);
							break;
					}
					
					break;
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
}
