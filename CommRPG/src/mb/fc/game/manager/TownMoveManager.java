package mb.fc.game.manager;

import java.util.ArrayList;

import mb.fc.engine.message.Message;
import mb.fc.game.constants.Direction;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.move.MovingSprite;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;
import mb.fc.map.Map;

public class TownMoveManager extends Manager
{
	private ArrayList<MovingSprite> movers;
	private boolean moving = false;
	private int updateDelta = 0;
	public static int UPDATE_TIME = 20;	

	@Override
	public void initialize() {		
		// new OverlandMove(stateInfo);
		movers = new ArrayList<MovingSprite>();
		moving = false;
	}
	
	public void update(int delta)
	{		
		updateDelta += delta;
		if (updateDelta >= UPDATE_TIME)
		{
			updateDelta -= UPDATE_TIME;
			/******************************************/
			/* Handle movement for the players Leader */
			/******************************************/
			CombatSprite current = stateInfo.getCurrentSprite();
			
			// Check to see if we are done moving
			if (!moving)
			{
				int sx = current.getTileX();
				int sy = current.getTileY();
				
				switch (stateInfo.getInput().getMostRecentDirection())
				{
					case KeyMapping.BUTTON_RIGHT:
						if (!blocked(stateInfo.getResourceManager().getMap(), sx + 1, sy))
							setMoving(Direction.RIGHT, current);
						else
							current.setFacing(Direction.RIGHT);
						break;
					case KeyMapping.BUTTON_LEFT:
						if (!blocked(stateInfo.getResourceManager().getMap(), sx - 1, sy))
							setMoving(Direction.LEFT, current);
						else
							current.setFacing(Direction.LEFT);
						break;
					case KeyMapping.BUTTON_UP:
						if (!blocked(stateInfo.getResourceManager().getMap(), sx, sy - 1))
							setMoving(Direction.UP, current);
						else
							current.setFacing(Direction.UP);
						break;
					case KeyMapping.BUTTON_DOWN:
						if (!blocked(stateInfo.getResourceManager().getMap(), sx, sy + 1))
							setMoving(Direction.DOWN, current);
						else
							current.setFacing(Direction.DOWN);
						break;
				}		
			}
			
			for (int i = 0; i < movers.size(); i++)
			{
				MovingSprite ms = movers.get(i);
				
				if (ms.update())
				{
					movers.remove(i);
					i--;
					if (stateInfo.getCurrentSprite() == ms.getCombatSprite())
					{
						stateInfo.checkTriggers(stateInfo.getCurrentSprite().getLocX(), stateInfo.getCurrentSprite().getLocY());
						moving = false;
					}
				}
			}
		}
	}
	
	private void setMoving(Direction direction, CombatSprite current)
	{
		if (current == stateInfo.getCurrentSprite())		
			moving = true;
		movers.add(new MovingSprite(current, direction, stateInfo));
	}
	
	private boolean blocked(Map map, int tx, int ty) 
	{
		if (tx >= 0 && ty >= 0 && map.getMapEffectiveHeight() > ty && map.getMapEffectiveWidth() > tx && map.isMarkedMoveable(tx, ty))
		{
			for (Sprite s : stateInfo.getSprites())
			{
				if (s.getTileX() == tx && s.getTileY() == ty)
					return true;
			}
			
			return false;
		}
		return true;
	}

	@Override
	public void recieveMessage(Message message) 
	{
		/*
		switch (message.getMessageType())
		{
			case Message.MESSAGE_OVERLAND_MOVE_MESSAGE:
				CombatSprite cs = (CombatSprite) ((OverlandMoveMessage) message).getSprite();
				
				MovingSprite ms = new MovingSprite(cs, 
						((OverlandMoveMessage) message).getPath());
				
				for (int i = 0; i < movers.size(); i++)
				{
					if (movers.get(i).getCombatSprite() == cs)
					{
						movers.remove(i);	
						ms.setMoveIndex(1);
						break;
					}
				}
				
				movers.add(ms);
				break;
		}
		*/
	}
}
