package mb.fc.game.manager;

import mb.fc.engine.message.ChatMessage;
import mb.fc.engine.message.Message;
import mb.fc.game.hudmenu.ChatPanel;
import mb.fc.game.hudmenu.MapMoveMenu;
import mb.fc.game.hudmenu.WaitPanel;

import org.newdawn.slick.Input;

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

	public void update() {
		int mouseX = stateInfo.getGc().getInput().getMouseX();
		int mouseY = stateInfo.getGc().getInput().getMouseY();

		boolean mouseDownLeft = stateInfo.getGc().getInput()
				.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON);
		boolean mouseDownRight = stateInfo.getGc().getInput()
				.isMouseButtonDown(Input.MOUSE_RIGHT_BUTTON);

		if (System.currentTimeMillis() < stateInfo.getInputDelay())
			mouseDownLeft = mouseDownRight = false;

		int mapX = stateInfo.getCamera().getLocationX() + mouseX
				- stateInfo.getGc().getDisplayPaddingX();
		int mapY = stateInfo.getCamera().getLocationY() + mouseY;

		for (int i = 0; i < stateInfo.getMouseListenersSize(); i++) {
			boolean consumeClick = stateInfo.getMouseListener(i).mouseUpdate(
					mouseX, mouseY, mapX, mapY, mouseDownLeft, mouseDownRight,
					stateInfo);
			if (consumeClick) {
				mouseDownLeft = mouseDownRight = false;
				stateInfo.setInputDelay(System.currentTimeMillis() + 200);
			}
		}

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
