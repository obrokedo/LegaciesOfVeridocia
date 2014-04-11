package mb.fc.game.menu;

import org.newdawn.slick.Graphics;

import mb.fc.engine.CommRPG;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.ui.FCGameContainer;

public class LandEffectPanel extends Panel
{
	private int landEffect = 0;

	public LandEffectPanel() {
		super(Panel.PANEL_LAND_EFFECT);
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) {
		Panel.drawPanelBox(gc.getDisplayPaddingX() + 15, gc.getHeight() - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 25, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 112, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 20, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawString("Land Effect:" + landEffect + "%", gc.getDisplayPaddingX() + 30, gc.getHeight() - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 29);
	}

	public void setLandEffect(int landEffect) {
		this.landEffect = landEffect;
	}
}
