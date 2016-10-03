package mb.fc.game.combat;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;

public class WaitCombatAnimation extends CombatAnimation
{
	private CombatAnimation childAnimation;

	public WaitCombatAnimation(CombatAnimation combatAnimation, int minimumTimePassed)
	{
		this.childAnimation = combatAnimation;
		this.minimumTimePassed = minimumTimePassed;
	}

	@Override
	public boolean update(int delta)
	{
		this.totalTimePassed += delta;
		childAnimation.update(delta);
		return totalTimePassed >= minimumTimePassed;
	}

	@Override
	public void render(FCGameContainer fcCont, Graphics g, int yDrawPos, float scale) {
		childAnimation.render(fcCont, g, yDrawPos, scale);
	}

	@Override
	public CombatSprite getParentSprite() {
		return childAnimation.getParentSprite();
	}

	@Override
	public boolean isDrawSpell() {
		return childAnimation.isDrawSpell();
	}

	@Override
	public void initialize() {
		childAnimation.renderColor = null;
		childAnimation.xOffset = 0;
		childAnimation.yOffset = 0;
	}


}
