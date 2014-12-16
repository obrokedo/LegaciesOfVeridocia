package mb.fc.game.combat;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.utils.AnimationWrapper;

public class StandCombatAnimation extends CombatAnimation
{
	public StandCombatAnimation(CombatSprite parentSprite)
	{
		this(parentSprite, 500);
	}

	public StandCombatAnimation(CombatSprite parentSprite, int mimimumTimePassed)
	{
		super(new AnimationWrapper(parentSprite.getSpriteAnims(), "UnStand", true), parentSprite, false);
		this.minimumTimePassed = mimimumTimePassed;
	}
}
