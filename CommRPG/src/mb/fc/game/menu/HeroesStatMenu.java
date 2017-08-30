package mb.fc.game.menu;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.item.Item.ItemDurability;
import mb.fc.game.listener.MenuListener;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.utils.StringUtils;

public class HeroesStatMenu extends Menu
{
	protected static final int VIEW_LEVEL = 0;
	protected static final int VIEW_STATS = 1;
	protected static final int VIEW_DIFFS = 2;
	protected static final Color COLOR_NONE = new Color(204, 0, 70);

	// Why the fuck is the yoffsettop negative?!!??!?!?!?
	protected int yOffsetTop = -14, yOffsetBot = 10;
	protected int selectedIndex = 0;
	protected ArrayList<CombatSprite> heroes;
	protected String[][] items;
	protected CombatSprite selectedHero;
	protected int view = VIEW_LEVEL;

	protected Portrait selectedHeroPortrait;

	public HeroesStatMenu(StateInfo stateInfo)
	{
		this(stateInfo, null);
	}

	public HeroesStatMenu(StateInfo stateInfo, MenuListener listener)
	{
		super(PanelType.PANEL_HEROS_OVERVIEW);
		heroes = new ArrayList<>();
		for (CombatSprite cs : stateInfo.getAllHeroes())
			heroes.add(cs);
		updateCurrentHero();
		this.listener = listener;
	}

