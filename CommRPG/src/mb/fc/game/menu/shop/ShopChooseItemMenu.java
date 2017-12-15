package mb.fc.game.menu.shop;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import mb.fc.engine.CommRPG;
import mb.fc.engine.config.MenuConfiguration;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.item.Item.ItemDurability;
import mb.fc.game.listener.MenuListener;
import mb.fc.game.menu.HeroesStatMenu;
import mb.fc.game.menu.YesNoMenu;
import mb.fc.game.trigger.Trigger;
import mb.fc.game.ui.RectUI;
import mb.fc.game.ui.TextUI;

public class ShopChooseItemMenu extends HeroesStatMenu implements MenuListener
{
	protected boolean selectingItemState = false;
	protected int selectingItemIndex = 0;
	private ShopMessage shopMessage;
	private boolean isSellMenu = false;
	
	protected RectUI goldPanel;
	protected TextUI goldTitleText, goldAmountText;
	protected MenuConfiguration menuConfig;

	public ShopChooseItemMenu(StateInfo stateInfo, MenuListener listener, ShopMessage shopMessage) {
		super(stateInfo, listener);
		this.menuConfig = CommRPG.engineConfiguratior.getMenuConfiguration();
		this.shopMessage = shopMessage;
		
		if (shopMessage.getMessageType() == MessageType.SHOW_SHOP_SELL)
			isSellMenu = true;
		else
			isSellMenu = false;
		
		goldPanel = new RectUI(20, 89, 62, 32);
		goldTitleText = new TextUI("Gold", 25, 85);
		goldAmountText = new TextUI(stateInfo.getClientProfile().getGold() + "", 25, 97);
	}

	@Override
	protected void postRender(Graphics g) {
		if (selectingItemState)
		{
			g.setColor(Color.white);
			g.drawRect(207, yOffsetTop + 50 + selectingItemIndex * 20, 80, 20);
		}
		
		// Draw gold box
		goldPanel.drawPanel(g);
		g.setColor(Color.white);
		goldTitleText.drawText(g);
		goldAmountText.drawText(g);
	}



	@Override
	protected MenuUpdate onUp(StateInfo stateInfo) {
		if (selectingItemState)
		{
			if (selectingItemIndex > 0)
				selectingItemIndex--;
			else
				selectingItemIndex = selectedHero.getItemsSize() - 1;
			return MenuUpdate.MENU_ACTION_LONG;
		}
		else
			return super.onUp(stateInfo);
	}



	@Override
	protected MenuUpdate onDown(StateInfo stateInfo) {
		if (selectingItemState)
		{
			if (selectingItemIndex < selectedHero.getItemsSize() - 1)
				selectingItemIndex++;
			else
				selectingItemIndex = 0;
			return MenuUpdate.MENU_ACTION_LONG;
		}
		else
			return super.onDown(stateInfo);
	}



	@Override
	protected MenuUpdate onBack(StateInfo stateInfo) {
		if (selectingItemState)
		{
			selectingItemState = false;
			return MenuUpdate.MENU_ACTION_LONG;
		}
		else
		{
			selectedHero = null;
			return MenuUpdate.MENU_CLOSE;
		}
	}

	@Override
	protected MenuUpdate onConfirm(StateInfo stateInfo) {
		// Show the item selection cursor
		if (!selectingItemState)
		{
			// Check if the hero has items to sell
			if (selectedHero.getItemsSize() > 0 ) {
				selectingItemState = true;
				selectingItemIndex = 0;
			}
		}
		// Otherwise we are done, prompt to sell the selected item
		else
		{
			if (isSellMenu)
				promptSellItem(stateInfo);	
			else
				prompRepairItem(stateInfo);
		}

		return MenuUpdate.MENU_ACTION_LONG;
	}

	private void promptSellItem(StateInfo stateInfo) {
		Item item = selectedHero.getItem(selectingItemIndex);
		String sellText = null;
		if (item.isDeal()) {
			sellText = menuConfig.getShopPromptSellDealText(item.getName(), (int) (item.getCost() * shopMessage.getSellPercent()) + "");
		} else {
			sellText = menuConfig.getShopPromptSellNormalText(item.getName(), (int) (item.getCost() * shopMessage.getSellPercent()) + "");
		}
		stateInfo.addMenu(new YesNoMenu(sellText, Trigger.TRIGGER_NONE, shopMessage.getPortrait(stateInfo), stateInfo, this));
	}
	
