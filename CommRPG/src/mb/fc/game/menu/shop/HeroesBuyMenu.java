package mb.fc.game.menu.shop;

import java.util.ArrayList;

import org.newdawn.slick.Graphics;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.item.Item.EquippableDifference;
import mb.fc.game.listener.MenuListener;
import mb.fc.game.menu.HeroesStatMenu;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.utils.StringUtils;

public class HeroesBuyMenu extends HeroesStatMenu
{
	protected Item selectedShopItem;
	protected ArrayList<String> differences = new ArrayList<>();

	public HeroesBuyMenu(StateInfo stateInfo, MenuListener listener, Item item) {
		super(stateInfo, listener);
		selectedShopItem = item;
		if (selectedShopItem.isEquippable())
		{
			view = VIEW_DIFFS;
			determineDifferences(stateInfo);
		}
	}

	public void determineDifferences(StateInfo stateInfo)
	{
		differences.clear();
		if (selectedShopItem.isEquippable())
		{
			int type = ((EquippableItem) selectedShopItem).getItemType();

			for (CombatSprite hero : stateInfo.getAllHeroes())
			{
				EquippableDifference ed = null;
				if (hero.isEquippable((EquippableItem) selectedShopItem))
				{
					if (type == EquippableItem.TYPE_WEAPON)
						ed = Item.getEquippableDifference(hero.getEquippedWeapon(), (EquippableItem) selectedShopItem);
					else if (type == EquippableItem.TYPE_ARMOR)
						ed = Item.getEquippableDifference(hero.getEquippedArmor(), (EquippableItem) selectedShopItem);
					else if (type == EquippableItem.TYPE_RING)
						ed = Item.getEquippableDifference(hero.getEquippedRing(), (EquippableItem) selectedShopItem);
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
	protected void renderMenuItem(Graphics graphics, int index)
	{
		if (view == VIEW_DIFFS)
		{
			StringUtils.drawString(differences.get(index), 92,
					yOffsetBot + (128 + 15 * (index - (selectedIndex < 12 ? 0 : selectedIndex - 11))), graphics);
		}
	}


	@Override
	protected MenuUpdate onLeft(StateInfo stateInfo) {
		if (view > 0)
			view--;
		else if (selectedShopItem.isEquippable())
			view = 2;
		else
			return super.onLeft(stateInfo);
		return MenuUpdate.MENU_ACTION_LONG;
	}

	@Override
	protected MenuUpdate onRight(StateInfo stateInfo) {
		if ((view == 2 && selectedShopItem.isEquippable())
				|| (view == 1 && !selectedShopItem.isEquippable()))
			view = 0;
		else
			view++;
		return MenuUpdate.MENU_ACTION_LONG;
	}

	@Override
	protected MenuUpdate onConfirm(StateInfo stateInfo) {
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public Object getExitValue() {
		return selectedHero;
	}
}
