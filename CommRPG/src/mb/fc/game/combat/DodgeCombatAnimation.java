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
	private boolean animationSwapped = false;
	
	public DodgeCombatAnimation(CombatSprite parentSprite, FCResourceManager fcrm, Integer minimumTimePassed)
	{
		super(new HeroAnimationWrapper(parentSprite, getAnimationName(parentSprite)), parentSprite, false);
		if (minimumTimePassed == null)
			this.minimumTimePassed = this.animationWrapper.getAnimationLength();
		else
			this.minimumTimePassed = minimumTimePassed;
		
		if (parentSprite.getSpellsDescriptors().size() > 0) {
			String animName = "Shield";
			if (!parentSprite.isHero())
				animName = "EnemyShield";
			spellBlockAnimation = new AnimationWrapper(fcrm.getSpriteAnimation(animName), getSecondaryAnimName());
			int blockAnimationLength = this.animationWrapper.getAnimationLength();
			int spellAnimationLength = spellBlockAnimation.getAnimationLength();
			if (minimumTimePassed == null)
				this.minimumTimePassed = Math.max(blockAnimationLength,spellAnimationLength);
			
			timeToStartSecondAnimation = Math.abs(blockAnimationLength - spellAnimationLength);
			// We're going to set the longer animation to the one that is driven by the super class
			// and we'll manually update the shorter one
			if (spellAnimationLength > blockAnimationLength) {
				System.out.println("SWAP ANIMATIONS");
				AnimationWrapper temp = spellBlockAnimation;
				spellBlockAnimation = animationWrapper;
				animationWrapper = temp;
				animationSwapped = true;
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
		if (animationSwapped)
			super.render(fcCont, g, yDrawPos, scale);
		if (shouldUpdateSecondAnimation(0)) { 
			int x = xOffset; // + (parentSprite.isHero() ? xOffset : -xOffset);
			int y = yDrawPos + yOffset;
			spellBlockAnimation.drawAnimation(x, y, null, scale, g);
		}
		if (!animationSwapped)
			super.render(fcCont, g, yDrawPos, scale);
	}
	
	private boolean shouldUpdateSecondAnimation(int delta) {
		return spellBlockAnimation != null && timeToStartSecondAnimation <= (this.totalTimePassed + delta);
	}

	private static String getAnimationName(CombatSprite cs) {
		if (cs.hasAnimation("Dodge"))
			return "Dodge";
		else if (cs.getSpellsDescriptors().size() > 0)
			return "Spell";
		else
			throw new BadResourceException("CombatSprite " + cs.getName() + " has no 'Dodge' animation defined");
	}
	
	protected String getSecondaryAnimName() {
		return "Dodge";
	}
}
