package mb.fc.game.manager;

import java.util.ArrayList;

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
import mb.fc.game.menu.MultiHeroJoinMenu;
import mb.fc.game.menu.PriestMenu2;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.menu.SystemMenu;
import mb.fc.game.menu.shop.HeroesSellMenu;
import mb.fc.game.menu.shop.ShopMenuTabled;
import mb.fc.game.menu.shop.ShopOptionsMenu;
import mb.fc.game.resource.HeroResource;
import mb.fc.game.sprite.CombatSprite;

public class MenuManager extends Manager
{
	private static DebugMenu DEBUG_MENU;
	
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
		else {
			System.out.println("WAITING");
			stateInfo.getInput().clear();
		}
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
			case MENU_NEXT_ACTION:
				stateInfo.sendMessage(MessageType.CIN_NEXT_ACTION);
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
						stateInfo.getFCGameContainer(), spm.getTriggerId(), spm.getPortrait(), stateInfo));
				break;
			case SHOW_SYSTEM_MENU:
				stateInfo.addSingleInstanceMenu(new SystemMenu(stateInfo.getFCGameContainer()));
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
				stateInfo.addMenu(new HeroStatMenu(stateInfo.getFCGameContainer(), ((SpriteContextMessage) message).getCombatSprite(
						stateInfo.getAllHeroes()), stateInfo));
				break;
			case SHOW_PRIEST:
				stateInfo.addMenu(new PriestMenu2(stateInfo, stateInfo.getFCGameContainer(), stateInfo.getAllHeroes()));
				break;
			case SHOW_PANEL_MULTI_JOIN_CHOOSE:
				ArrayList<CombatSprite> heroesToChooseList = new ArrayList<>();
				((SpriteContextMessage) message).getSpriteIds().forEach(id -> heroesToChooseList.add(HeroResource.getHero(id)));
				heroesToChooseList.forEach(cs -> { cs.initializeSprite(stateInfo); cs.initializeStats(); });
				stateInfo.addMenu(new MultiHeroJoinMenu(heroesToChooseList, stateInfo));
				break;
			case SHOW_DEBUG:
				if (DEBUG_MENU == null)
					DEBUG_MENU = new DebugMenu(stateInfo.getFCGameContainer());
				DEBUG_MENU.clearText();
				stateInfo.addMenu(DEBUG_MENU);
				break;
			default:
				break;
		}
	}
}
