package mb.fc.renderer;

import mb.fc.engine.message.Message;
import mb.fc.game.manager.Manager;
import mb.fc.game.menu.Menu;

public class MenuRenderer extends Manager {

	@Override
	public void initialize() {

	}

	public void render()
	{
		if (stateInfo.areMenusDisplayed())
		for (Menu m : stateInfo.getMenus())
		{
			if (m.displayWhenNotTop() || m == stateInfo.getTopMenu())
				m.render(stateInfo.getGc(), stateInfo.getGraphics());
		}
	}

	@Override
	public void recieveMessage(Message message)
	{

	}
}
