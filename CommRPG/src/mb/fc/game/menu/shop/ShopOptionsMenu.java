package mb.fc.game.menu.shop;

import org.newdawn.slick.Image;

import mb.fc.engine.CommRPG;
import mb.fc.engine.config.MenuConfiguration;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.menu.QuadMenu;
import mb.fc.game.trigger.Trigger;

public class ShopOptionsMenu extends QuadMenu
{
	private ShopMessage shopMessage;
	protected MenuConfiguration menuConfig;

	public ShopOptionsMenu(ShopMessage shopMessage, StateInfo stateInfo) {
		super(PanelType.PANEL_SHOP_OPTIONS, shopMessage.getPortrait(stateInfo), true, stateInfo);
		this.menuConfig = CommRPG.engineConfiguratior.getMenuConfiguration();
		this.shopMessage = shopMessage;
		
		icons = new Image[8];

		for (int i = 0; i < icons.length; i++)
			icons[i] = stateInfo.getResourceManager().getSpriteSheet("actionicons").getSubImage(i % 4 + 11, i / 4);
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
		stateInfo.sendMessage(new SpeechMessage(menuConfig.getShopMenuClosedText(), Trigger.TRIGGER_NONE, portrait));
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
				break;
			case LEFT:
				if (stateInfo.getClientProgress().getDealItems().size() > 0) {
					shopMessage.setMenuTypeShopDeals();
					stateInfo.sendMessage(shopMessage);
				} else {
					stateInfo.sendMessage(new SpeechMessage(menuConfig.getShopNoDealsText(), Trigger.TRIGGER_NONE, portrait));
				}
				break;
			case RIGHT:
				shopMessage.setMenuTypeShopRepair();
				stateInfo.sendMessage(shopMessage);
				break;
			case DOWN:
				shopMessage.setMenuTypeShopSell();
				stateInfo.sendMessage(shopMessage);
				break;
		}
		return MenuUpdate.MENU_ACTION_LONG;
	}
	
	

	@Override
	public MenuUpdate update(int delta) {
		// TODO Auto-generated method stub
		return super.update(delta);
	}

	@Override
	public boolean displayWhenNotTop() {
		return false;
	}
}
