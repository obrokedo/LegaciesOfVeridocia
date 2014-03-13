package mb.fc.renderer;

import mb.fc.engine.message.Message;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.manager.Manager;

// This is a catch all for any sort of menu that is going to be rendered but not have focus
public class PanelRenderer extends Manager
{	
	public PanelRenderer() {
		
	}
	
	public void render()
	{		
		// displayMenubar();
		if (stateInfo.arePanelsDisplayed())
			for (Panel m : stateInfo.getPanels())
				m.render(stateInfo.getGc(), stateInfo.getGraphics());
	}
	
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void recieveMessage(Message message) {
		// TODO Auto-generated method stub
		
	}	
}
