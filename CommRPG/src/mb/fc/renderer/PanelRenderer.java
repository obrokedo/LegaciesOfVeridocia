package mb.fc.renderer;

import org.newdawn.slick.Graphics;

import mb.fc.engine.message.Message;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.manager.Manager;

// This is a catch all for any sort of menu that is going to be rendered but not have focus
public class PanelRenderer extends Manager
{
	public PanelRenderer() {

	}

	public void render(Graphics graphics)
	{
		// displayMenubar();
		if (stateInfo.arePanelsDisplayed())
			for (Panel m : stateInfo.getPanels())
				m.render(stateInfo.getPaddedGameContainer(), graphics);
	}

	@Override
	public void initialize() {

	}

	@Override
	public void recieveMessage(Message message) {

	}
}
