package mb.fc.game.manager;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.game.menu.DebugMenu;
import mb.fc.game.menu.HeroStatMenu;
import mb.fc.game.menu.HeroesStatMenu;
import mb.fc.game.menu.Menu;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.menu.PriestMenu2;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.menu.SystemMenu;
import mb.fc.game.menu.shop.HeroesSellMenu;
import mb.fc.game.menu.shop.ShopMenuTabled;
import mb.fc.game.menu.shop.ShopOptionsMenu;

public class MenuManager extends Manager
{
	@Override
	public void initialize()
	{
		// stateInfo.addMenu(new ShopMenuTabled(stateInfo, .8, 1.2, new int[] {0, 1, 2}));
	}

	public boolean isBlocking()
	{
		return stateInfo.areMenusDisplayed();
	}

	public void update(long delta)
	{
		if (stateInfo.areMenusDisplayed())
			handleMenuUpdate(stateInfo.getTopMenu().update(delta, stateInfo));

		if (stateInfo.areMenusDisplayed() && System.currentTimeMillis() > stateInfo.getInputDelay())
			handleMenuUpdate(stateInfo.getTopMenu().handleUserInput(stateInfo.getInput(), stateInfo));
	}

	private void handleMenuUpdate(MenuUpdate menuUpdate)
	{
		switch (menuUpdate)
		{
			case MENU_CLOSE:
				Menu menu = stateInfo.getTopMenu();
				if (menu instanceof SpeechMenu)
				{
					stateInfo.setWaiting();
					stateInfo.sendMessage(MessageType.WAIT);
				}
				stateInfo.removeTopMenu();
				if (menu.getMenuListener() != null)
				{
					menu.getMenuListener().valueSelected(stateInfo, menu.getExitValue());
					menu.getMenuListener().menuClosed();
				}

				// stateInfo.addMenu(new ShopMenuTabled(stateInfo, .8, 1.2, new int[] {1, 1, 2, 2, 0, 0, 1, 1, 2, 2, 0, 0}));
				// stateInfo.addSingleInstanceMenu(new HeroesStatMenu(stateInfo, true, 1));
				stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				break;
			case MENU_ACTION_SHORT:
				stateInfo.setInputDelay(System.currentTimeMillis() + 75);
				break;
			case MENU_ACTION_LONG:
				stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				break;
			default:
				break;
		}
	}

	@Override
	public void recieveMessage(Message message)
	{
		switch (message.getMessageType())
		{
			case SPEECH:
				SpeechMessage spm = (SpeechMessage) message;
				stateInfo.addMenu(new SpeechMenu(spm.getText(),
						stateInfo.getGc(), spm.getTriggerId(), spm.getPortrait(), stateInfo));
				break;
			case SHOW_SYSTEM_MENU:
				stateInfo.addSingleInstanceMenu(new SystemMenu(stateInfo.getGc()));
				break;
			case SHOW_SHOP:
				ShopMessage sm = (ShopMessage) message;
				// stateInfo.addSingleInstanceMenu(new ShopMenuTabled(stateInfo, sm.getSellPercent(), sm.getBuyPercent(), sm.getItemIds()));
				stateInfo.addSingleInstanceMenu(new ShopOptionsMenu(sm, stateInfo));
				break;
			case SHOW_SHOP_BUY:
				sm = (ShopMessage) message;
				stateInfo.addSingleInstanceMenu(new ShopMenuTabled(stateInfo, sm));
				break;
			case SHOW_SHOP_SELL:
				stateInfo.addSingleInstanceMenu(new HeroesSellMenu(stateInfo, null));
				break;
			case SHOW_HEROES:
				stateInfo.addSingleInstanceMenu(new HeroesStatMenu(stateInfo));
				break;
			case SHOW_HERO:
				stateInfo.addMenu(new HeroStatMenu(stateInfo.getGc(), ((SpriteContextMessage) message).getCombatSprite(
						stateInfo.getPsi().getClientProfile().getHeroes()), stateInfo));
				break;
			case SHOW_PRIEST:
				stateInfo.addMenu(new PriestMenu2(stateInfo, stateInfo.getGc(), stateInfo.getClientProfile().getHeroes()));
				break;
			case SHOW_DEBUG:
				stateInfo.addMenu(new DebugMenu(stateInfo.getGc()));
				break;
			default:
				break;
		}
	}
}
