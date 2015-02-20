package mb.fc.game.combat;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.LOVAttackCinematicState;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.utils.AnimationWrapper;
import mb.jython.JBattleEffect;

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
			displayPlatform = (parentSprite.isHero() && parentSprite.getMovementType() != CombatSprite.MOVEMENT_FLYING);
	}

	public boolean update(int delta)
	{
		totalTimePassed += delta;
		animationWrapper.update(delta);
		for (JBattleEffect be : parentSprite.getBattleEffects())
		{
			be.getEffectAnimation().update(delta);
		}
		if (minimumTimePassed > -1 && totalTimePassed >= minimumTimePassed)
			return true;

		return !blocks;
	}

	public void render(FCGameContainer fcCont, Graphics g, int yDrawPos)
	{
		int x = fcCont.getDisplayPaddingX() + xOffset; // + (parentSprite.isHero() ? xOffset : -xOffset);
		int y = yDrawPos + yOffset;

		if (displayPlatform)
			g.drawImage(LOVAttackCinematicState.FLOOR_IMAGE, x + 220 * SCREEN_SCALE, y - 15 * SCREEN_SCALE);

		animationWrapper.drawAnimation(x, y, renderColor, g);

		for (JBattleEffect be : parentSprite.getBattleEffects())
		{
			be.getEffectAnimation().drawAnimation(xOffset + (int) (fcCont.getDisplayPaddingX() + (parentSprite.isHero() ? 276 * SCREEN_SCALE : 50 * SCREEN_SCALE)),
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
}