	private void prompRepairItem(StateInfo stateInfo) {
		Item item = selectedHero.getItem(selectingItemIndex);
		switch (item.getDurability()) {
		case BROKEN:
			
			stateInfo.addMenu(new YesNoMenu(menuConfig.getShopPromptRepairBrokenText(item.getName(), (int) (item.getCost() * .5) + ""),
					Trigger.TRIGGER_NONE, shopMessage.getPortrait(stateInfo), stateInfo, this));
			break;
		case DAMAGED:
			stateInfo.addMenu(new YesNoMenu(menuConfig.getShopPromptRepairDamagedText(item.getName(), (int) (item.getCost() * .2) + ""), 
					Trigger.TRIGGER_NONE, shopMessage.getPortrait(stateInfo),stateInfo, this));
			break;
		case PERFECT:
			stateInfo.sendMessage(new SpeechMessage(menuConfig.getShopItemNotDamagedText(item.getName()), 
					Trigger.TRIGGER_NONE, shopMessage.getPortrait(stateInfo)));
			break;
		default:
			break;
		
		}
	}

	@Override
	public Object getExitValue() {
		if (selectedHero != null)
			return new Object[] {selectedHero, selectingItemIndex};
		else
			return null;
	}

	@Override
	public void valueSelected(StateInfo stateInfo, Object value) {
		if ((boolean) value) {
			if (isSellMenu)
				sellItem(stateInfo);
			else
				repairItem(stateInfo);
		} else {
			if (isSellMenu)
				stateInfo.sendMessage(new SpeechMessage(menuConfig.getShopTransactionCancelledText(), 
						Trigger.TRIGGER_NONE, shopMessage.getPortrait(stateInfo)));
			else
				stateInfo.sendMessage(new SpeechMessage(menuConfig.getShopRepairCancelledText(), 
						Trigger.TRIGGER_NONE, shopMessage.getPortrait(stateInfo)));
		}
		selectingItemState = false;
	}

	private void sellItem(StateInfo stateInfo) {
		Item item = selectedHero.getItem(selectingItemIndex);
		if (selectedHero.getEquipped().get(selectingItemIndex)) {
			selectedHero.unequipItem((EquippableItem) item);
		}
		selectedHero.removeItem(item);
		if (item.isDeal()) {
			stateInfo.getClientProgress().getDealItems().add(item.getItemId());
		}
		stateInfo.getClientProfile().setGold(stateInfo.getClientProfile().getGold() + (int) (item.getCost() * shopMessage.getSellPercent()));
		goldAmountText.setText(stateInfo.getClientProfile().getGold() + "");
		stateInfo.sendMessage(new SpeechMessage(menuConfig.getShopTransactionSuccessfulText(), 
				Trigger.TRIGGER_NONE, shopMessage.getPortrait(stateInfo)));
	}
	
	private void repairItem(StateInfo stateInfo) {
		
		Item item = selectedHero.getItem(selectingItemIndex);
		int cost = 0;
		if (item.getDurability() == ItemDurability.BROKEN)
			cost = (int) (item.getCost() * .5);
		else if (item.getDurability() == ItemDurability.DAMAGED)
			cost = (int) (item.getCost() * .2);
		
		if (cost > stateInfo.getClientProfile().getGold()) {
			stateInfo.sendMessage(new SpeechMessage(menuConfig.getShopNotEnoughGoldText(), 
					Trigger.TRIGGER_NONE, shopMessage.getPortrait(stateInfo)));
		} else {
			stateInfo.getClientProfile().setGold(stateInfo.getClientProfile().getGold() - cost);
			item.setDurability(ItemDurability.PERFECT);
			goldAmountText.setText(stateInfo.getClientProfile().getGold() + "");
			
			stateInfo.sendMessage(new SpeechMessage(menuConfig.getShopItemRepairedText(), 
					Trigger.TRIGGER_NONE, shopMessage.getPortrait(stateInfo)));
		}
	}

	@Override
	public void menuClosed() {

	}
}
