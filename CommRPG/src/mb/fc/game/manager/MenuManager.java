package mb.fc.game.manager;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.game.menu.DebugMenu;
import mb.fc.game.menu.HeroStatMenu;
import mb.fc.game.menu.HeroesStatMenu;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.menu.PriestMenu;
import mb.fc.game.menu.ShopMenu;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.menu.SystemMenu;

public class MenuManager extends Manager
{
	@Override
	public void initialize()
	{

	}

	public boolean isBlocking()
	{
		return stateInfo.areMenusDisplayed();
	}

	public void update(long delta)
	{
		if (stateInfo.areMenusDisplayed() && stateInfo.areMenusDisplayed())
			handleMenuUpdate(stateInfo.getTopMenu().update(delta, stateInfo));

		if (stateInfo.areMenusDisplayed() && System.currentTimeMillis() > stateInfo.getInputDelay())
			handleMenuUpdate(stateInfo.getTopMenu().handleUserInput(stateInfo.getInput(), stateInfo));
	}

	private void handleMenuUpdate(MenuUpdate menuUpdate)
	{
		switch (menuUpdate)
		{
			case MENU_CLOSE:
				stateInfo.removeTopMenu();
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
						stateInfo.getGc(), spm.getTriggerId(), spm.getPortraitId(), stateInfo));
				break;
			case SHOW_SYSTEM_MENU:
				stateInfo.addSingleInstanceMenu(new SystemMenu(stateInfo.getGc()));
				break;
			case SHOW_SHOP:
				ShopMessage sm = (ShopMessage) message;
				stateInfo.addMenu(new ShopMenu(stateInfo.getGc(), stateInfo, sm.getSellPercent(), sm.getBuyPercent(), sm.getItemIds()));
				break;
			case SHOW_HEROES:
				stateInfo.addSingleInstanceMenu(new HeroesStatMenu(stateInfo.getGc(), stateInfo.getClientProfile().getHeroes()));
				break;
			case SHOW_HERO:
				stateInfo.addMenu(new HeroStatMenu(stateInfo.getGc(), ((SpriteContextMessage) message).getSprite(stateInfo.getSprites()), stateInfo));
				break;
			case SHOW_PRIEST:
				stateInfo.addMenu(new PriestMenu(stateInfo, stateInfo.getGc(), stateInfo.getClientProfile().getHeroes()));
				break;
			case SHOW_DEBUG:
				stateInfo.addMenu(new DebugMenu(stateInfo.getGc()));
				break;
			default:
				break;
		}
	}
}