	@Override
	public MenuUpdate update(long delta, StateInfo stateInfo) {
		if (selectedHeroPortrait != null)
			this.selectedHeroPortrait.update(delta);
		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		drawHeroSpecifics(graphics);

		Panel.drawPanelBox(20,
				yOffsetBot + 118,
				CommRPG.GAME_SCREEN_SIZE.width - 40,
			108, graphics);


		graphics.setColor(Color.white);
		graphics.drawRect(25,
				yOffsetBot + (134 + 15 * Math.min(selectedIndex, 11)),
				269, 15);
		StringUtils.drawString("NAME", 27,
				yOffsetBot + 113, graphics);

		if (view == VIEW_LEVEL)
		{
			StringUtils.drawString("LEVEL", 127,
					yOffsetBot + 113, graphics);
			StringUtils.drawString("EXP", 227,
					yOffsetBot + 113, graphics);
		}
		else if (view == VIEW_STATS)
		{
			StringUtils.drawString("HP", 92,
					yOffsetBot + 113, graphics);
			StringUtils.drawString("MP", 127,
					yOffsetBot + 113, graphics);
			StringUtils.drawString("ATK", 162,
					yOffsetBot + 113, graphics);
			StringUtils.drawString("DEF", 197,
					yOffsetBot + 113, graphics);
			StringUtils.drawString("SPD", 232,
					yOffsetBot + 113, graphics);
			StringUtils.drawString("MOV", 267,
					yOffsetBot + 113, graphics);
		}

		for (int i = (selectedIndex < 12 ? 0 : selectedIndex - 11); i < Math.min(heroes.size(),  (selectedIndex < 12 ? 12 : selectedIndex + 1)); i++)
		{
			StringUtils.drawString(heroes.get(i).getName(),
					27,
					yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))), graphics);

			if (view == VIEW_LEVEL)
			{
				StringUtils.drawString(heroes.get(i).getLevel() + "", 127,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))), graphics);
				StringUtils.drawString(heroes.get(i).getExp() + "", 227,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))), graphics);
			}
			else if (view == VIEW_STATS)
			{
				StringUtils.drawString(heroes.get(i).getCurrentHP() + "", 92,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))), graphics);
				StringUtils.drawString(heroes.get(i).getCurrentMP() + "", 127,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))), graphics);
				StringUtils.drawString(heroes.get(i).getCurrentAttack() + "", 162,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))), graphics);
				StringUtils.drawString(heroes.get(i).getCurrentDefense() + "", 197,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))), graphics);
				StringUtils.drawString(heroes.get(i).getCurrentSpeed() + "", 232,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))), graphics);
				StringUtils.drawString(heroes.get(i).getCurrentMove() + "", 267,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))), graphics);
			}
			else
			{
				renderMenuItem(graphics, i);
			}
		}

		postRender(graphics);
	}

	private void drawHeroSpecifics(Graphics graphics) {
		// Draw hero stat box
		Panel.drawPanelBox(82,
				yOffsetTop + 20,
			218,
			115, graphics);
		graphics.setColor(Color.white);

		/****************************/
		/* Draw the portrait window	*/
		/****************************/
		if (selectedHero != null)
		{
			int x = (CommRPG.GAME_SCREEN_SIZE.width - 280) / 2;
			int y = (CommRPG.GAME_SCREEN_SIZE.height - 226) / 2 -1;
			if (selectedHeroPortrait != null)
				selectedHeroPortrait.render(x, y, graphics);
		}

		graphics.setColor(Color.white);
		StringUtils.drawString(selectedHero.getName() + " " + selectedHero.getCurrentProgression().getClassName() +
				" L" + selectedHero.getLevel(),
				88,
				yOffsetTop + 15, graphics);

		StringUtils.drawString("SPELLS", 90,
				yOffsetTop + 32, graphics);

		StringUtils.drawString("ITEMS", 200,
				yOffsetTop + 32, graphics);

		// Draw Hero Spells
		if (selectedHero.getSpellsDescriptors() != null && selectedHero.getSpellsDescriptors().size() > 0)
			for (int i = 0; i < selectedHero.getSpellsDescriptors().size(); i++)
			{
				StringUtils.drawString(selectedHero.getSpellsDescriptors().get(i).getSpell().getName(),
						100,
					yOffsetTop + (42 + i * 20), graphics);
				StringUtils.drawString("Level 1", 115,
						yOffsetTop + (52 + i * 20), graphics);
			}
		else
		{
			graphics.setColor(COLOR_NONE);
			StringUtils.drawString("NONE", 100,
				yOffsetTop + 42, graphics);
			graphics.setColor(Color.white);
		}

		// Draw hero items
		if (items != null && selectedHero.getItemsSize() > 0)
			for (int i = 0; i < selectedHero.getItemsSize(); i++)
			{
				if (selectedHero.getEquipped().get(i))
				{
					graphics.setColor(Color.yellow);
					StringUtils.drawString("EQ", 190,
							yOffsetTop + (42 + i * 20), graphics);
					graphics.setColor(Color.red);
					if (selectedHero.getItem(i).getDurability() == ItemDurability.DAMAGED)
						StringUtils.drawString("DM", 190,
							yOffsetTop + (52 + i * 20), graphics);
					else if (selectedHero.getItem(i).getDurability() == ItemDurability.BROKEN)
						StringUtils.drawString("BR", 190,
								yOffsetTop + (52 + i * 20), graphics);
					graphics.setColor(Color.white);
				}

				StringUtils.drawString(items[i][0], 210,
					yOffsetTop + (42 + i * 20), graphics);
				if (items[i].length > 1)
					StringUtils.drawString(items[i][1], 225,
							yOffsetTop + (52 + i * 20), graphics);
			}
		else
		{
			graphics.setColor(COLOR_NONE);
			StringUtils.drawString("NONE", 210,
				yOffsetTop + 42, graphics);
			graphics.setColor(Color.white);
		}
	}

	protected void postRender(Graphics g)
	{

	}

	protected void renderMenuItem(Graphics graphics, int index)
	{

	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo)
	{
		if (input.isKeyDown(KeyMapping.BUTTON_UP))
		{
			return onUp(stateInfo);
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_DOWN))
		{
			return onDown(stateInfo);
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_LEFT))
		{
			return onLeft(stateInfo);
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT))
		{
			return onRight(stateInfo);
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_2))
		{
			return onBack(stateInfo);
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_1) || input.isKeyDown(KeyMapping.BUTTON_3))
		{
			return onConfirm(stateInfo);
		}

		return MenuUpdate.MENU_NO_ACTION;
	}

	protected MenuUpdate onLeft(StateInfo stateInfo)
	{
		if (view > 0)
			view--;
		else
			view = 1;
		return MenuUpdate.MENU_ACTION_LONG;
	}

	protected MenuUpdate onRight (StateInfo stateInfo)
	{
		if (view == 1)
			view = 0;
		else
			view++;
		return MenuUpdate.MENU_ACTION_LONG;
	}

	protected MenuUpdate onBack(StateInfo stateInfo)
	{
		selectedHero = null;
		return MenuUpdate.MENU_CLOSE;
	}

	protected MenuUpdate onConfirm(StateInfo stateInfo)
	{
		stateInfo.sendMessage(new SpriteContextMessage(MessageType.SHOW_HERO, selectedHero));
		return MenuUpdate.MENU_ACTION_LONG;
	}

	protected MenuUpdate onUp(StateInfo stateInfo)
	{
		if (selectedIndex > 0)
		{
			selectedIndex--;
			updateCurrentHero();
			return MenuUpdate.MENU_ACTION_LONG;
		}
		return MenuUpdate.MENU_NO_ACTION;
	}

	protected MenuUpdate onDown(StateInfo stateInfo)
	{
		if (selectedIndex < heroes.size() - 1)
		{
			selectedIndex++;
			updateCurrentHero();
			return MenuUpdate.MENU_ACTION_LONG;
		}
		return MenuUpdate.MENU_NO_ACTION;
	}

	private void updateCurrentHero()
	{
		selectedHero = heroes.get(selectedIndex);
		selectedHeroPortrait = Portrait.getPortrait(selectedHero);
		items = new String[selectedHero.getItemsSize()][];
		for (int i = 0; i < selectedHero.getItemsSize(); i++)
			items[i] = selectedHero.getItem(i).getName().split(" ");
	}

	@Override
	public Object getExitValue() {
		return null;
	}

	@Override
	public boolean displayWhenNotTop() {
		return false;
	}
}
