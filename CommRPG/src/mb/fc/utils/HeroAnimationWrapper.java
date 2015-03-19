package mb.fc.utils;

import mb.fc.game.sprite.CombatSprite;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class HeroAnimationWrapper extends AnimationWrapper
{
	protected Image weapon = null;
	protected CombatSprite combatSprite;

	public HeroAnimationWrapper(CombatSprite combatSprite, String animationName)
	{
		this(combatSprite, animationName, false);
	}

	public HeroAnimationWrapper(CombatSprite combatSprite, String animationName, boolean loops)
	{
		super(combatSprite.getSpriteAnims());
		if (combatSprite.isHero())
			this.weapon = combatSprite.getCurrentWeaponImage();
		this.combatSprite = combatSprite;
		this.setAnimation(animationName, loops);
	}

	@Override
	protected void drawWeapon(AnimSprite as, int x, int y, Color filter,
			Graphics g) {
		if (weapon != null)
		{
			if (filter == null)
				g.drawImage(getRotatedImageIfNeeded(weapon, as),
						x + as.x * SCREEN_SCALE, y + as.y * SCREEN_SCALE);
			else
				g.drawImage(getRotatedImageIfNeeded(weapon, as),
						x + as.x * SCREEN_SCALE, y + as.y * SCREEN_SCALE, filter);
		}
	}

	@Override
	public void setAnimation(String animationName, boolean loops) {
		super.setAnimation((combatSprite.isPromoted() ? "Pro" : "Un") + animationName, loops);
	}

	@Override
	public boolean hasAnimation(String animationName) {
		return combatSprite.getAnimation(animationName) != null;
	}
}
