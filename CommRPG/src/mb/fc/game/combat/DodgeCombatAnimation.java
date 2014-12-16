package mb.fc.game.combat;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.utils.AnimationWrapper;

public class DodgeCombatAnimation extends CombatAnimation
{
	public DodgeCombatAnimation(CombatSprite parentSprite)
	{
		super(new AnimationWrapper(parentSprite.getSpriteAnims(), "UnDodge"), parentSprite, false);
		this.minimumTimePassed = this.animationWrapper.getAnimationLength();
	}
}
