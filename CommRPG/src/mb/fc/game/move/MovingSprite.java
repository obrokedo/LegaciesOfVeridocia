package mb.fc.game.move;

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
	private int endX, endY;
	private StateInfo stateInfo;
	private boolean isFirstMove = true;
	public static int MOVE_SPEED = 11;
	public static int STAND_ANIMATION_SPEED = 10;
	public static int WALK_ANIMATION_SPEED = 4;

	public MovingSprite(AnimatedSprite combatSprite, Direction dir, StateInfo stateInfo) {
		super();
		this.animatedSprite = combatSprite;
		this.direction = dir;
		this.stateInfo = stateInfo;

		combatSprite.setAnimationUpdate(WALK_ANIMATION_SPEED);

		switch (direction)
		{
			case UP:
				endX = combatSprite.getLocX();
				endY = combatSprite.getLocY() - stateInfo.getTileHeight();
				break;
			case DOWN:
				endX = combatSprite.getLocX();
				endY = combatSprite.getLocY() + stateInfo.getTileHeight();
				break;
			case LEFT:
				endX = combatSprite.getLocX() - stateInfo.getTileWidth();
				endY = combatSprite.getLocY();
				break;
			case RIGHT:
				endX = combatSprite.getLocX() + stateInfo.getTileWidth();
				endY = combatSprite.getLocY();
				break;
		}
	}

	public boolean update(boolean fastMove)
	{
		isFirstMove = false;
		int moveSpeed = MOVE_SPEED;
		if (stateInfo.isCombat() && (!((CombatSprite) animatedSprite).isHero() || fastMove))
			moveSpeed /= 2;
		switch (direction)
		{
			case UP:
				animatedSprite.setLocY(animatedSprite.getAbsLocY() -
						1.0f * stateInfo.getTileHeight() / moveSpeed);
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
			case DOWN:
				animatedSprite.setLocY(animatedSprite.getAbsLocY() +
						1.0f * stateInfo.getTileHeight() / moveSpeed);
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
			case LEFT:
				animatedSprite.setLocX(animatedSprite.getAbsLocX() -
						1.0f * stateInfo.getTileWidth() / moveSpeed);
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
			case RIGHT:
				animatedSprite.setLocX(animatedSprite.getAbsLocX() +
						1.0f * stateInfo.getTileWidth() / moveSpeed);
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
		}
		if (stateInfo.isCombat() && moveIndex == 0)
			stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "step", 1f, false));

		if (animatedSprite == stateInfo.getCurrentSprite())
			stateInfo.getCamera().centerOnSprite(animatedSprite, stateInfo.getCurrentMap());

		moveIndex++;

		if (moveIndex == moveSpeed)
		{
			animatedSprite.setLocation(endX, endY);
			if (animatedSprite == stateInfo.getCurrentSprite())
				stateInfo.getCamera().centerOnSprite(animatedSprite, stateInfo.getCurrentMap());
			animatedSprite.setAnimationUpdate(STAND_ANIMATION_SPEED);
			return true;
		}
		return false;
	}

	public boolean isFirstMove() {
		return isFirstMove;
	}

	public boolean update()
	{
		return update(false);
	}

	public int getEndX() {
		return endX;
	}

	public int getEndY() {
		return endY;
	}

	public AnimatedSprite getAnimatedSprite() {
		return animatedSprite;
	}

	public Direction getDirection() {
		return direction;
	}

	public int getMoveIndex() {
		return moveIndex;
	}

	public void setMoveIndex(int moveIndex) {
		this.moveIndex = moveIndex;
	}
}
