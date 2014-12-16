package mb.fc.game.combat;

import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class TransBGCombatAnimation extends CombatAnimation
{
	private Image backgroundImage;
	private int bgXLoc, bgYLoc;
	private int endLocX;
	private int offsetLocX;
	private CombatAnimation childAnimation;
	private boolean transIn;

	public TransBGCombatAnimation(Image backgroundImage, int bgXLoc,
			int bgYLoc, int screenWidth, CombatAnimation childAnimation,
			boolean transIn, boolean isHero) {
		super();
		this.backgroundImage = backgroundImage;
		this.bgXLoc = bgXLoc;
		this.bgYLoc = bgYLoc;
		this.childAnimation = childAnimation;
		this.minimumTimePassed = 500;
		this.transIn = transIn;

		if (isHero)
			endLocX = screenWidth;
		else
			endLocX = -screenWidth;

		update(0);
	}

	@Override
	public void initialize() {
		update(0);
	}

	@Override
	public boolean update(int delta) {
		this.totalTimePassed += delta;

		if (transIn)
			offsetLocX = (int) (1.0f * endLocX * (minimumTimePassed - totalTimePassed) / minimumTimePassed);
		else
			offsetLocX = (int) (1.0f * endLocX * totalTimePassed / minimumTimePassed);

		if (childAnimation != null)
			childAnimation.xOffset = offsetLocX;

		return totalTimePassed >= minimumTimePassed;
	}

	@Override
	public void render(FCGameContainer fcCont, Graphics g, int yDrawPos) {
		g.setColor(Color.black);
		g.fillRect(0, 0, fcCont.getWidth(), fcCont.getHeight());

		g.drawImage(backgroundImage, bgXLoc + offsetLocX, bgYLoc);

		if (childAnimation != null)
		{
			if (childAnimation.getParentSprite().getCurrentHP() > 0)
				childAnimation.render(fcCont, g, yDrawPos);
		}

		g.setColor(Color.black);
		g.fillRect(0, 0, fcCont.getDisplayPaddingX(), fcCont.getHeight());
		g.fillRect(fcCont.getDisplayPaddingX() + backgroundImage.getWidth(), 0, fcCont.getDisplayPaddingX(), fcCont.getHeight());
	}


}
