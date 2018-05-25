package mb.fc.game.manager;

import java.util.ArrayList;
import java.util.Iterator;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.message.SpriteMoveMessage;
import mb.fc.game.constants.Direction;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.move.MovingSprite;
import mb.fc.game.sprite.AnimatedSprite;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Door;
import mb.fc.game.sprite.Sprite;
import mb.fc.map.Map;
import mb.fc.map.Map.Stairs;

public class TownMoveManager extends Manager
{
	private ArrayList<MovingSprite> movers;
	private boolean moving = false;
	private int updateDelta = 0;
	public static int UPDATE_TIME = 20;
	private Stairs stairsHeroIsOn = null;

	@Override
	public void initialize() {
		movers = new ArrayList<MovingSprite>();
		moving = false;
	}

	public void update(int delta)
	{
		updateDelta += delta;
		boolean currentSpriteJustFinishedMoving = false;
		int moveRemainder = 0;

		for (int i = 0; i < movers.size(); i++)
		{
			MovingSprite ms = movers.get(i);

			if(ms.isFirstMove() && ms.getAnimatedSprite().getSpriteType() == Sprite.TYPE_COMBAT)
			{
				stateInfo.checkTriggersMovement((int) ms.getEndX(), (int) ms.getEndY(), true);
			}

			if (ms.update(delta))
			{
				movers.remove(i);
				ms.getAnimatedSprite().doneMoving(stateInfo);
				i--;

				if (ms.getAnimatedSprite().getSpriteType() == Sprite.TYPE_COMBAT) {
					stateInfo.checkTriggersMovement((int) ms.getAnimatedSprite().getLocX(), (int) ms.getAnimatedSprite().getLocY(), false);
				}	
				if (stateInfo.getCurrentSprite() == ms.getAnimatedSprite()) {
					if (stairsHeroIsOn == null)
						stairsHeroIsOn = stateInfo.getCurrentMap().isStartOfStairs(stateInfo.getCurrentSprite().getTileX(), 
							stateInfo.getCurrentSprite().getTileY());
					
					moving = false;
					currentSpriteJustFinishedMoving = true;
					moveRemainder = ms.getMoveRemainder();
				}
			}
		}

		if (currentSpriteJustFinishedMoving && moveRemainder > 0) {
			checkInput();

			for (int i = 0; i < movers.size(); i++)
			{
				if (stateInfo.getCurrentSprite() == movers.get(i).getAnimatedSprite()) {
					movers.get(i).updateWithRemainder(moveRemainder);
					break;
				}
			}
		}
		
		while (updateDelta >= UPDATE_TIME)
		{
			updateDelta -= UPDATE_TIME;

			checkInput();
		}

		stateInfo.getCamera().centerOnSprite(stateInfo.getCurrentSprite(), stateInfo.getCurrentMap());
	}

	private void checkInput()
	{
		/******************************************/
		/* Handle movement for the players Leader */
		/******************************************/
		CombatSprite current = stateInfo.getCurrentSprite();

		// Check to see if we are done moving
		if (!moving && !stateInfo.areMenusDisplayed())
		{
			int sx = current.getTileX();
			int sy = current.getTileY();
			switch (stateInfo.getInput().getMostRecentDirection())
			{
				case KeyMapping.BUTTON_RIGHT:
					if  (stairsHeroIsOn != null && !stairsHeroIsOn.isOnRightEntry(sx, sy)) {
						setMoving(Direction.RIGHT, current, stairsHeroIsOn);
					}
					else if (!blocked(stateInfo.getResourceManager().getMap(), sx + 1, sy)) {
						stairsHeroIsOn = null;
						setMoving(Direction.RIGHT, current, stairsHeroIsOn);
					}
					else
						current.setFacing(Direction.RIGHT);
					break;
				case KeyMapping.BUTTON_LEFT:
					if  (stairsHeroIsOn != null && !stairsHeroIsOn.isOnLeftEntry(sx, sy)) {
						setMoving(Direction.LEFT, current, stairsHeroIsOn);
					}
					else if (!blocked(stateInfo.getResourceManager().getMap(), sx - 1, sy)) {
						stairsHeroIsOn = null;
						setMoving(Direction.LEFT, current, stairsHeroIsOn);
					}
					else
						current.setFacing(Direction.LEFT);
					break;
				case KeyMapping.BUTTON_UP:
					if (stairsHeroIsOn != null && 
						!stairsHeroIsOn.isOnLeftEntry(sx, sy) &&
						!stairsHeroIsOn.isOnRightEntry(sx, sy)) {
						current.setFacing(Direction.UP);
					}
					else if (!blocked(stateInfo.getResourceManager().getMap(), sx, sy - 1)) {
						stairsHeroIsOn = null;
						setMoving(Direction.UP, current, stairsHeroIsOn);
					}
					else
						current.setFacing(Direction.UP);
					break;
				case KeyMapping.BUTTON_DOWN:
					if (stairsHeroIsOn != null && 
						!stairsHeroIsOn.isOnLeftEntry(sx, sy) &&
						!stairsHeroIsOn.isOnRightEntry(sx, sy)) {
						current.setFacing(Direction.DOWN);
					}
					else if (!blocked(stateInfo.getResourceManager().getMap(), sx, sy + 1)) {
						stairsHeroIsOn = null;
						setMoving(Direction.DOWN, current, stairsHeroIsOn);
					}
					else
						current.setFacing(Direction.DOWN);
					break;
			}
		}
	}

