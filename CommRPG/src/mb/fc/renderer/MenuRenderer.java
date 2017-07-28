package mb.fc.renderer;

import org.newdawn.slick.Graphics;

import mb.fc.engine.message.Message;
import mb.fc.game.manager.Manager;
import mb.fc.game.menu.Menu;

public class MenuRenderer extends Manager {

	@Override
	public void initialize() {

	}

	public void render(Graphics graphics)
	{
		if (stateInfo.areMenusDisplayed())
		for (Menu m : stateInfo.getMenus())
		{
			if (m.displayWhenNotTop() || m == stateInfo.getTopMenu())
				m.render(stateInfo.getFCGameContainer(), graphics);
		}
	}

	@Override
	public void recieveMessage(Message message)
	{

	}
}
