package mb.fc.game.menu;

import mb.fc.engine.CommRPG;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;

public class LandEffectPanel extends Panel
{
	private int landEffect = 0;

	public LandEffectPanel() {
		super(Panel.PANEL_LAND_EFFECT);
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) {
		Panel.drawPanelBox(gc.getDisplayPaddingX() + 15, 15, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 57, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 43, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawString("Land", gc.getDisplayPaddingX() +  CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 13 + 16, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 1);
		graphics.drawString("Effect:", gc.getDisplayPaddingX() +  CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 6 + 15, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 13);
		graphics.drawString(landEffect + "%", gc.getDisplayPaddingX() +  CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 17 + 20, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 26);
	}

	public void setLandEffect(int landEffect) {
		this.landEffect = landEffect;
	}

	@Override
	public boolean makeAddAndRemoveSounds() {
		return true;
	}
}
