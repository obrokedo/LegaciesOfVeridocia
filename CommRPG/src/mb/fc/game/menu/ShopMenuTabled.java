package mb.fc.game.menu;


public class ShopMenuTabled { /*extends PersistentMenu implements YesNoListener
{
	private int x;
	
	private boolean buy = true;
	private Button switchViewButton;
	
	
	private ArrayList<Item> items;	
	private Item selectedItem = null;	
	private Table<Item> itemTable;
	
	private ArrayList<CombatSprite> heroes;
	private int heroOffset = 0;
	private int mouseOverHeroes = -1;
	private Rectangle heroUpRect;
	private Rectangle heroDownRect;
	private Rectangle charSelectRect;	
	private CombatSprite selectedHero = null;
	
	private Button buyButton;	
	
	private ArrayList<String> differences;
	private int gold;
	private double sellPercent = .75;
	private double buyPercent = 1;
	private int waitingYesNo = 0;
	private EquippableItem oldItem;
	
	public ShopMenu(GameContainer gc, StateInfo stateInfo, double sellPercent, double buyPercent, int[] itemIds) {
		super(Menu.MENU_SHOP, gc);
		x = (gc.getWidth() - 700) / 2;
		switchViewButton = new Button(x + 100, 35, 140, 20, "Switch to sell");
		heroUpRect = new Rectangle(x + 670, 476, 15, 15);
		heroDownRect = new Rectangle(x + 670, 650, 15, 15);
		buyButton = new Button(x + 570, 680, 100, 20, "Buy");
		charSelectRect = new Rectangle(x + 15, 475, 655, 180);
		
		this.heroes = stateInfo.getClientProfile().getHeroes();
		this.gold = stateInfo.getClientProfile().getGold();
		items = new ArrayList<Item>();
		differences = new ArrayList<String>();
		
		for (Integer i : itemIds)
			items.add(ItemResource.getItem(i, stateInfo));
		for (Integer i : itemIds)
			items.add(ItemResource.getItem(i, stateInfo));
		for (Integer i : itemIds)
			items.add(ItemResource.getItem(i, stateInfo));
		for (Integer i : itemIds)
			items.add(ItemResource.getItem(i, stateInfo));
		for (Integer i : itemIds)
			items.add(ItemResource.getItem(i, stateInfo));
		
		selectedHero = heroes.get(0);
		this.sellPercent = sellPercent;
		this.buyPercent = buyPercent;
		
		itemTable = new Table<Item>(x + 15, 70, new int[] {200, 355, 100}, new String[] {"Name", "Description", "Cost"}, 10, items,
				new ItemTableCellRenderer());
	}

	@Override
	public boolean handleUserInput(int mouseX, int mouseY, boolean leftClick,
			boolean rightClick, StateInfo stateInfo) {
		if (rightClick)
		{
			stateInfo.getClientProfile().setGold(gold);
			return true;
		}
		
		// Handle switching the view from buy/sell
		if (switchViewButton.handleUserInput(mouseX, mouseY, leftClick))
		{
			if (buy)
			{
				switchViewButton.setText("Switch to buy");
				buyButton.setText("Sell");
			}
			else
			{
				switchViewButton.setText("Switch to sell");
				buyButton.setText("Buy");
			}
			
			differences.clear();
			buy = !buy;
			ArrayList<Item> heroItems = new ArrayList<Item>();
			for (int i = 0; i < selectedHero.getItemsSize(); i++)
				heroItems.add(selectedHero.getItem(i));
			itemTable.setItems(heroItems);
			selectedItem = null;			
		}
		
		// Handle heroes
		if (leftClick && heroUpRect.contains(mouseX, mouseY))
			heroOffset = Math.max(heroOffset - 1, 0);
		else if (leftClick && heroes.size() > 6 && heroDownRect.contains(mouseX, mouseY))
			heroOffset = Math.min(heroOffset + 1, heroes.size() - 6);
		
		if (buyButton.handleUserInput(mouseX, mouseY, leftClick) && selectedItem != null)
		{
			if (buy)
			{					
				if (selectedHero.getItemsSize() < 4)
				{
					selectedHero.addItem(selectedItem);
					gold -= (int) (selectedItem.getCost() * buyPercent);
					
					if (selectedItem.isEquippable() && selectedHero.isEquippable((EquippableItem) selectedItem))
					{
						waitingYesNo = 1;
						stateInfo.getMenus().add(new YesNoMenu(stateInfo.getGc(), "Would you like to equip it now?", this));
					}
				}
				else
					stateInfo.sendMessage(new ChatMessage(Message.MESSAGE_SEND_INTERNAL_MESSAGE, "SYSTEM", "Selected character already has 4 items."), false);
			}
			else
			{
				selectedHero.removeItem(selectedItem);
				gold += (int) (selectedItem.getCost() * sellPercent);
				selectedItem = null;
			}
		}
		
		// Select Heroes
		if (charSelectRect.contains(mouseX, mouseY))
		{
			int over = ((mouseY - 475) / 30);
			
			if (over < heroes.size())
			{
				mouseOverHeroes = over;
				if (leftClick)
				{
					selectedHero = heroes.get(mouseOverHeroes + heroOffset);
					if (!buy)
					{
						selectedItem = null;
					}
				}
			}
		}
		
		else
			mouseOverHeroes = -1;
		
		// Handle items
		Item item = itemTable.handleUserInput(mouseX, mouseY, leftClick);
		if (item != null)
		{
			selectedItem = item;
			determineDifferences();
		}		
		
		return false;
	}
	
	public void determineDifferences()
	{
		differences.clear();
		if (selectedItem.isEquippable())
		{
			int type = ((EquippableItem) selectedItem).getItemType();
			
			for (CombatSprite hero : heroes)
			{
				EquippableDifference ed = null;
				if (hero.isEquippable((EquippableItem) selectedItem))
				{
					
					if (type == EquippableItem.TYPE_WEAPON)
						ed = Item.getEquippableDifference(hero.getEquippedWeapon(), (EquippableItem) selectedItem);
					else if (type == EquippableItem.TYPE_ARMOR)
						ed = Item.getEquippableDifference(hero.getEquippedArmor(), (EquippableItem) selectedItem);
					else if (type == EquippableItem.TYPE_RING)
						ed = Item.getEquippableDifference(hero.getEquippedRing(), (EquippableItem) selectedItem);
					differences.add("ATK: " + ed.atk + 
						" DEF: " + ed.def +
						" SPD: " + ed.spd);
				}
				else
					differences.add("Can not equip");
			}
		}
	}

	@Override
	public void render(GameContainer gc, Graphics graphics) {		
		Menu.drawMenuBox(x, 25, 700, 700, graphics);
	
		graphics.setColor(Menu.COLOR_FOREFRONT);
		
		// Draw Shop Type
		graphics.drawString("Shop", x + 15, 35);
		
		switchViewButton.render(gc, graphics);

		itemTable.render(gc, graphics);
		
		// Draw buy buttons
		buyButton.render(gc, graphics);
		
		graphics.drawString("Gold: " + gold, x + 15, 680);		
		
		// Draw usuable by box
		if (mouseOverHeroes != -1)
		{
			graphics.setColor(Menu.COLOR_MOUSE_OVER);
			graphics.fillRect(x + 15, 475 + (mouseOverHeroes * 30), 655, 30);			
		}
		
		graphics.setColor(Menu.COLOR_FOREFRONT);
		
		for (int i = 0; i < Math.min(heroes.size(), 6); i++)
		{
			if (heroes.get(i + heroOffset) == selectedHero)
			{
				graphics.setColor(Menu.COLOR_MOUSE_OVER);
				graphics.fillRect(x + 15, 475 + (i * 30), 655, 30);
				graphics.setColor(Menu.COLOR_FOREFRONT);
			}
			
			graphics.drawString(heroes.get(i + heroOffset).getName(), x + 25, 485 + i * 30);
			
			if (differences.size() > 0)
				graphics.drawString(differences.get(i),
										x + 210, 485 + i * 30);
		}
		
		
		graphics.drawRect(x + 15, 475, 670, 190);
		graphics.drawLine(x + 200, 475, x + 200, 665);
		
		graphics.setColor(Color.darkGray);
		graphics.fillRect(x + 670, 476, 15, 189);
		
		graphics.setColor(Color.lightGray);
		Menu.fillRect(heroUpRect, graphics);
		Menu.fillRect(heroDownRect, graphics);
		
		graphics.setColor(Menu.COLOR_FOREFRONT);
		graphics.drawString("^", heroUpRect.getX() + 2, heroUpRect.getY() + 2);
		
		graphics.drawString("v", heroDownRect.getX() + 2, heroDownRect.getY() - 1);
		
		
		
		/*
		graphics.setColor(Color.red);
		Menu.drawRect(charSelectRect, graphics);
		*/
	/*
	}

	@Override
	public boolean valueSelected(StateInfo stateInfo, boolean value) {
		// Equip the item based on value
		if (waitingYesNo == 1)
		{			
			if (value)
			{
				oldItem = selectedHero.equipItem((EquippableItem) selectedItem);
				determineDifferences();
			
				if (oldItem != null)
				{
					waitingYesNo = 2;
					stateInfo.getMenus().add(new YesNoMenu(stateInfo.getGc(), "Would you like to sell your previously equipped " + oldItem.getName() + "?", this));
				}
			}
		}
		else if (waitingYesNo == 2)
		{
			if (value)
			{
				selectedHero.removeItem(oldItem);
				gold += (int) (oldItem.getCost() * sellPercent);
				oldItem = null;
			}
		}
		return false;
	} */
}
