package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.listener.MenuListener;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.PaddedGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class HeroesStatMenu extends Menu implements MenuListener
{
	protected static final int VIEW_LEVEL = 0;
	protected static final int VIEW_STATS = 1;
	protected static final int VIEW_DIFFS = 2;
	protected static final Color COLOR_NONE = new Color(204, 0, 70);

	protected int xOffset = 0, yOffsetTop = -40, yOffsetBot = 10;
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
		this.selectedHeroPortrait.update(delta);
		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		// Draw hero stat box
		Panel.drawPanelBox(xOffset + 82,
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
			selectedHeroPortrait.render(x, y, graphics);
		}

		graphics.setColor(Color.white);
		graphics.drawString(selectedHero.getName() + " " + selectedHero.getCurrentProgression().getClassName() +
				" L" + selectedHero.getLevel(),
				xOffset + 88,
				yOffsetTop + 15);

		graphics.drawString("SPELLS", xOffset + 90,
				yOffsetTop + 32);

		graphics.drawString("ITEMS", xOffset + 200,
				yOffsetTop + 32);

		// Draw Hero Spells
		if (selectedHero.getSpellsDescriptors() != null && selectedHero.getSpellsDescriptors().size() > 0)
			for (int i = 0; i < selectedHero.getSpellsDescriptors().size(); i++)
			{
				graphics.drawString(selectedHero.getSpellsDescriptors().get(i).getSpell().getName(),
						xOffset + 100,
					yOffsetTop + (42 + i * 20));
				graphics.drawString("Level 1", xOffset + 115,
						yOffsetTop + (52 + i * 20));
			}
		else
		{
			graphics.setColor(COLOR_NONE);
			graphics.drawString("NONE", xOffset + 100,
				yOffsetTop + 42);
			graphics.setColor(Color.white);
		}

		// Draw hero items
		if (items != null && selectedHero.getItemsSize() > 0)
			for (int i = 0; i < selectedHero.getItemsSize(); i++)
			{
				if (selectedHero.getEquipped().get(i))
				{
					graphics.setColor(Color.yellow);
					graphics.drawString("EQ", xOffset + 190,
							yOffsetTop + (42 + i * 20));
					graphics.setColor(Color.white);
				}

				graphics.drawString(items[i][0], xOffset + 210,
					yOffsetTop + (42 + i * 20));
				if (items[i].length > 1)
					graphics.drawString(items[i][1], xOffset + 225,
							yOffsetTop + (52 + i * 20));
			}
		else
		{
			graphics.setColor(COLOR_NONE);
			graphics.drawString("NONE", xOffset + 210,
				yOffsetTop + 42);
			graphics.setColor(Color.white);
		}

		// TODO PORTRAITS
		/*
		if (selectedHero.getPortraitImage() != null)
		{
			Panel.drawPanelBox(xOffset + 20,
					yOffsetTop + 20,
					CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 62,
					CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 78, graphics, Color.black);
			graphics.drawImage(selectedHero.getPortraitImage(),
					xOffset + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 27,
					yOffsetTop + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 27);

		}
		*/

		Panel.drawPanelBox(xOffset + 20,
				yOffsetBot + 118,
				CommRPG.GAME_SCREEN_SIZE.width - 40,
			108, graphics);


		graphics.setColor(Color.white);
		graphics.drawRect(xOffset + 25,
				yOffsetBot + (134 + 15 * Math.min(selectedIndex, 11)),
				269, 15);
		graphics.drawString("NAME", xOffset + 27,
				yOffsetBot + 113);

		if (view == VIEW_LEVEL)
		{
			graphics.drawString("LEVEL", xOffset + 127,
					yOffsetBot + 113);
			graphics.drawString("EXP", xOffset + 227,
					yOffsetBot + 113);
		}
		else if (view == VIEW_STATS)
		{
			graphics.drawString("HP", xOffset + 92,
					yOffsetBot + 113);
			graphics.drawString("MP", xOffset + 127,
					yOffsetBot + 113);
			graphics.drawString("ATK", xOffset + 162,
					yOffsetBot + 113);
			graphics.drawString("DEF", xOffset + 197,
					yOffsetBot + 113);
			graphics.drawString("SPD", xOffset + 232,
					yOffsetBot + 113);
			graphics.drawString("MOV", xOffset + 267,
					yOffsetBot + 113);
		}

		for (int i = (selectedIndex < 12 ? 0 : selectedIndex - 11); i < Math.min(heroes.size(),  (selectedIndex < 12 ? 12 : selectedIndex + 1)); i++)
		{
			graphics.drawString(heroes.get(i).getName(),
					xOffset + 27,
					yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))));

			if (view == VIEW_LEVEL)
			{
				graphics.drawString(heroes.get(i).getLevel() + "", xOffset + 127,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))));
				graphics.drawString(heroes.get(i).getExp() + "", xOffset + 227,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))));
			}
			else if (view == VIEW_STATS)
			{
				graphics.drawString(heroes.get(i).getCurrentHP() + "", xOffset + 92,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))));
				graphics.drawString(heroes.get(i).getCurrentMP() + "", xOffset + 127,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))));
				graphics.drawString(heroes.get(i).getCurrentAttack() + "", xOffset + 162,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))));
				graphics.drawString(heroes.get(i).getCurrentDefense() + "", xOffset + 197,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))));
				graphics.drawString(heroes.get(i).getCurrentSpeed() + "", xOffset + 232,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))));
				graphics.drawString(heroes.get(i).getCurrentMove() + "", xOffset + 267,
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))));
			}
			else
			{
				renderMenuItem(graphics, i);
			}
		}

		postRender(graphics);
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
	public boolean valueSelected(StateInfo stateInfo, Object value) {
		return false;
	}

	@Override
	public void menuClosed() {

	}

	@Override
	public boolean displayWhenNotTop() {
		return false;
	}


}
