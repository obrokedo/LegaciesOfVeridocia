package mb.fc.game.hudmenu;

import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;

public class WaitPanel extends Panel
{
	public WaitPanel() {
		super(PanelType.PANEL_WAIT);
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics)
	{
		Panel.drawPanelBox(160, 340, 630, 60, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawString("Waiting for other players...", 180, 330);
	}
}
