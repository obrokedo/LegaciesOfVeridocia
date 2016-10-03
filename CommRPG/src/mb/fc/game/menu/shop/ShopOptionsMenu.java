package mb.fc.game.menu.shop;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.menu.QuadMenu;

import org.newdawn.slick.Image;

public class ShopOptionsMenu extends QuadMenu
{
	private ShopMessage shopMessage;

	public ShopOptionsMenu(ShopMessage shopMessage, StateInfo stateInfo) {
		super(PanelType.PANEL_SHOP_OPTIONS, true, stateInfo);
		this.shopMessage = shopMessage;

		icons = new Image[8];

		for (int i = 0; i < icons.length; i++)
			icons[i] = stateInfo.getResourceManager().getSpriteSheets().get("actionicons").getSubImage(i % 4 + 11, i / 4);
		enabled = new boolean[4];
		for (int i = 0; i < enabled.length; i++)
			enabled[i] = true;
		text = new String[] {"Buy", "Deals", "Repair", "Sell"};
	}

	@Override
	public void initialize() {

	}

	@Override
	protected MenuUpdate onBack() {
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	protected MenuUpdate onConfirm() {
		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));
		switch (selected)
		{
			case UP:
				shopMessage.setMenuTypeShopBuy();
				stateInfo.sendMessage(shopMessage);
				return MenuUpdate.MENU_CLOSE;
			case LEFT:
				// stateInfo.sendMessage(MessageType.SHOW_SPELLMENU);
				return MenuUpdate.MENU_CLOSE;
			case RIGHT:
				return MenuUpdate.MENU_CLOSE;
			case DOWN:
				stateInfo.sendMessage(MessageType.SHOW_SHOP_SELL);
				return MenuUpdate.MENU_CLOSE;
		}
		return MenuUpdate.MENU_NO_ACTION;
	}

}
