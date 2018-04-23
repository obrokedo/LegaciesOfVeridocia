package mb.fc.game.combat;

import mb.fc.game.battle.BattleResults;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.utils.AnimationWrapper;
import mb.fc.utils.HeroAnimationWrapper;

public class AttackCombatAnimation extends CombatAnimation
{
	/**
	 * Constructor to create a ranged attack
	 *
	 * @param animationWrapper
	 * @param parentSprite
	 */
	public AttackCombatAnimation(AnimationWrapper animationWrapper, CombatSprite parentSprite)
	{
		super(animationWrapper, parentSprite, true);
		minimumTimePassed = animationWrapper.getAnimationLength();
	}

	public AttackCombatAnimation(CombatSprite parentSprite, BattleResults battleResults, boolean blockingAnimation,
			boolean critted)
	{
		this(parentSprite, battleResults, blockingAnimation, false, critted);
	}

	public AttackCombatAnimation(CombatSprite parentSprite, BattleResults battleResults, boolean blockingAnimation,
			boolean rangedAttack, boolean critted)
	{
		super(new HeroAnimationWrapper(parentSprite,
				(rangedAttack ? "Ranged" : "Attack")),
				parentSprite, false);

		if (critted && animationWrapper.hasAnimation("Crit"))
			this.animationWrapper.setAnimation("Crit", false);
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
		{
			if (animationWrapper.hasAnimation("Spell"))
				this.animationWrapper.setAnimation("Spell", false);
			else { 
				animationWrapper.setAnimation("Attack", false);
			}
		}
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ITEM)
		{
			if ( animationWrapper.hasAnimation("Item"))
				this.animationWrapper.setAnimation("Item", false);
			else 
				animationWrapper.setAnimation("Attack", false);
		}

		this.blocks = blockingAnimation;

		minimumTimePassed = animationWrapper.getAnimationLength();
	}

	@Override
	public boolean update(int delta) {
		/*
		if (castingSpell)
		{
			int maxTime = getAnimationLength() - 600;
			if (this.totalTimePassed > maxTime)
				this.setDrawSpell(true);
		}
		*/
		return super.update(delta);
	}
}
