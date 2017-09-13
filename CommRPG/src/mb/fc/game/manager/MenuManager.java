package mb.fc.game.manager;

import java.util.ArrayList;

import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.ShopMessage;
import mb.fc.engine.message.SpeechBundleMessage;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.message.SpriteContextMessage;
import mb.fc.engine.message.StringMessage;
import mb.fc.game.menu.DebugMenu;
import mb.fc.game.menu.HeroStatMenu;
import mb.fc.game.menu.HeroesStatMenu;
import mb.fc.game.menu.Menu;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.menu.MultiHeroJoinMenu;
import mb.fc.game.menu.PriestMenu;
import mb.fc.game.menu.SpeechMenu;
import mb.fc.game.menu.SystemMenu;
import mb.fc.game.menu.YesNoMenu;
import mb.fc.game.menu.shop.ShopBuyMenu;
import mb.fc.game.menu.shop.ShopChooseItemMenu;
import mb.fc.game.menu.shop.ShopOptionsMenu;
import mb.fc.game.resource.HeroResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.text.Speech;
import mb.fc.game.text.YesNoSpeech;

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
		
		if (stateInfo.areMenusDisplayed()) {
			Menu topMenu = stateInfo.getTopMenu();
			handleMenuUpdate(topMenu.update(delta, stateInfo), topMenu);

			if (System.currentTimeMillis() > stateInfo.getInputDelay())
				handleMenuUpdate(topMenu.handleUserInput(stateInfo.getInput(), stateInfo), topMenu);
		}
	}

	private void handleMenuUpdate(MenuUpdate menuUpdate, Menu updatedMenu)
	{
		switch (menuUpdate)
		{
			case MENU_CLOSE:
				if (updatedMenu instanceof SpeechMenu)
				{
					stateInfo.setWaiting();
					stateInfo.sendMessage(MessageType.WAIT);
				}
				stateInfo.removeMenu(updatedMenu);
				if (updatedMenu.getMenuListener() != null)
				{
					updatedMenu.getMenuListener().valueSelected(stateInfo, updatedMenu.getExitValue());
					updatedMenu.getMenuListener().menuClosed();
				}

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
				if (message instanceof SpeechMessage) {
					SpeechMessage spm = (SpeechMessage) message;
					stateInfo.addMenu(new SpeechMenu(spm.getText(), spm.getTriggerId(), spm.getPortrait(), stateInfo));
				} else if (message instanceof SpeechBundleMessage) {
					
					SpeechBundleMessage sbm = (SpeechBundleMessage) message;
					Speech speech = stateInfo.getResourceManager().getSpeechesById(sbm.getSpeechId()).get(sbm.getSpeechIndex());
					speech.initialize();
					if (!(speech instanceof YesNoSpeech))
						stateInfo.addMenu(new SpeechMenu(speech, stateInfo));
					else {
						YesNoSpeech yns = (YesNoSpeech) speech;
						stateInfo.addMenu(new YesNoMenu(speech.getMessage(), yns.getYesTrigger(), yns.getNoTrigger(), stateInfo));
					}
				}
				break;
			case SHOW_SYSTEM_MENU:
				stateInfo.addSingleInstanceMenu(new SystemMenu(stateInfo.getFCGameContainer()));
				break;
			case SHOW_SHOP:
				ShopMessage sm = (ShopMessage) message;
				// stateInfo.addSingleInstanceMenu(new ShopMenuTabled(stateInfo, sm.getSellPercent(), sm.getBuyPercent(), sm.getItemIds()));
				stateInfo.addSingleInstanceMenu(new ShopOptionsMenu(sm, stateInfo));
				break;
			case SHOW_SHOP_DEALS:
			case SHOW_SHOP_BUY:
				sm = (ShopMessage) message;
				stateInfo.addSingleInstanceMenu(new ShopBuyMenu(stateInfo, sm));
				break;
			case SHOW_SHOP_REPAIR:
			case SHOW_SHOP_SELL:
				sm = (ShopMessage) message;
				stateInfo.addSingleInstanceMenu(new ShopChooseItemMenu(stateInfo, null, sm));
				break;
			case SHOW_HEROES:
				stateInfo.addSingleInstanceMenu(new HeroesStatMenu(stateInfo));
				stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				break;
			case SHOW_HERO:
				stateInfo.addMenu(new HeroStatMenu(stateInfo.getFCGameContainer(), ((SpriteContextMessage) message).getCombatSprite(
						stateInfo.getAllHeroes()), stateInfo));
				break;
			case SHOW_PRIEST:
				StringMessage stringMessage = (StringMessage) message;
				stateInfo.addMenu(new PriestMenu(stringMessage.getString(), stateInfo));
				break;
			case SHOW_PANEL_MULTI_JOIN_CHOOSE:
				ArrayList<CombatSprite> heroesToChooseList = new ArrayList<>();
				((SpriteContextMessage) message).getSpriteIds().forEach(id -> heroesToChooseList.add(HeroResource.getHero(id)));
				heroesToChooseList.forEach(cs -> { cs.initializeSprite(stateInfo.getResourceManager()); cs.initializeStats(); });
				stateInfo.addMenu(new MultiHeroJoinMenu(heroesToChooseList, stateInfo));
				break;
			case SHOW_DEBUG:
				stateInfo.addMenu(new DebugMenu(stateInfo));
				break;
			default:
				break;
		}
	}
}
