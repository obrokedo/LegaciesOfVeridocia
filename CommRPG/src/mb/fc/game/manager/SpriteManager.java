package mb.fc.game.manager;

import java.util.Iterator;

import mb.fc.engine.message.Message;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.map.MapObject;

public class SpriteManager extends Manager
{
	@Override
	public void initialize() 
	{		
		
	}
	
	private void initializeAfterSprites()
	{
		// If we are not in combat then get this clients main character and set them as the current sprite
		if (!stateInfo.isCombat())
		{
			for (CombatSprite cs : stateInfo.getHeroes())
				if (cs.isLeader())
				{
					stateInfo.setCurrentSprite(cs);
					stateInfo.addSprite(cs);
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
				defaultEntrance.getStartLocation(stateInfo);
			
			stateInfo.getCamera().centerOnSprite(stateInfo.getCurrentSprite(), stateInfo);
		}
		// Otherwise just add all of the heroes
		else
		{
			for (CombatSprite cs : stateInfo.getHeroes())
				cs.initializeSprite(stateInfo);
			
			stateInfo.addAllCombatSprites(stateInfo.getHeroes());				
			
			
			// Get any npcs from the map
			for (MapObject mo : stateInfo.getResourceManager().getMap().getMapObjects())
			{
				// TODO This should automatically start the "BATTLE START"
				if (mo.getKey().equalsIgnoreCase("start") && mo.getParam("exit").equalsIgnoreCase("battle"))
				{
					mo.getStartLocation(stateInfo);
				}
				if (mo.getKey().equalsIgnoreCase("enemy"))
				{
					stateInfo.addSprite(mo.getEnemy(stateInfo));
				}
			}
		}
	}
	
	public void update()
	{
		boolean isEnemyAlive = false;

		Iterator<Sprite> spriteItr = stateInfo.getSpriteIterator();
				
		while (spriteItr.hasNext())
		{
			Sprite s = spriteItr.next();
			s.update();
			if (s.getSpriteType() == Sprite.TYPE_COMBAT)
			{
				if (((CombatSprite) s).getCurrentHP() < -255)
				{
					stateInfo.removeCombatSprite((CombatSprite) s);
					s.destroy(stateInfo);
					spriteItr.remove();					
				}
				// If the sprite did not die, then check to see if it is an enemy, if so the battle is not over 
				else if (!((CombatSprite) s).isHero())
					isEnemyAlive = true;
			}
		}
		
		if (!isEnemyAlive && stateInfo.isCombat())
			stateInfo.getResourceManager().getTriggerEventById(1).perform(stateInfo);
	}

	@Override
	public void recieveMessage(Message message) 
	{
		switch (message.getMessageType())
		{			
			case Message.MESSAGE_INTIIALIZE:
				initializeAfterSprites();
				break;
		}
	}
}
