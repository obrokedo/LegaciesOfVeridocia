package mb.fc.game;

import mb.fc.game.exception.BadResourceException;
import mb.fc.game.move.AttackableSpace;

public enum Range {
	SELF_ONLY,
	ONE_ONLY,
	TWO_AND_LESS,
	THREE_AND_LESS,
	TWO_NO_ONE,
	THREE_NO_ONE,
	THREE_NO_ONE_OR_TWO;

	public boolean isInDistance(int range)
	{
		if (range == 1)
		{
			switch (this)
			{
				case ONE_ONLY:
				case TWO_AND_LESS:
				case THREE_AND_LESS:
					return true;
				default:
					return false;
			}
		}
		else if (range == 2)
		{
			switch (this)
			{
				case TWO_AND_LESS:
				case THREE_AND_LESS:
				case TWO_NO_ONE:
				case THREE_NO_ONE:
					return true;
				default:
					return false;
			}
		}
		else if (range == 3)
		{
			switch (this)
			{
				case THREE_AND_LESS:
				case THREE_NO_ONE:
				case THREE_NO_ONE_OR_TWO:
					return true;
				default:
					return false;
			}
		}

		return true;
	}

	public int[][] getAttackableSpace()
	{
		switch (this)
		{
			case ONE_ONLY:
				return AttackableSpace.RANGE_1;
			case TWO_AND_LESS:
				return AttackableSpace.RANGE_2;
			case THREE_AND_LESS:
				return AttackableSpace.RANGE_3;
			case TWO_NO_ONE:
				return AttackableSpace.RANGE_2_NO_1;
			case THREE_NO_ONE:
				return AttackableSpace.RANGE_3_NO_1;
			case THREE_NO_ONE_OR_TWO:
				return AttackableSpace.RANGE_3_NO_1_2;
		}

		return null;
	}

	public int getMaxRange()
	{
		switch (this)
		{
			case THREE_AND_LESS:
			case THREE_NO_ONE:
			case THREE_NO_ONE_OR_TWO:
				return 3;
			case TWO_NO_ONE:
			case TWO_AND_LESS:
				return 2;
			case ONE_ONLY:
				return 1;
			default:
				return 0;
		}
	}

	public static Range convertToRange(int rangeInt)
	{
		switch (rangeInt)
		{
			case 0:
				return SELF_ONLY;
			case 1:
				return ONE_ONLY;
			case 2:
				return TWO_AND_LESS;
			case 3:
				return THREE_AND_LESS;
			case 4:
				return TWO_NO_ONE;
			case 5:
				return THREE_NO_ONE;
			case 6:
				return THREE_NO_ONE_OR_TWO;
			default:
				throw new BadResourceException("Attempted to create a resource with an illegal range");
		}
	}
}
