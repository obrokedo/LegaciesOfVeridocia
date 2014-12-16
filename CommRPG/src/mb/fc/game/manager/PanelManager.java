package mb.fc.game.manager;

import mb.fc.engine.message.InfoMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.StringMessage;
import mb.fc.game.hudmenu.ChatPanel;
import mb.fc.game.hudmenu.MapEntryPanel;
import mb.fc.game.hudmenu.Panel;

public class PanelManager extends Manager {

	private ChatPanel chatMenu;
	@Override
	public void initialize() {
		chatMenu = new ChatPanel();
		stateInfo.addPanel(chatMenu);
	}

	public void update(int delta)
	{
		Panel panelToRemove = null;
		for (Panel panel : stateInfo.getPanels())
		{
			switch (panel.update(delta))
			{
				case MENU_CLOSE:
					panelToRemove = panel;
					break;
				default:
					break;
			}
		}

		if (panelToRemove != null)
			stateInfo.removePanel(panelToRemove);
	}

	@Override
	public void recieveMessage(Message message) {
		switch (message.getMessageType()) {
		case SEND_INTERNAL_MESSAGE:
			chatMenu.addMessage(((InfoMessage) message).getText());
			break;
		case DISPLAY_MAP_ENTRY:
			stateInfo.addPanel(new MapEntryPanel(((StringMessage) message).getString()));
			break;
		default:
			break;
		}
	}
}
