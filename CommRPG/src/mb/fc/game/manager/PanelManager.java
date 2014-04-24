package mb.fc.game.manager;

import mb.fc.engine.message.ChatMessage;
import mb.fc.engine.message.Message;
import mb.fc.game.hudmenu.ChatPanel;
import mb.fc.game.hudmenu.MapMoveMenu;
import mb.fc.game.hudmenu.WaitPanel;

public class PanelManager extends Manager {

	private MapMoveMenu mapMoveMenu;
	private ChatPanel chatMenu;
	private WaitPanel waitMenu;

	@Override
	public void initialize() {
		if (stateInfo.isCombat()) {
			mapMoveMenu = new MapMoveMenu(stateInfo);
			stateInfo.addPanel(mapMoveMenu);

		}

		waitMenu = new WaitPanel();
		chatMenu = new ChatPanel();
		stateInfo.addPanel(chatMenu);
	}

	public void update() 
	{

		chatMenu.update(1);
	}

	@Override
	public void recieveMessage(Message message) {
		switch (message.getMessageType()) {
		case Message.MESSAGE_SEND_INTERNAL_MESSAGE:
			chatMenu.addMessage(((ChatMessage) message).getText());
			break;
		case Message.MESSAGE_SHOW_WAIT:
			if (waitMenu != null)
				stateInfo.addPanel(waitMenu);
			break;
		}
	}
}
