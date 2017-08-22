package mb.fc.game.move;

import java.util.LinkedList;
import java.util.Queue;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.sprite.AnimatedSprite;
import mb.fc.game.sprite.CombatSprite;

public class MovingSprite
{
	private AnimatedSprite animatedSprite;
	private int moveIndex;
	private Direction direction;
	private float endX, endY;
	private StateInfo stateInfo;
	private boolean isFirstMove = true;
	private int moveRemainder = 0;
	private Queue<Direction> nextMoveQueue = null;
	// public static int MOVE_SPEED = 11;
	public static int MOVE_SPEED = 220;
	public static int STAND_ANIMATION_SPEED = 10;
	public static int WALK_ANIMATION_SPEED = 4;

	public MovingSprite(AnimatedSprite combatSprite, Direction dir, StateInfo stateInfo) {
		super();
		this.animatedSprite = combatSprite;
		this.stateInfo = stateInfo;
		combatSprite.setAnimationUpdate(WALK_ANIMATION_SPEED);
		initializeDirection(dir);
	}

	private void initializeDirection(Direction dir) {
		this.direction = dir;
		this.moveIndex = 0;
		switch (direction)
		{
			case UP:
				endX = animatedSprite.getLocX();
				endY = animatedSprite.getLocY() - stateInfo.getTileHeight();
				break;
			case DOWN:
				endX = animatedSprite.getLocX();
				endY = animatedSprite.getLocY() + stateInfo.getTileHeight();
				break;
			case LEFT:
				endX = animatedSprite.getLocX() - stateInfo.getTileWidth();
				endY = animatedSprite.getLocY();
				break;
			case RIGHT:
				MOVE_SPEED = 220;
				endX = animatedSprite.getLocX() + stateInfo.getTileWidth();
				endY = animatedSprite.getLocY();
				break;
		}
	}

	public boolean update(int delta, boolean fastMove)
	{
		
		int moveSpeed = MOVE_SPEED;
		if (stateInfo.isCombat() && (!((CombatSprite) animatedSprite).isHero() || fastMove))
			moveSpeed /= 2;

		if (stateInfo.isCombat() && isFirstMove)
		{
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "step", 1f, false));
		}
		
		isFirstMove = false;

		moveIndex += delta;

		if (moveIndex >= moveSpeed)
		{
			moveRemainder = moveIndex - moveSpeed;
			moveIndex = moveSpeed;
			animatedSprite.setLocation(endX, endY, stateInfo.getTileWidth(), stateInfo.getTileHeight());

			if (animatedSprite == stateInfo.getCurrentSprite())
				stateInfo.getCamera().centerOnSprite(animatedSprite, stateInfo.getCurrentMap());

			// Check to see if we have queued moves, if so start the next move
			if (nextMoveQueue != null && nextMoveQueue.size() > 0)
			{
				initializeDirection(nextMoveQueue.remove());
				this.updateWithRemainder(moveRemainder);
				return false;
			}

			animatedSprite.setAnimationUpdate(STAND_ANIMATION_SPEED);
			return true;
		}

		float amountMoved = ((moveSpeed - moveIndex) / (moveSpeed * 1.0f) * stateInfo.getTileHeight());
		switch (direction)
		{
			case UP:
				animatedSprite.setLocY(endY + amountMoved, stateInfo.getTileHeight());
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
			case DOWN:
				animatedSprite.setLocY(endY - amountMoved, stateInfo.getTileHeight());
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
			case LEFT:
				animatedSprite.setLocX(endX + amountMoved, stateInfo.getTileWidth());
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
			case RIGHT:
				animatedSprite.setLocX(endX - amountMoved, stateInfo.getTileWidth());
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
		}

		if (animatedSprite == stateInfo.getCurrentSprite()) {
			stateInfo.getCamera().centerOnSprite(animatedSprite, stateInfo.getCurrentMap());
		}

		return false;
	}

	public boolean isFirstMove() {
		return isFirstMove;
	}

	public boolean update(int delta)
	{
		return update(delta, false);
	}

	public boolean updateWithRemainder(int delta)
	{
		boolean retVal = update(delta, false);
		this.isFirstMove = true;
		this.moveRemainder = 0;
		return retVal;
	}

	public float getEndX() {
		return endX;
	}

	public float getEndY() {
		return endY;
	}

	public AnimatedSprite getAnimatedSprite() {
		return animatedSprite;
	}

	public Direction getDirection() {
		return direction;
	}

	public int getMoveRemainder() {
		return moveRemainder;
	}

	public void addNextMovement(Direction dir) {
		if (nextMoveQueue == null)
			nextMoveQueue = new LinkedList<>();
		nextMoveQueue.add(dir);
	}
}
