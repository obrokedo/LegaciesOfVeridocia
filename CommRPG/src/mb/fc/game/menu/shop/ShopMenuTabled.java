package mb.fc.game.menu.shop;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.listener.MenuListener;
import mb.fc.game.menu.Menu;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.menu.YesNoMenu;
import mb.fc.game.resource.ItemResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.game.ui.RectUI;
import mb.fc.game.ui.TextUI;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Polygon;

public class ShopMenuTabled extends Menu implements MenuListener
{
	protected enum ShopStepEnum
	{
		SELECT_ITEM,
		IS_COST_OK,
		WHO_WILL_USE,
		SELECT_HERO,
		SALE_COMPLETED,
		SELL_OLD_WEAPON,
		EQUIP_NOW
	}

	protected double sellPercent;
	protected double buyPercent;
	protected Item[] items;
	protected int selectedItemIndex = 0;
	protected Item selectedItem;
	protected String[] itemName;
	protected Font smallFont;
	protected int gold;
	protected SpeechMenu speechMenu;
	protected boolean hasFocus = true;
	protected ShopStepEnum currentStep;
	protected CombatSprite selectedHero;
	protected ShopMessage shopMessage;
	protected StateInfo stateInfo;

	// Base UI Shapes
	protected Polygon leftArrow, rightArrow;
	protected RectUI itemPanel, itemNamePanel, goldPanel, selectedItemRect;
	protected TextUI itemNameText1, itemNameText2, itemCostText, goldTitleText, goldAmountText;

	public ShopMenuTabled(StateInfo stateInfo, ShopMessage shopMessage) {
		super(Panel.PANEL_SHOP);

		this.sellPercent = shopMessage.getSellPercent();
		this.buyPercent = shopMessage.getBuyPercent();
		this.items = new Item[shopMessage.getItemIds().length];
		this.smallFont = stateInfo.getResourceManager().getFontByName("smallmenufont");
		this.gold = stateInfo.getClientProfile().getGold();

		for (int i = 0; i < items.length; i++)
			items[i] = ItemResource.getItem(shopMessage.getItemIds()[i], stateInfo);

		selectedItem = items[0];
		itemName = selectedItem.getName().split(" ");

		rightArrow = new Polygon(new float[] {
				285 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 13 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				289 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 17 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				285 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 21 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]});


