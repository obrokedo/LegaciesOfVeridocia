package mb.fc.game.manager;

import mb.fc.engine.message.InfoMessage;
import mb.fc.engine.message.Message;
import mb.fc.game.hudmenu.ChatPanel;

public class PanelManager extends Manager {

	private ChatPanel chatMenu;
	@Override
	public void initialize() {
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
			chatMenu.addMessage(((InfoMessage) message).getText());
			break;
		}
	}
}
