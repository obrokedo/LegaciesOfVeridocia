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

import org.newdawn.slick.util.Log;

public class TownMoveManager extends Manager
{
	private ArrayList<MovingSprite> movers;
	private boolean moving = false;
	private int updateDelta = 0;
	public static int UPDATE_TIME = 20;

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
				ms.getAnimatedSprite().doneMoving();
				i--;

				if (ms.getAnimatedSprite().getSpriteType() == Sprite.TYPE_COMBAT)
					stateInfo.checkTriggersMovement((int) ms.getAnimatedSprite().getLocX(), (int) ms.getAnimatedSprite().getLocY(), false);
				if (stateInfo.getCurrentSprite() == ms.getAnimatedSprite()) {
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
	}

	private void setMoving(Direction direction, AnimatedSprite current)
	{
		if (current == stateInfo.getCurrentSprite())
			moving = true;
		stateInfo.sendMessage(new SpriteMoveMessage(current, direction));
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
		else
			Log.info("Blocked by tile");
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

				for (MovingSprite ms : movers)
				{
					if (ms.getAnimatedSprite().getId() == sprite.getId()) {
						ms.addNextMovement(m.getDirection());
						return;
					}
				}

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
