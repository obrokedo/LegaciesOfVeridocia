package mb.fc.game.combat;

import org.newdawn.slick.Graphics;

import mb.fc.game.exception.BadResourceException;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.utils.AnimationWrapper;
import mb.fc.utils.HeroAnimationWrapper;

public class DodgeCombatAnimation extends CombatAnimation
{
	private AnimationWrapper spellBlockAnimation = null;
	private int timeToStartSecondAnimation = 0;
	
	public DodgeCombatAnimation(CombatSprite parentSprite, FCResourceManager fcrm)
	{
		super(new HeroAnimationWrapper(parentSprite, getDodgeAnimationName(parentSprite)), parentSprite, false);
		this.minimumTimePassed = this.animationWrapper.getAnimationLength();
		
		if (parentSprite.getSpellsDescriptors().size() > 0) {
			String animName = "Shield";
			if (!parentSprite.isHero())
				animName = "EnemyShield";
			spellBlockAnimation = new AnimationWrapper(fcrm.getSpriteAnimation(animName), "Dodge");
			int blockAnimationLength = minimumTimePassed;
			int spellAnimationLength = spellBlockAnimation.getAnimationLength();
			this.minimumTimePassed = Math.max(blockAnimationLength,spellAnimationLength);
			
			timeToStartSecondAnimation = Math.abs(blockAnimationLength - spellAnimationLength);
			// We're going to set the longer animation to the one that is driven by the super class
			// and we'll manually update the shorter one
			if (spellAnimationLength > blockAnimationLength) {
				AnimationWrapper temp = spellBlockAnimation;
				spellBlockAnimation = animationWrapper;
				animationWrapper = temp;
			}
		}
	}
	
	@Override
	public boolean update(int delta) {
		if (shouldUpdateSecondAnimation(delta)) {
			spellBlockAnimation.update(delta);
		}
		return super.update(delta);
	}



	@Override
	public void render(PaddedGameContainer fcCont, Graphics g, int yDrawPos, float scale) {
		super.render(fcCont, g, yDrawPos, scale);
		if (shouldUpdateSecondAnimation(0)) { 
			int x = xOffset; // + (parentSprite.isHero() ? xOffset : -xOffset);
			int y = yDrawPos + yOffset;
			spellBlockAnimation.drawAnimation(x, y, null, scale, g);
		}
	}
	
	private boolean shouldUpdateSecondAnimation(int delta) {
		return spellBlockAnimation != null && timeToStartSecondAnimation <= (this.minimumTimePassed + delta);
	}

	private static String getDodgeAnimationName(CombatSprite cs) {
		if (cs.hasAnimation("Dodge"))
			return "Dodge";
		else if (cs.getSpellsDescriptors().size() > 0)
			return "Spell";
		else
			throw new BadResourceException("CombatSprite " + cs.getName() + " has no 'Dodge' animation defined");
	}
}
