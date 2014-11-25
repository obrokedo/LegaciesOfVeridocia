package mb.fc.game.combat;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class DeathCombatAnimation extends CombatAnimation
{
	private Color deathColor = new Color(255, 255, 255, 255);
	private CombatAnimation childAnimation;

	public DeathCombatAnimation(CombatAnimation childAnimation)
	{
		super();
		this.minimumTimePassed = 500;
		this.childAnimation = childAnimation;
	}

	@Override
	public boolean update(int delta)
	{
		this.totalTimePassed += delta;
		childAnimation.renderColor.a = Math.max(1 - (totalTimePassed / (minimumTimePassed * 1.0f)), 0);
		childAnimation.update(delta);
		return totalTimePassed >= minimumTimePassed;
	}

	@Override
	public void render(FCGameContainer fcCont, Graphics g, int yDrawPos) {
		childAnimation.render(fcCont, g, yDrawPos);
	}

	@Override
	public void initialize() {
		childAnimation.renderColor = deathColor;
		childAnimation.xOffset = 0;
		childAnimation.yOffset = 0;
	}

	@Override
	public CombatSprite getParentSprite() {
		return childAnimation.getParentSprite();
	}

	@Override
	public boolean isDrawSpell() {
		return childAnimation.isDrawSpell();
	}
}