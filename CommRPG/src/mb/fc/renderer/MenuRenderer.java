package mb.fc.renderer;

import mb.fc.engine.message.Message;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.manager.Manager;

public class MenuRenderer extends Manager {

	@Override
	public void initialize() {
		
	}

	public void render()
	{		
		if (stateInfo.areMenusDisplayed())
		for (Panel m : stateInfo.getMenus())
			m.render(stateInfo.getGc(), stateInfo.getGraphics());
	}

	@Override
	public void recieveMessage(Message message) 
	{
			
	}
}
