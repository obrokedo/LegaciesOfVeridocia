package mb.fc.game.combat;

import mb.fc.engine.state.AttackCinematicState2;
import mb.fc.game.battle.BattleResults;
import mb.fc.game.battle.command.BattleCommand;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.utils.AnimationWrapper;

public class AttackCombatAnimation extends CombatAnimation
{
	private boolean castingSpell = false;

	public AttackCombatAnimation(AnimationWrapper animationWrapper)
	{
		super(animationWrapper, null);
	}

	public AttackCombatAnimation(CombatSprite parentSprite, BattleResults battleResults, boolean blockingAnimation)
	{
		this(parentSprite, battleResults, blockingAnimation, false);
	}

	public AttackCombatAnimation(CombatSprite parentSprite, BattleResults battleResults, boolean blockingAnimation,
			boolean rangedAttack)
	{
		super(new AnimationWrapper(parentSprite.getSpriteAnims(), (rangedAttack ? "UnRanged" : "UnAttack")), parentSprite);
		if (battleResults.critted && animationWrapper.hasAnimation("UnCrit"))
			this.animationWrapper.setAnimation("UnCrit", false);
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_SPELL)
		{
			this.animationWrapper.setAnimation("UnSpell", false);
			castingSpell = true;
		}
		else if (battleResults.battleCommand.getCommand() == BattleCommand.COMMAND_ITEM
				 && animationWrapper.hasAnimation("UnItem"))
			this.animationWrapper.setAnimation("UnItem", false);

		this.blocks = blockingAnimation;

		minimumTimePassed = animationWrapper.getAnimationLength();
	}

	@Override
	public boolean update(int delta) {
		if (castingSpell)
		{
			int maxTime = getAnimationLength();
			if (this.totalTimePassed > maxTime - AttackCinematicState2.SPELL_FLASH_DURATION)
				this.setDrawSpell(true);
		}
		return super.update(delta);
	}
}
