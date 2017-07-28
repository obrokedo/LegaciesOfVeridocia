package mb.fc.game.menu;

public class ShopMenuTabled { /* extends Menu implements YesNoListener
{
	protected double sellPercent;
	protected double buyPercent;
	protected Item[] items;
	protected Image[] costImages;
	protected int selectedItemIndex = 0;
	protected Item selectedItem;
	protected String[] itemName;
	protected Font smallFont;
	protected int gold;
	protected SpeechMenu speechMenu;
	protected boolean hasFocus = true;

	// Base UI Shapes
	protected Polygon leftArrow, rightArrow;
	protected RectUI itemPanel, itemNamePanel, goldPanel, selectedItemRect;
	protected TextUI itemNameText1, itemNameText2, itemCostText, goldTitleText, goldAmountText;

	public ShopMenuTabled(StateInfo stateInfo,
			double sellPercent, double buyPercent, int[] itemIds) {
		super(Panel.PANEL_SHOP);

		this.sellPercent = sellPercent;
		this.buyPercent = buyPercent;
		this.items = new Item[itemIds.length];
		this.smallFont = stateInfo.getResourceManager().getFontByName("smallmenufont");
		this.gold = stateInfo.getClientProfile().getGold();

		for (int i = 0; i < items.length; i++)
		{
			items[i] = ItemResource.getItem(itemIds[i], stateInfo);
			System.out.println(items[i].getName());
			if (items[i].isEquippable())
				System.out.println(((EquippableItem) items[i]).getItemType() + " " + ((EquippableItem) items[i]).getItemStyle());
		}

		selectedItem = items[0];
		itemName = selectedItem.getName().split(" ");

		costImages = new Image[items.length];
		for (int i = 0; i < costImages.length; i++)
		{

			try {
				Image im = new Image(8 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						24 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);

				Graphics g = im.getGraphics();

				costImages[i] = im;

				g.setFont(smallFont);

				g.rotate(3 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						3 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 90);
				g.setColor(Color.white);
				g.drawString("" + items[i].getCost(), 0, 0);
				g.destroy();
			} catch (SlickException e) {}
		}

		rightArrow = new Polygon(new float[] {
				285 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 13 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				289 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 17 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				285 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 21 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]});


		leftArrow = new Polygon(new float[] {
				35 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 13 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				31 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 17 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				35 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 21 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]});

		itemPanel = new RectUI(27, 2, -54, 32, 0, 0, CommRPG.GAME_SCREEN_SIZE.width, 0);
		itemNamePanel = new RectUI(27, 34, 68, 37);
		selectedItemRect = new RectUI(36,  6,  24,  24);
		updateSelectedItem();

		// Setup gold
		goldPanel = new RectUI(241, 148, 62, 32);
		goldTitleText = new TextUI("Gold", 246, 144);
		goldAmountText = new TextUI(gold + "", 246, 156);

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
			speechMenu.handleUserInput(input, stateInfo);
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
		else if (input.isKeyDown(Input.KEY_F))
		{
			stateInfo.removeTopMenu();
			stateInfo.addMenu(new ShopMenuTabled(stateInfo, .8, 1.2, new int[] {1, 1, 2, 2, 0, 0, 1, 1, 2, 2, 0, 0}));
			return MenuUpdate.MENU_NO_ACTION;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_3))
		{
			showCostPanel(stateInfo);
			// stateInfo.sendMessage(new IntMessage(MessageType.SHOW_SHOP_HERO_SELECT, selectedItem.getItemId()));
			return MenuUpdate.MENU_ACTION_LONG;
		}

		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) {
		// Draw items box
		itemPanel.drawPanel(graphics);

		// Draw items and price
		for (int i = (selectedItemIndex < 9 ? 0 : selectedItemIndex - 8); i < Math.min(items.length,  (selectedItemIndex < 9 ? 9 : selectedItemIndex + 1)); i++)
		{
			items[i].getImage().draw(36 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] +
				(28 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (i - (selectedItemIndex < 9 ? 0 : selectedItemIndex - 8))),
					6 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);

			costImages[i].draw(51 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] +
				(28 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * (i - (selectedItemIndex < 9 ? 0 : selectedItemIndex - 8))),
					7 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
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
	}

	private void showCostPanel(StateInfo stateInfo)
	{
		hasFocus = false;
		speechMenu = new YesNoMenu("The " + selectedItem.getName() + " costs " + selectedItem.getCost() + " gold coins. Is that OK?]", stateInfo, this);
	}

	@Override
	public boolean valueSelected(StateInfo stateInfo, boolean value) {
		hasFocus = true;
		if (!value)
			showBuyPanel(stateInfo);
		return false;
	}
	*/
}
