package mb.fc.game.manager;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.game.menu.HeroStatMenu;
import mb.fc.game.menu.HeroesStatMenu;
import mb.fc.game.menu.PriestMenu;
import mb.fc.game.menu.ShopMenu;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.menu.SystemMenu;
import mb.fc.game.sprite.CombatSprite;

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
	
	public void update()
	{
		if (stateInfo.areMenusDisplayed())
		{
			if (System.currentTimeMillis() > stateInfo.getInputDelay())
				switch (stateInfo.getTopMenu().handleUserInput(stateInfo.getInput(), stateInfo))
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
					case MENU_NO_ACTION:
						break;
				}
		}
	}

	@Override
	public void recieveMessage(Message message) 
	{
		switch (message.getMessageType())
		{
			case Message.MESSAGE_SPEECH:				
				stateInfo.addMenu(new SpeechMenu(((SpeechMessage) message).getText(), stateInfo.getGc(), ((SpeechMessage) message).getTriggerId()));
				break;
			case Message.MESSAGE_SHOW_SYSTEM_MENU:
				stateInfo.addSingleInstanceMenu(new SystemMenu(stateInfo.getGc()));
				break;
			case Message.MESSAGE_SHOW_SHOP:
				ShopMessage sm = (ShopMessage) message;
				stateInfo.addMenu(new ShopMenu(stateInfo.getGc(), stateInfo, sm.getSellPercent(), sm.getBuyPercent(), sm.getItemIds()));
				break;
			case Message.MESSAGE_SHOW_HEROES:
				stateInfo.addSingleInstanceMenu(new HeroesStatMenu(stateInfo.getGc(), stateInfo.getClientProfile().getHeroes()));
				break;
			case Message.MESSAGE_SHOW_HERO:
				stateInfo.addMenu(new HeroStatMenu(stateInfo.getGc(), (CombatSprite) ((SpriteContextMessage) message).getSprite(), stateInfo));
				break;
			case Message.MESSAGE_SHOW_PRIEST:
				stateInfo.addMenu(new PriestMenu(stateInfo, stateInfo.getGc(), stateInfo.getClientProfile().getHeroes()));
				break;
		}
	}
}
