package mb.fc.game.menu;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.item.Item;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ItemOptionMenu extends Menu
{
	private Image[] icons;
	private int selected = -1;
	private Item item;
	private int itemIndex;
	
	private static final Color disabledColor = new Color(111, 111, 111);

	public ItemOptionMenu(StateInfo stateInfo) {
		super(Panel.PANEL_ITEM_OPTIONS);
		icons = new Image[8];
		
		for (int i = 0; i < icons.length; i++)
			icons[i] = stateInfo.getResourceManager().getSpriteSheets().get("actionicons").getSubImage(i % 4 + 7, i / 4);
		
	}
	
	public void initialize(int itemIndex, StateInfo stateInfo)
	{
		this.itemIndex = itemIndex;
		this.item = stateInfo.getCurrentSprite().getItem(itemIndex);
		
		if (item.isUsuable())
			selected = 4;
		else if (item.isEquippable())
			selected = 6;
		else
			selected = 5;
	}
	
	@Override
	public void render(FCGameContainer gc, Graphics graphics) {
		int iconWidth = icons[0].getWidth();
		int iconHeight = icons[0].getHeight();
		
		int x = (gc.getWidth() - iconWidth) / 2;
		int y = gc.getHeight() - iconHeight * 2 - 25;				
		
		Panel.drawPanelBox(gc.getDisplayPaddingX() + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 198, 
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 202 - 17, 
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 57,
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 20 + 5, graphics);
		graphics.setColor(COLOR_FOREFRONT);
		int sX = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 205 + gc.getDisplayPaddingX();
		int sY = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 198 - 14;
		if (selected == 4)
			graphics.drawString("USE", sX, sY);
		else if (selected == 5)
			graphics.drawString("GIVE", sX, sY);
		else if (selected == 6)
			graphics.drawString("EQUIP", sX, sY);
		else if (selected == 7)
			graphics.drawString("DROP", sX, sY);
		
		if (item.isUsuable())
			graphics.drawImage(icons[(selected == 4 ? 4 : 0)], x, y);
		else
			graphics.drawImage(icons[(selected == 4 ? 4 : 0)], x, y, disabledColor);
		
		graphics.drawImage(icons[(selected == 5 ? 5 : 1)], x - iconWidth, (float) (y + iconHeight * .5));
		
		if (item.isEquippable())
			graphics.drawImage(icons[(selected == 6 ? 6 : 2)], x + iconWidth, (float) (y + iconHeight * .5));
		else
			graphics.drawImage(icons[(selected == 6 ? 6 : 2)], x + iconWidth, (float) (y + iconHeight * .5), disabledColor);
		
		graphics.drawImage(icons[(selected == 7 ? 7 : 3)], x, y + iconHeight);
	}	

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) 
	{
		if (input.isKeyDown(KeyMapping.BUTTON_1))
		{
			
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_2))
		{
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuback", 1f, false));
			stateInfo.sendMessage(Message.MESSAGE_SHOW_ITEM_MENU);
			return MenuUpdate.MENU_CLOSE;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_3))
		{
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
			switch (selected)
			{
				case 4:
					stateInfo.sendMessage(new BattleSelectionMessage(Message.MESSAGE_SELECT_ITEM, itemIndex));
					return MenuUpdate.MENU_CLOSE;
				case 5:
					// stateInfo.sendMessage(Message.MESSAGE_SHOW_SPELLMENU);
					return MenuUpdate.MENU_CLOSE;
				case 6:
					stateInfo.getCurrentSprite().equipItem((EquippableItem) item);					
					return MenuUpdate.MENU_CLOSE;
				case 7:
					stateInfo.getCurrentSprite().removeItem(item);
					return MenuUpdate.MENU_CLOSE;
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_UP))
		{
			if (item.isUsuable())
			{
				if (selected != 4)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
				selected = 4;				
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_DOWN))
		{
			if (selected != 7)
				stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
			selected = 7;
		}		
		else if (input.isKeyDown(KeyMapping.BUTTON_LEFT))
		{
			if (selected != 5)
				stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
			selected = 5;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT))
		{
			if (item.isEquippable())
			{
				if (selected != 6)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
				selected = 6;				
			}
		}
		return MenuUpdate.MENU_NO_ACTION;
	}
}
