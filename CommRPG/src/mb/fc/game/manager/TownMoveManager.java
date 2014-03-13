package mb.fc.game.manager;

import java.util.ArrayList;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.OverlandMoveMessage;
import mb.fc.game.constants.Direction;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Sprite;

import org.newdawn.slick.Input;
import org.newdawn.slick.util.pathfinding.Path;

public class TownMoveManager extends Manager
{
	private ArrayList<MovingSprite> movers;
	private boolean moving = false;
	private Direction movingDirection;

	@Override
	public void initialize() {		
		// new OverlandMove(stateInfo);
		movers = new ArrayList<MovingSprite>();
	}
	
	public void update()
	{				
		/******************************************/
		/* Handle movement for the players Leader */
		/******************************************/
		CombatSprite current = stateInfo.getCurrentSprite();
		
		// Check to see if we are done moving
		if (!moving)
		{
			int sx = current.getTileX();
			int sy = current.getTileY();
			
			if (stateInfo.getGc().getInput().isKeyDown(Input.KEY_RIGHT))
			{
				if (!blocked(stateInfo.getResourceManager().getMap().getMapLayer(4), sx + 1, sy))
					setMoving(Direction.RIGHT, current);
				else
					current.setFacing(Direction.RIGHT);
			}
			else if (stateInfo.getGc().getInput().isKeyDown(Input.KEY_LEFT))
			{
				if (!blocked(stateInfo.getResourceManager().getMap().getMapLayer(4), sx - 1, sy))
					setMoving(Direction.LEFT, current);
				else
					current.setFacing(Direction.LEFT);
			}
			else if (stateInfo.getGc().getInput().isKeyDown(Input.KEY_UP))
			{
				if (!blocked(stateInfo.getResourceManager().getMap().getMapLayer(4), sx, sy - 1))
					setMoving(Direction.UP, current);
				else
					current.setFacing(Direction.UP);
			}
			else if (stateInfo.getGc().getInput().isKeyDown(Input.KEY_DOWN))
			{
				if (!blocked(stateInfo.getResourceManager().getMap().getMapLayer(4), sx, sy + 1))
					setMoving(Direction.DOWN, current);
				else
					current.setFacing(Direction.DOWN);
			}			
		}
		else
		{
			move(current);
		}
		
		for (int i = 0; i < movers.size(); i++)
		{
			MovingSprite ms = movers.get(i);
			
			if (ms.getMoveIndex() == ms.getPath().getLength())
			{				
				movers.remove(i);
				i--;
				continue;
			}					
			
			int moveToX = ms.getPath().getStep(ms.getMoveIndex()).getX() * stateInfo.getTileWidth();
			int moveToY = ms.getPath().getStep(ms.getMoveIndex()).getY() * stateInfo.getTileHeight();
							
			
			// Move Right
			if (moveToX > ms.getCombatSprite().getLocX())
				ms.getCombatSprite().setLocX(ms.getCombatSprite().getLocX() + 8);
			// Move Left
			else if (moveToX < ms.getCombatSprite().getLocX())
				ms.getCombatSprite().setLocX(ms.getCombatSprite().getLocX() - 8);
			// Move Down
			else if (moveToY > ms.getCombatSprite().getLocY())
				ms.getCombatSprite().setLocY(ms.getCombatSprite().getLocY() + 8);
			// Move Up
			else if (moveToY < ms.getCombatSprite().getLocY())
				ms.getCombatSprite().setLocY(ms.getCombatSprite().getLocY() - 8);
			
			if (ms.getCombatSprite().getLocX() % 32 == 0 && ms.getCombatSprite().getLocY() % 32 == 0) // ms.getOffsetIndex() == 3)
			{								
				stateInfo.checkTriggers(ms.getCombatSprite().getLocX(), ms.getCombatSprite().getLocY());
				ms.setOffsetIndex(0);
				ms.setMoveIndex(ms.getMoveIndex() + 1);				
			}
			else
				ms.setOffsetIndex(ms.getOffsetIndex() + 1);		
			
			// We are on a square now so check for triggers and see if we need to move the camera
			// if (ms.getOffsetIndex() == 0)
				// stateInfo.checkTriggers(ms.getCombatSprite().getLocX(), ms.getCombatSprite().getLocY());
			if (ms.getCombatSprite() == stateInfo.getCurrentSprite())
			{
				stateInfo.getCamera().centerOnSprite(ms.getCombatSprite(), stateInfo);
			}
		}
	}
	
	private void setMoving(Direction direction, CombatSprite current)
	{
		movingDirection = direction;
		moving = true;
		move(current);
	}
	
	private void move(CombatSprite current)
	{
		switch (movingDirection)
		{
			case UP:
				current.setLocY(current.getLocY() - 8);
				break;
			case DOWN:
				current.setLocY(current.getLocY() + 8);
				break;
			case LEFT:
				current.setLocX(current.getLocX() - 8);
				break;
			case RIGHT:
				current.setLocX(current.getLocX() + 8);
				break;
		}
		
		if (current.getLocX() % stateInfo.getTileWidth() == 0 && 
			current.getLocY() % stateInfo.getTileHeight() == 0)
		{
			stateInfo.checkTriggers(current.getLocX(), current.getLocY());
			moving = false;
		}
		stateInfo.getCamera().centerOnSprite(current, stateInfo);
	}
	
	private boolean blocked(int[][] tiles, int tx, int ty) 
	{
		if (tx >= 0 && ty >= 0 && tiles.length > ty && tiles[0].length > tx && tiles[ty][tx] != 0)
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
	}
	
	private class MovingSprite
	{
		private CombatSprite combatSprite;
		private Path path;
		private int moveIndex;
		private int offsetIndex;
		
		public MovingSprite(CombatSprite combatSprite, Path path) {
			super();
			this.combatSprite = combatSprite;
			this.path = path;
		}

		public CombatSprite getCombatSprite() {
			return combatSprite;
		}

		public Path getPath() {
			return path;
		}

		public int getMoveIndex() {
			return moveIndex;
		}

		public void setMoveIndex(int moveIndex) {
			this.moveIndex = moveIndex;
		}

		public int getOffsetIndex() {
			return offsetIndex;
		}

		public void setOffsetIndex(int offsetIndex) {
			this.offsetIndex = offsetIndex;
		}
	}
}
