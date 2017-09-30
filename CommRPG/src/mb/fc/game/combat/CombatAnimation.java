package mb.fc.game.combat;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import mb.fc.engine.state.LOVAttackCinematicState;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.utils.AnimationWrapper;
import mb.jython.JBattleEffect;

public class CombatAnimation
{
	protected AnimationWrapper animationWrapper;
	protected int totalTimePassed = 0;
	protected int minimumTimePassed = -1;
	protected boolean drawSpell = false;
	protected int xOffset;
	protected int yOffset;
	protected Color renderColor = null;
	protected boolean blocks = true;
	protected CombatSprite parentSprite;
	protected boolean displayPlatform = false;

	public CombatAnimation() {}

	public CombatAnimation(AnimationWrapper animationWrapper, CombatSprite combatSprite, boolean isMissile) {
		this(animationWrapper, combatSprite, -1, (isMissile ? true : null));
	}

	public CombatAnimation(AnimationWrapper animationWrapper, CombatSprite combatSprite, int minimumTimePassed)
	{
		this(animationWrapper, combatSprite, minimumTimePassed, null);
	}

	private CombatAnimation(AnimationWrapper animationWrapper, CombatSprite combatSprite, int minimumTimePassed, Boolean showPlatform) {
		super();
		this.animationWrapper = animationWrapper;
		this.minimumTimePassed = minimumTimePassed;
		this.parentSprite = combatSprite;
		if (animationWrapper != null)
			minimumTimePassed = animationWrapper.getAnimationLength();

		if (showPlatform == null)
			displayPlatform = (parentSprite.isHero() && parentSprite.isDrawShadow());
	}

	public boolean update(int delta)
	{
		totalTimePassed += delta;
		animationWrapper.update(delta);
		for (JBattleEffect be : parentSprite.getBattleEffects())
		{
			if (be.getEffectAnimation() != null)
				be.getEffectAnimation().update(delta);
		}
		if (minimumTimePassed > -1 && totalTimePassed >= minimumTimePassed)
			return true;

		return !blocks;
	}

	public void render(PaddedGameContainer fcCont, Graphics g, int yDrawPos, float scale)
	{
		int x = xOffset; // + (parentSprite.isHero() ? xOffset : -xOffset);
		int y = yDrawPos + yOffset;

		if (displayPlatform)
			g.drawImage(LOVAttackCinematicState.FLOOR_IMAGE, x + 135, y - 15);

		if (renderColor != null || parentSprite.getCurrentHP() > 0)
			animationWrapper.drawAnimation(x, y, renderColor, scale, g);

		// TODO Scale battle animations
		for (JBattleEffect be : parentSprite.getBattleEffects())
		{
			if (be.getEffectAnimation() != null)
				be.getEffectAnimation().drawAnimation(xOffset + (int) ((parentSprite.isHero() ? 276 : 50)),
					yDrawPos, g);
		}
	}

	public int getAnimationLength()
	{
		return animationWrapper.getAnimationLength();
	}

	public int getAnimationLengthMinusLast()
	{
		return animationWrapper.getCurrentAnimation().getAnimationLengthMinusLast();
	}

	public boolean isDrawSpell() {
		return drawSpell;
	}

	public void setDrawSpell(boolean drawSpell) {
		this.drawSpell = drawSpell;
	}

	public void initialize()
	{
		this.renderColor = null;
	}

	public CombatSprite getParentSprite() {
		return parentSprite;
	}

	public boolean isDamaging()
	{
		return false;
	}

	@Override
	public String toString() {
		return "CombatAnimation [animationWrapper=" + animationWrapper + ", totalTimePassed=" + totalTimePassed
				+ ", minimumTimePassed=" + minimumTimePassed + ", drawSpell=" + drawSpell + ", xOffset=" + xOffset
				+ ", yOffset=" + yOffset + ", renderColor=" + renderColor + ", blocks=" + blocks + ", parentSprite="
				+ parentSprite + ", displayPlatform=" + displayPlatform + "]";
	}
}
