package mb.fc.game.move;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.sprite.CombatSprite;

public class MovingSprite
{
	private CombatSprite combatSprite;
	private int moveIndex;
	private Direction direction;
	private int endX, endY;
	private StateInfo stateInfo;
	public static int MOVE_SPEED = 10; 
	public static int STAND_ANIMATION_SPEED = 10;
	public static int WALK_ANIMATION_SPEED = 4;
	
	public MovingSprite(CombatSprite combatSprite, Direction dir, StateInfo stateInfo) {
		super();
		this.combatSprite = combatSprite;
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
	
	public boolean update()
	{
		int moveSpeed = MOVE_SPEED;
		if (!combatSprite.isHero())
			moveSpeed /= 2;
		switch (direction)
		{
			case UP:
				combatSprite.setLocY(combatSprite.getAbsLocY() -
						1.0f * stateInfo.getTileHeight() / moveSpeed);
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
			case DOWN:
				combatSprite.setLocY(combatSprite.getAbsLocY() +
						1.0f * stateInfo.getTileHeight() / moveSpeed);
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
			case LEFT:
				combatSprite.setLocX(combatSprite.getAbsLocX() - 
						1.0f * stateInfo.getTileWidth() / moveSpeed);
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
			case RIGHT:
				combatSprite.setLocX(combatSprite.getAbsLocX() +
						1.0f * stateInfo.getTileWidth() / moveSpeed);
						// 2 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]));
				break;
		}
		
		if (combatSprite == stateInfo.getCurrentSprite())
			stateInfo.getCamera().centerOnSprite(combatSprite, stateInfo.getCurrentMap());
		
		moveIndex++;
		
		if (moveIndex == moveSpeed)
		{				
			combatSprite.setLocation(endX, endY);
			combatSprite.setAnimationUpdate(STAND_ANIMATION_SPEED);
			return true;
		}	
		return false;
	}

	public int getEndX() {
		return endX;
	}
	
	public int getEndY() {
		return endY;
	}
	
	public CombatSprite getCombatSprite() {
		return combatSprite;
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
