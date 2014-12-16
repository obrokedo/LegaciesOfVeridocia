package mb.fc.game.combat;

import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;


public class TransCombatAnimation extends CombatAnimation
{
	private CombatAnimation childAnimation;
	private boolean out;
	private boolean isHero;
	private int maxOffset;
	public static final int TRANSITION_TIME = 300;

	public TransCombatAnimation(CombatAnimation childAnimation, boolean transitionOut)
	{
		super();
		this.out = transitionOut;
		this.minimumTimePassed = TRANSITION_TIME;
		this.childAnimation = childAnimation;
		this.isHero = childAnimation.getParentSprite().isHero();
		if (isHero)
			maxOffset = (int) (250 * SCREEN_SCALE);
		else
			maxOffset = (int) (-150 * SCREEN_SCALE);

		if (!out)
			childAnimation.xOffset = maxOffset;
	}

	@Override
	public boolean update(int delta) {
		this.totalTimePassed += delta;
		if (out)
			childAnimation.xOffset = (maxOffset * totalTimePassed) / this.minimumTimePassed;
		else
		{
			int offset = maxOffset - ((maxOffset * totalTimePassed) / this.minimumTimePassed);
			if (maxOffset > 0)
				childAnimation.xOffset = Math.max(offset, 0);
			else
				childAnimation.xOffset = Math.min(offset, 0);
		}
		childAnimation.update(delta);
		return totalTimePassed >= minimumTimePassed;
	}

	@Override
	public void render(FCGameContainer fcCont, Graphics g, int yDrawPos) {
		g.setColor(Color.white);
		childAnimation.render(fcCont, g, yDrawPos);
	}

	@Override
	public boolean isDrawSpell() {
		return drawSpell;
	}

	@Override
	public CombatSprite getParentSprite() {
		return childAnimation.getParentSprite();
	}
}
