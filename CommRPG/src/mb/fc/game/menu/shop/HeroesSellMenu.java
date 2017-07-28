package mb.fc.game.menu.shop;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.listener.MenuListener;
import mb.fc.game.menu.HeroesStatMenu;

public class HeroesSellMenu extends HeroesStatMenu
{
	protected boolean selectingItemState = false;
	protected int selectingItemIndex = 0;

	public HeroesSellMenu(StateInfo stateInfo, MenuListener listener) {
		super(stateInfo, listener);
	}

	@Override
	protected void postRender(Graphics g) {
		if (selectingItemState)
		{
			g.setColor(Color.white);
			//graphics.drawString("EQ", xOffset + 190,
			// yOffsetTop + (42 + i * 20));
			g.drawRect(xOffset + 207,
					yOffsetTop + 28 + (42 + selectingItemIndex * 20), 200, 60);
		}
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
			System.out.println("STOP SELECTING ITEM STATE");
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
		// TODO CHECK IF HERO HAS ITEMS
		// Show the item selection cursor
		if (!selectingItemState)
		{
			System.out.println("SELECTING ITEM STATE");
			selectingItemState = true;
			selectingItemIndex = 0;
		}
		// Otherwise we are done, set the selected item and exit
		else
		{

		}

		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public Object getExitValue() {
		if (selectedHero != null)
			return new Object[] {selectedHero, selectingItemIndex};
		else
			return null;
	}


}
