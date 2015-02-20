package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.item.Item.EquippableDifference;
import mb.fc.game.resource.ItemResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;

public class HeroesStatMenu extends Menu
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
	protected boolean showDiffs = false;
	protected ArrayList<String> differences = new ArrayList<>();
	protected Item selectedItem;

	public HeroesStatMenu(StateInfo stateInfo)
	{
		this(stateInfo, false, -1);
	}

	public HeroesStatMenu(StateInfo stateInfo, boolean showDiffs, int itemId)
	{
		super(Panel.PANEL_HEROS_OVERVIEW);
		heroes = new ArrayList<>(stateInfo.getPsi().getClientProfile().getHeroes());
		updateCurrentHero();

		this.showDiffs = showDiffs;

		if (showDiffs)
		{
			view = VIEW_DIFFS;
			selectedItem = ItemResource.getItem(itemId, stateInfo);
			determineDifferences(stateInfo);
		}
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) {
		// Draw hero stat box
		Panel.drawPanelBox(xOffset + 82 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				yOffsetTop + 20 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
			218 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
			115 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], graphics);
		graphics.setColor(Color.white);


		graphics.drawString(selectedHero.getName() + " " + selectedHero.getCurrentProgression().getClassName() +
				" L" + selectedHero.getLevel(),
				xOffset + 88 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				yOffsetTop + 15 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);

		graphics.drawString("SPELLS", xOffset + 90 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				yOffsetTop + 32 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);

		graphics.drawString("ITEMS", xOffset + 200 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				yOffsetTop + 32 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);

		// Draw hero items
		if (selectedHero.getSpellsDescriptors() != null && selectedHero.getSpellsDescriptors().size() > 0)
			for (int i = 0; i < selectedHero.getSpellsDescriptors().size(); i++)
			{
				graphics.drawString(selectedHero.getSpellsDescriptors().get(i).getSpell().getName(),
						xOffset + 100 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetTop + (42 + i * 20) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
				graphics.drawString("Level 1", xOffset + 115 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						yOffsetTop + (52 + i * 20) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			}
		else
		{
			graphics.setColor(COLOR_NONE);
			graphics.drawString("NONE", xOffset + 100 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				yOffsetTop + 42 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			graphics.setColor(Color.white);
		}

		if (items != null && selectedHero.getItemsSize() > 0)
			for (int i = 0; i < selectedHero.getItemsSize(); i++)
			{
				if (selectedHero.getEquipped().get(i))
				{
					graphics.setColor(Color.yellow);
					graphics.drawString("EQ", xOffset + 190 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
							yOffsetTop + (42 + i * 20) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
					graphics.setColor(Color.white);
				}

				graphics.drawString(items[i][0], xOffset + 210 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetTop + (42 + i * 20) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
				if (items[i].length > 1)
					graphics.drawString(items[i][1], xOffset + 225 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
							yOffsetTop + (52 + i * 20) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			}
		else
		{
			graphics.setColor(COLOR_NONE);
			graphics.drawString("NONE", xOffset + 210 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				yOffsetTop + 42 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			graphics.setColor(Color.white);
		}

		// TODO PORTRAITS
		/*
		if (selectedHero.getPortraitImage() != null)
		{
			Panel.drawPanelBox(xOffset + 20 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetTop + 20 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 62,
					CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 78, graphics, Color.black);
			graphics.drawImage(selectedHero.getPortraitImage(),
					xOffset + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 27,
					yOffsetTop + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 27);

		}
		*/

		Panel.drawPanelBox(xOffset + 20 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				yOffsetBot + 118 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
			gc.getWidth() - 40 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
			108 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], graphics);


		graphics.setColor(Color.white);
		graphics.drawRect(xOffset + 25 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				yOffsetBot + (134 + 15 * Math.min(selectedIndex, 11)) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				269 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 15);
		graphics.drawString("NAME", xOffset + 27 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				yOffsetBot + 113 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);

		if (view == VIEW_LEVEL)
		{
			graphics.drawString("LEVEL", xOffset + 127 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetBot + 113 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			graphics.drawString("EXP", xOffset + 227 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetBot + 113 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
		}
		else if (view == VIEW_STATS)
		{
			graphics.drawString("HP", xOffset + 92 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetBot + 113 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			graphics.drawString("MP", xOffset + 127 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetBot + 113 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			graphics.drawString("ATK", xOffset + 162 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetBot + 113 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			graphics.drawString("DEF", xOffset + 197 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetBot + 113 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			graphics.drawString("SPD", xOffset + 232 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetBot + 113 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			graphics.drawString("MOV", xOffset + 267 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetBot + 113 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
		}

		for (int i = (selectedIndex < 12 ? 0 : selectedIndex - 11); i < Math.min(heroes.size(),  (selectedIndex < 12 ? 12 : selectedIndex + 1)); i++)
		{
			graphics.drawString(heroes.get(i).getName(),
					xOffset + 27 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);

			if (view == VIEW_LEVEL)
			{
				graphics.drawString(heroes.get(i).getLevel() + "", xOffset + 127 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
				graphics.drawString(heroes.get(i).getExp() + "", xOffset + 227 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			}
			else if (view == VIEW_STATS)
			{
				graphics.drawString(heroes.get(i).getCurrentHP() + "", xOffset + 92 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
				graphics.drawString(heroes.get(i).getCurrentMP() + "", xOffset + 127 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
				graphics.drawString(heroes.get(i).getCurrentAttack() + "", xOffset + 162 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
				graphics.drawString(heroes.get(i).getCurrentDefense() + "", xOffset + 197 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
				graphics.drawString(heroes.get(i).getCurrentSpeed() + "", xOffset + 232 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
				graphics.drawString(heroes.get(i).getCurrentMove() + "", xOffset + 267 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			}
			else if (view == VIEW_DIFFS)
			{
				graphics.drawString(differences.get(i), xOffset + 92 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
						yOffsetBot + (128 + 15 * (i - (selectedIndex < 12 ? 0 : selectedIndex - 11))) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
			}
		}
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo)
	{
		if (input.isKeyDown(KeyMapping.BUTTON_UP))
		{
			if (selectedIndex > 0)
			{
				selectedIndex--;
				updateCurrentHero();
				return MenuUpdate.MENU_ACTION_LONG;
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_DOWN))
		{
			if (selectedIndex < heroes.size() - 1)
			{
				selectedIndex++;
				updateCurrentHero();
				return MenuUpdate.MENU_ACTION_LONG;
			}
		}
		else if (input.isKeyDown(Input.KEY_ENTER))
			return MenuUpdate.MENU_CLOSE;
		else if (input.isKeyDown(KeyMapping.BUTTON_LEFT))
		{
			if (view > 0)
				view--;
			else
			{
				if (showDiffs)
					view = 2;
				else
					view = 1;
			}
			return MenuUpdate.MENU_ACTION_LONG;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT))
		{
			if ((showDiffs && view == 2) ||
					(!showDiffs && view == 1))
				view = 0;
			else
				view++;
			return MenuUpdate.MENU_ACTION_LONG;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_2))
		{
			return MenuUpdate.MENU_CLOSE;
		}

		return MenuUpdate.MENU_NO_ACTION;
	}

	public void determineDifferences(StateInfo stateInfo)
	{
		differences.clear();
		if (selectedItem.isEquippable())
		{
			int type = ((EquippableItem) selectedItem).getItemType();

			for (CombatSprite hero : stateInfo.getPsi().getClientProfile().getHeroes())
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

	private void updateCurrentHero()
	{
		selectedHero = heroes.get(selectedIndex);
		items = new String[selectedHero.getItemsSize()][];
		for (int i = 0; i < selectedHero.getItemsSize(); i++)
			items[i] = selectedHero.getItem(i).getName().split(" ");
	}
}
