package mb.fc.game.combat;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.utils.HeroAnimationWrapper;

public class StandCombatAnimation extends CombatAnimation
{
	public StandCombatAnimation(CombatSprite parentSprite)
	{
		this(parentSprite, 500);
	}

	public StandCombatAnimation(CombatSprite parentSprite, int mimimumTimePassed)
	{
		super(new HeroAnimationWrapper(parentSprite, "Stand", true), parentSprite, false);
		this.minimumTimePassed = mimimumTimePassed;
	}
	
	public void continueStandAnimation(CombatAnimation previousAnimation) {
		this.animationWrapper.copyAnimationLocation(previousAnimation.animationWrapper);
	}
}
