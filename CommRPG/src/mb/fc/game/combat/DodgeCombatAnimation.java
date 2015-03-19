package mb.fc.game.combat;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.utils.HeroAnimationWrapper;

public class DodgeCombatAnimation extends CombatAnimation
{
	public DodgeCombatAnimation(CombatSprite parentSprite)
	{
		super(new HeroAnimationWrapper(parentSprite, "Dodge"), parentSprite, false);
		this.minimumTimePassed = this.animationWrapper.getAnimationLength();
	}
}
