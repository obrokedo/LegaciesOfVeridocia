package mb.fc.game.manager;

import java.util.HashSet;
import java.util.Iterator;

import mb.fc.engine.message.BattleCondMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.game.definition.EnemyDefinition;
import mb.fc.game.exception.BadMapException;
import mb.fc.game.resource.NPCResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.NPCSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.sprite.StaticSprite;
import mb.fc.game.trigger.TriggerEvent;
import mb.fc.map.MapObject;

public class SpriteManager extends Manager
{
	private int updateDelta = 0;
	private static final int UPDATE_TIME = 50;
	private HashSet<CombatSprite> heroLeaders;
	private HashSet<Integer> temporaryLeaderEnemyIds;
	private boolean killAllHeroLeaders = false;
	private boolean battleConditionsSet = false;

	@Override
	public void initialize()
	{
		this.battleConditionsSet = false;
		this.temporaryLeaderEnemyIds = new HashSet<Integer>();
	}

	private void initializeAfterSprites()
	{
		EnemyDefinition.resetEnemyIds();
		NPCResource.resetNPCIds();

		// If we are not in combat then get this clients main character and set them as the current sprite
		if (!stateInfo.isCombat())
		{
			for (CombatSprite cs : stateInfo.getHeroes())
			{
				if (cs.isLeader())
				{
					if (cs.getClientId() == stateInfo.getPsi().getClientId())
					{
						stateInfo.setCurrentSprite(cs);
					}
					stateInfo.addSprite(cs);
				}
			}

			// Even though this is not the leader, the sprites need to be initialized so we can
			// view items, spells and pictures
			for (CombatSprite cs : stateInfo.getClientProfile().getHeroes())
				cs.initializeSprite(stateInfo);

			boolean foundStart = false;
			MapObject defaultEntrance = null;

			// Get any npcs from the map
			for (MapObject mo : stateInfo.getResourceManager().getMap().getMapObjects())
			{
				if (mo.getKey().equalsIgnoreCase("npc"))
				{
					stateInfo.addSprite(mo.getNPC(stateInfo));
				}
				else if (mo.getKey().equalsIgnoreCase("start"))
				{
					if (mo.getParam("exit").equalsIgnoreCase(stateInfo.getEntranceLocation()))
					{
						mo.getStartLocation(stateInfo);
						foundStart = true;
					}
					else
						defaultEntrance = mo;
				}
			}

			if (!foundStart)
			{
				if (defaultEntrance != null)
					defaultEntrance.getStartLocation(stateInfo);
				else
					throw new BadMapException("The selected map does not contain a start location");
			}

			stateInfo.getCamera().centerOnSprite(stateInfo.getCurrentSprite(), stateInfo.getCurrentMap());
		}
		// Otherwise just add all of the heroes
		else
		{

			if (!this.battleConditionsSet) {
				killAllHeroLeaders = false;
				heroLeaders = new HashSet<CombatSprite>();
			}
			battleConditionsSet = true;
			for (CombatSprite cs : stateInfo.getHeroes())
			{
				// Do not allow dead sprites to appear in the battle
				if (cs.getCurrentHP() <= 0)
					continue;

				cs.initializeSprite(stateInfo);
				if (cs.isLeader())
					heroLeaders.add(cs);
			}

			stateInfo.addAllCombatSprites(stateInfo.getHeroes());

			// Get any npcs from the map
			for (MapObject mo : stateInfo.getResourceManager().getMap().getMapObjects())
			{
				// TODO This should automatically start the "BATTLE START"
				if (mo.getKey().equalsIgnoreCase("start") && mo.getParam("exit").equalsIgnoreCase(stateInfo.getEntranceLocation()))
				{
					mo.getStartLocation(stateInfo);
				}
				if (mo.getKey().equalsIgnoreCase("enemy"))
				{
					CombatSprite cs = mo.getEnemy(stateInfo);
					if (temporaryLeaderEnemyIds.contains(cs.getId()))
						cs.setLeader(true);
					stateInfo.addSprite(mo.getEnemy(stateInfo));
				}
			}

			/*
			AIController aic = new AIController();
			aic.initialize(stateInfo.getCombatSprites());
			*/
		}
	}

