package mb.fc.game.menu;

import org.newdawn.slick.Graphics;

import mb.fc.game.hudmenu.Panel;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.utils.StringUtils;

public class LandEffectPanel extends Panel
{
	private int landEffect = 0;

	public LandEffectPanel() {
		super(PanelType.PANEL_LAND_EFFECT);
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		Panel.drawPanelBox(8, 5, 80, 35, graphics);
		// Panel.drawPanelBox( 15, 5, 57, 43, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		StringUtils.drawString("Land", 20, 2, graphics);
		StringUtils.drawString("Effect", 14, 14, graphics);
		if (landEffect == 0)
			StringUtils.drawString(" " + landEffect + "%", 60, 8, graphics);
		else
			StringUtils.drawString(" " + landEffect + "%", 60, 8, graphics);
		// StringUtils.drawString(landEffect + "%", 37, 26, graphics);
	}

	public void setLandEffect(int landEffect) {
		this.landEffect = landEffect;
	}

	@Override
	public boolean makeAddAndRemoveSounds() {
		return true;
	}
}