		leftArrow = new Polygon(new float[] {
				35 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 13 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				31 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 17 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				35 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 21 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]});

		itemPanel = new RectUI(27, 2, -54, 32, 0, 0, stateInfo.getGc().getWidth(), 0);
		itemNamePanel = new RectUI(27, 34, 68, 37);
		selectedItemRect = new RectUI(36,  6,  24,  24);
		updateSelectedItem();

		// Setup gold
		goldPanel = new RectUI(241, 148, 62, 32);
		goldTitleText = new TextUI("Gold", 246, 144);
		goldAmountText = new TextUI(gold + "", 246, 156);
		this.stateInfo = stateInfo;
		this.shopMessage = shopMessage;

		showBuyPanel(stateInfo);
	}

	@Override
	public MenuUpdate update(long delta, StateInfo stateInfo) {
		if (speechMenu != null)
			speechMenu.update(delta, stateInfo);
		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		if (!hasFocus)
		{
			MenuUpdate mu = speechMenu.handleUserInput(input, stateInfo);
			if (input.isKeyDown(KeyMapping.BUTTON_3) || input.isKeyDown(KeyMapping.BUTTON_1))
				return MenuUpdate.MENU_ACTION_LONG;
			else
				return MenuUpdate.MENU_NO_ACTION;
		}

		if (input.isKeyDown(Input.KEY_RIGHT))
		{
			selectedItemIndex = Math.min(selectedItemIndex + 1, items.length - 1);
			selectedItem = items[selectedItemIndex];
			itemName = selectedItem.getName().split(" ");
			updateSelectedItem();
			return MenuUpdate.MENU_ACTION_LONG;
		}
		else if (input.isKeyDown(Input.KEY_LEFT))
		{
			selectedItemIndex = Math.max(0, selectedItemIndex - 1);
			selectedItem = items[selectedItemIndex];
			itemName = selectedItem.getName().split(" ");
			updateSelectedItem();
			return MenuUpdate.MENU_ACTION_LONG;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_1) || input.isKeyDown(KeyMapping.BUTTON_3))
		{
			showCostPanel(stateInfo);
			// stateInfo.sendMessage(new IntMessage(MessageType.SHOW_SHOP_HERO_SELECT, selectedItem.getItemId()));
			return MenuUpdate.MENU_ACTION_LONG;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_2))
		{
			stateInfo.getClientProfile().setGold(gold);
			shopMessage.setMenuTypeShopOptions();
			stateInfo.sendMessage(shopMessage);
			return MenuUpdate.MENU_CLOSE;
		}

		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) {
		if (currentStep != ShopStepEnum.IS_COST_OK && currentStep != ShopStepEnum.SELECT_ITEM)
			return;

		// Draw items box
		itemPanel.drawPanel(graphics);

		graphics.setColor(Color.white);
		graphics.setFont(smallFont);

		// Draw items and price
		for (int i = (selectedItemIndex < 9 ? 0 : selectedItemIndex - 8); i < Math.min(items.length,  (selectedItemIndex < 9 ? 9 : selectedItemIndex + 1)); i++)
		{
			items[i].getImage().draw(36 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] +
				(28 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (i - (selectedItemIndex < 9 ? 0 : selectedItemIndex - 8))),
					6 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);

			graphics.rotate(gc.getWidth() / 2, gc.getHeight() / 2, 90);
			graphics.drawString((int) (items[i].getCost() * buyPercent) + "", 49 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 216 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] -
					(28 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (i - (selectedItemIndex < 9 ? 0 : selectedItemIndex - 8))));
			graphics.rotate(gc.getWidth() / 2, gc.getHeight() / 2, -90);
		}

		// Draw item name

		itemNamePanel.drawPanel(graphics);
		graphics.setColor(Color.white);
		itemNameText1.drawText(graphics);
		if (itemName.length > 1)
			itemNameText2.drawText(graphics);
		itemCostText.drawText(graphics);

		// Draw gold box
		goldPanel.drawPanel(graphics);
		graphics.setColor(Color.white);
		goldTitleText.drawText(graphics);
		goldAmountText.drawText(graphics);

		// Draw selection box
		selectedItemRect.drawRect(graphics, Color.white);

		if (speechMenu != null)
			speechMenu.render(gc, graphics);

		graphics.setColor(Color.white);
		if (items.length > 9 && selectedItemIndex != items.length - 1)
			graphics.fill(rightArrow);

		if (selectedItemIndex > 8)
			graphics.fill(leftArrow);
	}

	private void updateSelectedItem()
	{
		itemNamePanel.setX(27 + 28 * Math.min(8, selectedItemIndex));
		itemNameText1 = new TextUI(itemName[0], 32 + 28  * Math.min(8, selectedItemIndex), 29);
		if (itemName.length > 1)
			itemNameText2 = new TextUI(itemName[1], 32 + 28 * Math.min(8, selectedItemIndex), 39);
		itemCostText = new TextUI(selectedItem.getCost() + "", 87 + 28 * Math.min(8, selectedItemIndex), 49,
				- PANEL_FONT.getWidth(selectedItem.getCost() + ""));
		selectedItemRect.setX(36 + 28 * Math.min(8, selectedItemIndex));
	}

	private void showBuyPanel(StateInfo stateInfo)
	{
		speechMenu = new SpeechMenu("What would you like to buy?]", stateInfo.getGc(),
				SpeechMenu.NO_TRIGGER, null, stateInfo);
		currentStep = ShopStepEnum.SELECT_ITEM;
	}

	private void showCostPanel(StateInfo stateInfo)
	{
		hasFocus = false;
		speechMenu = null;
		stateInfo.addMenu(new YesNoMenu("The " + selectedItem.getName() + " costs " + selectedItem.getCost() + " gold coins. Is that OK?]", stateInfo, this));
		currentStep = ShopStepEnum.IS_COST_OK;
	}

	@Override
	public boolean valueSelected(StateInfo stateInfo, Object value) {
		hasFocus = true;
		EquippableItem equipped;
		switch (currentStep)
		{
			case IS_COST_OK:
				if (!(boolean) value)
					showBuyPanel(stateInfo);
				else
				{
					currentStep = ShopStepEnum.WHO_WILL_USE;
					speechMenu = null;
					stateInfo.addMenu(new SpeechMenu("Who will use this?]", stateInfo.getGc(),
							SpeechMenu.NO_TRIGGER, null, stateInfo, this));
					//
				}
				break;
			case WHO_WILL_USE:
				currentStep = ShopStepEnum.SELECT_HERO;
				stateInfo.addMenu(new HeroesBuyMenu(stateInfo, this, selectedItem));
				break;
			case SELECT_HERO:
				if (value == null)
				{
					currentStep = ShopStepEnum.SALE_COMPLETED;
					stateInfo.addMenu(new SpeechMenu("I'm sorry we can't strike a deal...]", stateInfo.getGc(),
							SpeechMenu.NO_TRIGGER, null, stateInfo, this));
				}
				else
				{
					selectedHero = (CombatSprite) value;

					if (selectedHero.getItemsSize() < CombatSprite.MAXIMUM_ITEM_AMOUNT)
					{
						// If this is an equippable item that the hero can use
						if (selectedItem.isEquippable() && selectedHero.isEquippable((EquippableItem) selectedItem))
						{
							currentStep = ShopStepEnum.EQUIP_NOW;
							stateInfo.addMenu(new YesNoMenu("Would you like to equip it now?]", stateInfo, this));
						}
						// Otherwise it's not equippable or this hero can't equip it so just
						// put it in their inventor
						else
						{
							selectedHero.addItem(selectedItem);
							gold -= (int) (selectedItem.getCost() * buyPercent);
							goldAmountText.setText(gold + "");
							currentStep = ShopStepEnum.SALE_COMPLETED;
							stateInfo.addMenu(new SpeechMenu("A pleasure doing business with ya'!]", stateInfo.getGc(),
									SpeechMenu.NO_TRIGGER, null, stateInfo, this));
						}
					}
					// No room for items
					else
					{
						currentStep = ShopStepEnum.WHO_WILL_USE;
						stateInfo.addMenu(new SpeechMenu(selectedHero.getName() + " can't carry anything else... Who should use this?]", stateInfo.getGc(),
								SpeechMenu.NO_TRIGGER, null, stateInfo, this));
					}
				}
				break;

			case SALE_COMPLETED:
				showBuyPanel(stateInfo);
				break;
			case SELL_OLD_WEAPON:
				selectedHero.addItem(selectedItem);
				equipped = selectedHero.equipItem((EquippableItem) selectedItem);
				selectedHero.removeItem(equipped);
				gold += (int) (equipped.getCost() * sellPercent);
				goldAmountText.setText(gold + "");
				currentStep = ShopStepEnum.SALE_COMPLETED;
				stateInfo.addMenu(new SpeechMenu("A pleasure doing business with ya'!]", stateInfo.getGc(),
						SpeechMenu.NO_TRIGGER, null, stateInfo, this));
				break;
			case EQUIP_NOW:
				// Equip the item now
				if ((boolean) value)
				{
					equipped = selectedHero.getEquippedWeapon();
					gold -= (int) (selectedItem.getCost() * buyPercent);
					goldAmountText.setText(gold + "");
					// If they aren't equipped with anything then just equip this item
					// and be done
					if (equipped == null)
					{
						selectedHero.addItem(selectedItem);
						selectedHero.equipItem((EquippableItem) selectedItem);
						currentStep = ShopStepEnum.SALE_COMPLETED;
						stateInfo.addMenu(new SpeechMenu("A pleasure doing business with ya'!]", stateInfo.getGc(),
								SpeechMenu.NO_TRIGGER, null, stateInfo, this));
					}
					else
					{
						currentStep = ShopStepEnum.SELL_OLD_WEAPON;
						stateInfo.addMenu(new YesNoMenu("Would you like to sell your old " +
								equipped.getName() + " for " + ((int) (equipped.getCost() * sellPercent)) + " gold?]", stateInfo, this));
					}
				}
				// Don't equip the item, just put it in the inventory and complete
				else
				{
					selectedHero.addItem(selectedItem);
					gold -= (int) (selectedItem.getCost() * buyPercent);
					goldAmountText.setText(gold + "");
					currentStep = ShopStepEnum.SALE_COMPLETED;
					stateInfo.addMenu(new SpeechMenu("A pleasure doing business with ya'!]", stateInfo.getGc(),
							SpeechMenu.NO_TRIGGER, null, stateInfo, this));
				}
				break;
		}
		return false;
	}

	@Override
	public void menuClosed() {

	}
}
