package mb.fc.game.hudmenu;

import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;

public class WaitPanel extends Panel
{
	public WaitPanel() {
		super(Panel.PANEL_WAIT);
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) 
	{
		Panel.drawPanelBox(gc.getWidth() / 2 - 140, 340, 280, 40, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawString("Waiting for other players...", gc.getWidth() / 2 - 130, 350);
	}
}
