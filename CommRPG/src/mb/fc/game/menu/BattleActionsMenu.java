package mb.fc.game.menu;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.move.AttackableSpace;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class BattleActionsMenu extends Menu
{
	private Image[] icons;
	private int selected = -1;
	private static final Color disabledColor = new Color(111, 111, 111);
	private boolean[] enabled;

	public BattleActionsMenu(StateInfo stateInfo) {
		super(Panel.PANEL_BATTLE);
		icons = new Image[8];
		
		for (int i = 0; i < icons.length; i++)
			icons[i] = stateInfo.getResourceManager().getSpriteSheets().get("actionicons").getSubImage(i % 4, i / 4);
		
		enabled = new boolean[] {true, true, true};
	}
	
	public void initialize(StateInfo stateInfo)
	{
		CombatSprite currentSprite = stateInfo.getCurrentSprite();
		selected = 7;
		if (currentSprite.getSpellsDescriptors() != null && stateInfo.getCurrentSprite().getSpellsDescriptors().size() > 0)
			enabled[1] = true;
		else
			enabled[1] = false;
		
		if (currentSprite.getItemsSize() > 0)
			enabled[2] = true;
		else
			enabled[2] = false;
		
		/**************************************************/
		/* Determine if there are enemies in attack range */
		/**************************************************/
		int declaredRange = currentSprite.getAttackRange();
		int range[][] = null;
		
		switch (declaredRange)
		{
			case 1:
				range = AttackableSpace.RANGE_1;
				break;
			case 2:
				range = AttackableSpace.RANGE_2;
				break;
			case 3:
				range = AttackableSpace.RANGE_3;
				break;
			case EquippableItem.RANGE_BOW_2_NO_1:
				range = AttackableSpace.RANGE_2_1;
				break;
		}
		
		int rangeOffset = (range.length - 1) / 2;
		
		enabled[0] = false;
		
		OUTER: for (int i = 0; i < range.length; i++)
		{
			for (int j = 0; j < range[0].length; j++)
			{
				if (range[i][j] == 1)
				{
					CombatSprite targetable = stateInfo.getCombatSpriteAtTile(currentSprite.getTileX() - rangeOffset + i, 
							currentSprite.getTileY() - rangeOffset + j, false);
					if (targetable != null)
					{
						enabled[0] = true;
						selected = 4;
						break OUTER;
					}
				}
			}
		}
	}

	
	@Override
	public void render(FCGameContainer gc, Graphics graphics) {
		int iconWidth = icons[0].getWidth();
		int iconHeight = icons[0].getHeight();
		
		int x = (gc.getWidth() - iconWidth) / 2;
		int y = gc.getHeight() - iconHeight * 2 - 25;				
		
		Panel.drawPanelBox(396, 383, 115, 50, graphics);
		graphics.setColor(COLOR_FOREFRONT);
		if (selected == 4)
			graphics.drawString("ATTACK", 410, 381);
		else if (selected == 5)
			graphics.drawString("MAGIC", 410, 381);
		else if (selected == 6)
			graphics.drawString("ITEMS", 410, 381);
		else if (selected == 7)
			graphics.drawString("STAY", 410, 381);		
		
		if (enabled[0])
			graphics.drawImage(icons[(selected == 4 ? 4 : 0)], x, y);
		else
			graphics.drawImage(icons[(selected == 4 ? 4 : 0)], x, y, disabledColor);
		
		if (enabled[1])
			graphics.drawImage(icons[(selected == 5 ? 5 : 1)], x - iconWidth, (float) (y + iconHeight * .5));
		else
			graphics.drawImage(icons[(selected == 5 ? 5 : 1)], x - iconWidth, (float) (y + iconHeight * .5), disabledColor);
		
		if (enabled[2])
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
			stateInfo.sendMessage(Message.MESSAGE_SHOW_MOVEABLE);
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuback", 1f, false));
			return MenuUpdate.MENU_CLOSE;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_3))
		{			
			switch (selected)
			{
				case 4:
					stateInfo.sendMessage(Message.MESSAGE_ATTACK_PRESSED);
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "targetselect", 1f, false));
					return MenuUpdate.MENU_CLOSE;
				case 5:
					stateInfo.sendMessage(Message.MESSAGE_SHOW_SPELLMENU);
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
					return MenuUpdate.MENU_CLOSE;
				case 6:
					stateInfo.sendMessage(Message.MESSAGE_SHOW_ITEM_MENU);
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
					return MenuUpdate.MENU_CLOSE;
				case 7:
					stateInfo.sendMessage(Message.MESSAGE_PLAYER_END_TURN);
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
					return MenuUpdate.MENU_CLOSE;
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_UP))
		{
			if (enabled[0])
			{
				if (selected != 4)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
				selected = 4;				
				return MenuUpdate.MENU_ACTION_SHORT;
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_DOWN))
		{
			if (selected != 7)
				stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
			selected = 7;
			return MenuUpdate.MENU_ACTION_SHORT;
		}		
		else if (input.isKeyDown(KeyMapping.BUTTON_LEFT))
		{
			if (enabled[1])
			{
				if (selected != 5)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
				selected = 5;				
				return MenuUpdate.MENU_ACTION_SHORT;
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT))
		{
			if (enabled[2])
			{
				if (selected != 6)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
				selected = 6;				
				return MenuUpdate.MENU_ACTION_SHORT;
			}
		}
		return MenuUpdate.MENU_NO_ACTION;
	}
}
