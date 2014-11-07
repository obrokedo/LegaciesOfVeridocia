package mb.fc.game.constants;

public enum Direction
{
	UP,
	DOWN,
	LEFT,
	RIGHT;

	public static Direction getDirectionFromInt(int dir) {
		if (dir == 0)
			return Direction.UP;
		else if (dir == 1)
			return Direction.DOWN;
		else if (dir == 2)
			return Direction.LEFT;
		else if (dir == 3)
			return Direction.RIGHT;
		return null;
	}
}
