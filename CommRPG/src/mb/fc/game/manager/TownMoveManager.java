package mb.fc.game.manager;

import java.util.ArrayList;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.SpriteMoveMessage;
import mb.fc.game.constants.Direction;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.move.MovingSprite;
import mb.fc.game.sprite.AnimatedSprite;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.sprite.Door;
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

				if(ms.isFirstMove() && ms.getAnimatedSprite().getSpriteType() == Sprite.TYPE_COMBAT)
				{
					stateInfo.checkTriggers(ms.getEndX(), ms.getEndY(), true);
				}

				if (ms.update())
				{
					movers.remove(i);
					ms.getAnimatedSprite().doneMoving();
					i--;

					if (ms.getAnimatedSprite().getSpriteType() == Sprite.TYPE_COMBAT)
						stateInfo.checkTriggers(ms.getAnimatedSprite().getLocX(), ms.getAnimatedSprite().getLocY(), false);
					if (stateInfo.getCurrentSprite() == ms.getAnimatedSprite())
						moving = false;
				}
			}
		}
	}

	private void setMoving(Direction direction, AnimatedSprite current)
	{
		if (current == stateInfo.getCurrentSprite())
			moving = true;
		stateInfo.sendMessage(new SpriteMoveMessage(current, direction));
		// movers.add(new MovingSprite(current, direction, stateInfo));
	}

	private boolean blocked(Map map, int tx, int ty)
	{
		if (tx >= 0 && ty >= 0 && map.getMapEffectiveHeight() > ty && map.getMapEffectiveWidth() > tx && map.isMarkedMoveable(tx, ty))
		{
			for (Sprite s : stateInfo.getSprites())
			{
				if (s.getTileX() == tx && s.getTileY() == ty && !(s instanceof Door))
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
			case OVERLAND_MOVE_MESSAGE:
				SpriteMoveMessage m = (SpriteMoveMessage) message;
				AnimatedSprite sprite = m.getSprite(stateInfo.getSprites());

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
					sprite.doneMoving();
				break;
			default:
				break;
		}

	}
}
