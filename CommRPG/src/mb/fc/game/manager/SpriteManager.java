package mb.fc.game.manager;

import java.awt.Point;
import java.util.HashSet;
import java.util.Iterator;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.BattleCondMessage;
import mb.fc.engine.message.BooleanMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.game.definition.EnemyDefinition;
import mb.fc.game.dev.BattleOptimizer;
import mb.fc.game.exception.BadMapException;
import mb.fc.game.resource.NPCResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.NPCSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.game.sprite.StaticSprite;
import mb.fc.game.trigger.Trigger;
import mb.fc.map.MapObject;

public class SpriteManager extends Manager
{
	private int updateDelta = 0;
	private static final int UPDATE_TIME = 50;
	private HashSet<CombatSprite> heroLeaders;
	private HashSet<Integer> temporaryLeaderEnemyIds;
	private boolean killAllHeroLeaders = false;
	private boolean battleConditionsSet = false;
	private static BattleOptimizer battleOptimizer = new BattleOptimizer();

	@Override
	public void initialize()
	{
		this.battleConditionsSet = false;
		this.temporaryLeaderEnemyIds = new HashSet<Integer>();
	}

	private void initializeAfterSprites(boolean fromLoad)
	{
		EnemyDefinition.resetEnemyIds();
		NPCResource.resetNPCIds();

		// If we are not in combat then get this clients main character and set them as the current sprite
		if (!stateInfo.isCombat())
		{
			for (CombatSprite cs : stateInfo.getHeroesInState())
			{
				if (cs.isLeader())
				{
					if (cs.getClientId() == stateInfo.getPersistentStateInfo().getClientId())
					{
						stateInfo.setCurrentSprite(cs);
					}
					stateInfo.addSprite(cs);
				}
			}

			// Even though this is not the leader, the sprites need to be initialized so we can
			// view items, spells and pictures
			for (CombatSprite cs : stateInfo.getAllHeroes())
			{
				cs.initializeSprite(stateInfo.getResourceManager());
				cs.initializeStats();
			}

			boolean foundStart = false;
			String entranceLocation = stateInfo.getEntranceLocation();

			// Get any npcs from the map
			for (MapObject mo : stateInfo.getResourceManager().getMap().getMapObjects())
			{
				if (mo.getKey().equalsIgnoreCase("npc"))
				{
					stateInfo.addSprite(mo.getNPC(stateInfo.getResourceManager()));
				}
				// Depending on where we are initializing this map from the entrance location could be null.
				// If we are loading a map for the first time or via transition from another map then this
				// value will be set. If we are loading from save then we will just use the absolute sprite
				// absolute sprite location
				else if (entranceLocation != null && mo.getKey().equalsIgnoreCase("start")
						&& mo.getParam("exit").equalsIgnoreCase(entranceLocation))
				{
					mo.placeSpritesAtStartLocation(stateInfo);
					foundStart = true;
				}
			}
			
			if (entranceLocation == null) {
				Point savedPoint = stateInfo.getClientProgress().getInTownLocation();
				if (savedPoint == null)
					throw new BadMapException("The selected map does not contain a start location or a start point. Your save file may be bad");
				
				// Use integer division to place the hero at the nearest tile
				stateInfo.getCurrentSprite().setLocation((savedPoint.x / stateInfo.getTileWidth()) * stateInfo.getTileWidth(), 
						(savedPoint.y / stateInfo.getTileHeight()) * stateInfo.getTileHeight(), stateInfo.getTileWidth(), stateInfo.getTileHeight());
			}
			else if (!foundStart)
			{
				throw new BadMapException("The selected map does not contain a start location with the name " + entranceLocation);
			}

			stateInfo.getCamera().centerOnSprite(stateInfo.getCurrentSprite(), stateInfo.getCurrentMap());
		}
		// Otherwise just add all of the heroes
		else
		{
			// If the battle conditions weren't set then initialize
			// uninitialized values
			if (!this.battleConditionsSet) {
				killAllHeroLeaders = false;
				heroLeaders = new HashSet<CombatSprite>();
			// Reset the battle conditions for the next battle
			} else
				this.battleConditionsSet = false;
			
		
			// Initialize all of the heroes, this just sets images and initializes effects.
			// Stats are initialized above for all heroes
			for (CombatSprite cs : stateInfo.getAllHeroes())
				cs.initializeSprite(stateInfo.getResourceManager());
			
			
			// Regardless of whether or not this is a loaded mid-way battle
			// or a new battle, initialize the hero list, do NOT initialize
			// the stats
			for (CombatSprite cs : stateInfo.getHeroesInState())
			{
				// Do not allow dead sprites to appear in the battle
				if (cs.getCurrentHP() <= 0)
					continue;

				// Only initialize stats if this is a "new" battle
				if (!fromLoad)
					cs.initializeStats();
				
				if (cs.isLeader())
					heroLeaders.add(cs);
			}

			// Since this battle is not loaded we need to add all of the heroes and enemies
			// to the state and place them in their starting locations
			if (!fromLoad) 
			{
				stateInfo.addAllCombatSprites(stateInfo.getHeroesInState());

				// Get any npcs from the map
				for (MapObject mo : stateInfo.getResourceManager().getMap().getMapObjects())
				{
					// TODO This should automatically start the "BATTLE START"
					if (mo.getKey().equalsIgnoreCase("start") && mo.getParam("exit").equalsIgnoreCase(stateInfo.getEntranceLocation()))
					{
						mo.placeSpritesAtStartLocation(stateInfo);
					}
					
					if (mo.getKey().equalsIgnoreCase("enemy"))
					{
						CombatSprite cs = mo.getEnemy(stateInfo.getResourceManager());
						
						if (CommRPG.BATTLE_MODE_OPTIMIZE)
							battleOptimizer.modifyStats(cs);
							
						// Enemies do not need to be initialized as it was 
						// already done via the .getEnemy() method above
						cs.initializeStats();
						
						if (temporaryLeaderEnemyIds.contains(cs.getId()))
							cs.setLeader(true);
						stateInfo.addSprite(cs);
					}
					
					// We purposely don't remove the combat sprites until this point so that their
					// spots are still taken up in the start location (as empty spaces). Otherwise
					// it could completely fuck up split battles... but so could less people in the party...
					Iterator<CombatSprite> csIt = stateInfo.getHeroesInState().iterator();
					while (csIt.hasNext())
					{
						CombatSprite cs = csIt.next();
						if (cs.getCurrentHP() <= 0)
							csIt.remove();
					}
				}
			// Since the enemies already exist in the combat sprite list
			// we don't need to add them, but they do need to be "initialized"s
			} else {
				// Initialize all of the heroes, this just sets images and initializes effects.
				// Stats are initialized above for all heroes
				for (CombatSprite cs : stateInfo.getAllHeroes())
					cs.initializeSprite(stateInfo.getResourceManager());
				
				int nextEnemyId = 0;
				for (CombatSprite cs : stateInfo.getCombatSprites())
				{
					if (!cs.isHero())
					{
						cs.initializeSprite(stateInfo.getResourceManager());
						// The target location of AI may be incorrect, fix that here
						if (cs.getAi() != null)
							cs.getAi().reinitialize(stateInfo);
						nextEnemyId = Math.min(nextEnemyId, cs.getId());
					}
				}
				EnemyDefinition.setNextEnemyId(nextEnemyId - 1);
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
				s.update(stateInfo);
				if (s.getSpriteType() == Sprite.TYPE_COMBAT)
				{
					CombatSprite cs = (CombatSprite) s;
					if (cs.getCurrentHP() < -255)
					{
						spriteItr.remove();
						stateInfo.removeCombatSprite(cs);
						s.destroy(stateInfo);

						if (cs.isHero())
						{
							// This returns TRUE if a leader was removed
							if (heroLeaders.remove(cs))
							{
								if (heroLeaders.size() == 0 || !killAllHeroLeaders)
								{
									if (CommRPG.BATTLE_MODE_OPTIMIZE)
										loadBattleOptmize(false);
									else
										loadBattleDefeat();
								}
							}
						}
						else if (cs.isLeader())
						{
							if (CommRPG.BATTLE_MODE_OPTIMIZE)
								loadBattleOptmize(true);
							else
								loadBattleVictory();
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

			// If we are in battle and there are no enemies alive then the
			// battle has been won! Trigger the default "battle over" trigger
			if (!isEnemyAlive && stateInfo.isCombat())
			{
				/*
				 * Clear these values here so that when we load a battle
				 * we don't think that we're loading from a mid-battle save
				 */
				stateInfo.getClientProgress().setCurrentTurn(null);
				stateInfo.getClientProgress().setBattleSprites(null);
				
				if (CommRPG.BATTLE_MODE_OPTIMIZE)
				{
					loadBattleOptmize(true);
				}
				else
					loadBattleVictory();
			}
		}
	}
	
	private void loadBattleOptmize(boolean won)
	{
		// Set everyones hit points to full so that no one starts dead
		for (CombatSprite cs : stateInfo.getAllHeroes())
			cs.setCurrentHP(cs.getMaxHP());
		if (won)
			this.battleOptimizer.wonBattle();
		else
			this.battleOptimizer.lostBattle();
		stateInfo.getPersistentStateInfo().loadBattle(stateInfo.getClientProgress().getMapData(), 
				stateInfo.getPersistentStateInfo().getEntranceLocation(), 0);
	}
	
	private void loadBattleVictory()
	{
		stateInfo.getResourceManager().getTriggerEventById(1).perform(stateInfo);
	}
	
	private void loadBattleDefeat()
	{
		stateInfo.sendMessage(new SpeechMessage("You have been defeated...]", Trigger.TRIGGER_ID_EXIT, null));
	}

	@Override
	public void recieveMessage(Message message)
	{
		switch (message.getMessageType())
		{
			case INTIIALIZE:
				initializeAfterSprites(((BooleanMessage) message).isBool());
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
					this.heroLeaders.add(stateInfo.getHeroById(i));
				}
				break;
			default:
				break;
		}
	}
}