	public void update(int delta)
	{
		updateDelta += delta;
		while (updateDelta >= UPDATE_TIME)
		{
			updateDelta -= UPDATE_TIME;
			boolean isEnemyAlive = false;

			// TODO Is this to cumbersome, could move it when people move around the map?
			stateInfo.sortSprites();
			Iterator<Sprite> spriteItr = stateInfo.getSpriteIterator();
			while (spriteItr.hasNext())
			{
				Sprite s = spriteItr.next();
				s.update();
				if (s.getSpriteType() == Sprite.TYPE_COMBAT)
				{
					CombatSprite cs = (CombatSprite) s;
					if (cs.getCurrentHP() < -255)
					{
						stateInfo.removeCombatSprite(cs);
						s.destroy(stateInfo);
						spriteItr.remove();

						if (cs.isHero())
						{
							// This returns TRUE if a leader was removed
							if (heroLeaders.remove(cs))
							{
								if (heroLeaders.size() == 0 || !killAllHeroLeaders)
									stateInfo.sendMessage(new SpeechMessage("You have been defeated...]", TriggerEvent.TRIGGER_ID_EXIT, null));
							}
						}
						else if (cs.isLeader())
						{
							stateInfo.getResourceManager().getTriggerEventById(1).perform(stateInfo);
						}
						
						if (cs.isHero())
							stateInfo.checkTriggersHeroDeath(cs);
						else
							stateInfo.checkTriggersEnemyDeath(cs);
					}
					// If the sprite did not die, then check to see if it is an enemy, if so the battle is not over
					else if (!((CombatSprite) s).isHero())
						isEnemyAlive = true;
				}
			}

			if (!isEnemyAlive && stateInfo.isCombat())
				stateInfo.getResourceManager().getTriggerEventById(1).perform(stateInfo);
		}
	}

	@Override
	public void recieveMessage(Message message)
	{
		switch (message.getMessageType())
		{
			case INTIIALIZE:
				initializeAfterSprites();
				break;
			case INVESTIGATE:
				int checkX = stateInfo.getCurrentSprite().getTileX();
				int checkY = stateInfo.getCurrentSprite().getTileY();

				switch (stateInfo.getCurrentSprite().getFacing())
				{
					case UP:
						checkY--;
						break;
					case DOWN:
						checkY++;
						break;
					case LEFT:
						checkX--;
						break;
					case RIGHT:
						checkX++;
						break;
				}

				for (Sprite s : stateInfo.getSprites())
				{
					if (s.getSpriteType() == Sprite.TYPE_NPC)
					{
						NPCSprite npc = (NPCSprite) s;
						if (npc.getTileX() == checkX &&
								npc.getTileY() == checkY)
						{
							npc.triggerButton1Event(stateInfo);
							break;
						}

					}
					else if (s.getSpriteType() == Sprite.TYPE_STATIC_SPRITE)
					{
						StaticSprite ss = (StaticSprite) s;
						if (s.getTileX() == checkX &&
								ss.getTileY() == checkY)
						{
							ss.triggerButton1Event(stateInfo);
							break;
						}
					}
				}
				break;
			case BATTLE_COND:
				BattleCondMessage bcm = (BattleCondMessage) message;
				heroLeaders = new HashSet<CombatSprite>();
				this.killAllHeroLeaders = bcm.isKillAllLeaders();
				this.battleConditionsSet = true;
				this.temporaryLeaderEnemyIds = new HashSet<>();
				
				for (int id : bcm.getEnemyLeaderIds())
				{
					temporaryLeaderEnemyIds.add(id);
				}
				
				for (Integer i : bcm.getLeaderIds())
				{
					System.out.println(heroLeaders + " " + stateInfo.getHeroes());
					this.heroLeaders.add(stateInfo.getHeroes().get(i));
				}
				break;
			default:
				break;
		}
	}
}
