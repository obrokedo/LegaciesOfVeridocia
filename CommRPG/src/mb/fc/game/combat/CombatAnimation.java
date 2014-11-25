package mb.fc.game.combat;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.AttackCinematicState2;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.utils.AnimationWrapper;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public abstract class CombatAnimation
{
	protected static float SCREEN_SCALE = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];

	protected AnimationWrapper animationWrapper;
	protected int totalTimePassed = 0;
	protected int minimumTimePassed = -1;
	protected boolean drawSpell = false;
	protected int xOffset;
	protected int yOffset;
	protected Color renderColor = null;
	protected boolean blocks = true;
	protected CombatSprite parentSprite;

	public CombatAnimation(AnimationWrapper animationWrapper, CombatSprite combatSprite) {
		this(animationWrapper, combatSprite, -1);
	}

	public CombatAnimation() { }

	public CombatAnimation(AnimationWrapper animationWrapper, CombatSprite combatSprite, int minimumTimePassed) {
		super();
		this.animationWrapper = animationWrapper;
		this.minimumTimePassed = minimumTimePassed;
		this.parentSprite = combatSprite;
		if (animationWrapper != null)
			minimumTimePassed = animationWrapper.getAnimationLength();
	}

	public boolean update(int delta)
	{
		totalTimePassed += delta;
		animationWrapper.udpate(delta);
		if (minimumTimePassed > -1 && totalTimePassed >= minimumTimePassed)
			return true;

		return !blocks;
	}

	public void render(FCGameContainer fcCont, Graphics g, int yDrawPos)
	{
		int x = fcCont.getDisplayPaddingX() + (parentSprite.isHero() ? xOffset : -xOffset);
		int y = yDrawPos + yOffset;

		if (parentSprite.isHero() && parentSprite.getMovementType() != CombatSprite.MOVEMENT_FLYING)
			g.drawImage(AttackCinematicState2.FLOOR_IMAGE, x + 220 * SCREEN_SCALE, y - 15 * SCREEN_SCALE);

		animationWrapper.drawAnimation(x, y, renderColor, g);
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
}