	private void setMoving(Direction direction, AnimatedSprite current, Stairs stairs)
	{
		if (current == stateInfo.getCurrentSprite())
			moving = true;
		stateInfo.sendMessage(new SpriteMoveMessage(current, direction, stairs));
		// recieveMessage(new SpriteMoveMessage(current, direction));
	}

	private boolean blocked(Map map, int tx, int ty)
	{
		if (tx >= 0 && ty >= 0 && map.getMapEffectiveHeight() > ty
				&& map.getMapEffectiveWidth() > tx && map.isMarkedMoveable(tx, ty))
		{
			for (Sprite s : stateInfo.getSprites())
			{
				if (s.getTileX() == tx && s.getTileY() == ty && !(s instanceof Door))
				{
					return true;
				}
			}
			
			for (MovingSprite ms : movers) {
				if (ms.getEndX() / stateInfo.getTileWidth() == tx && ms.getEndY() / stateInfo.getTileHeight() == ty) {
					return true;
				}
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
			case OVERLAND_MOVE_MESSAGE:
				SpriteMoveMessage m = (SpriteMoveMessage) message;
				AnimatedSprite sprite = m.getSprite(stateInfo.getSprites());
				
				if (m.getStairs() != null) {
					movers.add(new MovingSprite(sprite, m.getDirection(), m.getStairs(), stateInfo));
					return;
				}

				/*
				for (MovingSprite ms : movers)
				{
					if (ms.getAnimatedSprite().getId() == sprite.getId()) {
						System.out.println("ADD NEXT MOVMENT");
						ms.addNextMovement(m.getDirection());
						return;
					}
				}
				*/
				
				int sx = sprite.getTileX();
				int sy = sprite.getTileY();
				Direction dir = m.getDirection();

				boolean nowMoving = false;

				switch (dir)
				{
					case RIGHT:
						if (!blocked(stateInfo.getResourceManager().getMap(), sx + 1, sy))
							nowMoving = true;
						break;
					case LEFT:
						if (!blocked(stateInfo.getResourceManager().getMap(), sx - 1, sy))
							nowMoving = true;
						break;
					case UP:
						if (!blocked(stateInfo.getResourceManager().getMap(), sx, sy - 1))
							nowMoving = true;
						break;
					case DOWN:
						if (!blocked(stateInfo.getResourceManager().getMap(), sx, sy + 1))
							nowMoving = true;
				}

				if (nowMoving)
					movers.add(new MovingSprite(sprite, dir, stateInfo));
				else
					sprite.doneMoving(stateInfo);
				break;
			case INTIIALIZE:
				stateInfo.getCurrentMap().checkRoofs(
						(int) stateInfo.getCurrentSprite().getLocX(), (int) stateInfo.getCurrentSprite().getLocY());
				break;
			// If we are in town and a cinematic ends, make sure none of the "associated"
			// actors have "MovingSprites" associated with them
			case CIN_END:
				SpriteContextMessage scm = (SpriteContextMessage) message;
				ArrayList<Integer> sprites = scm.getSpriteIds();
				for (Integer s : sprites) {
					Iterator<MovingSprite> movingIt = movers.iterator();
					while (movingIt.hasNext()) {
						MovingSprite moving = movingIt.next();
						if (moving.getAnimatedSprite().getId() == s) {
							if (moving.getAnimatedSprite().getId() == stateInfo.getCurrentSprite().getId())
								this.moving = false;
							movingIt.remove();
						}
					}
				}
				break;
			default:
				break;
		}

	}
}
