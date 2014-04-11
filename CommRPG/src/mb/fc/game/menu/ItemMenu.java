package mb.fc.game.menu;

import java.awt.Point;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ItemMenu extends Menu
{
	private CombatSprite currentSprite;
	private Point[] drawPoints; 
	private Image emptySpot;
	private int selected;
	private int width;
	private int height;
	
	public ItemMenu(StateInfo stateInfo) {
		super(Panel.PANEL_ITEM);
		emptySpot = stateInfo.getResourceManager().getSpriteSheets().get("items").getSprite(17, 1);
	}
	
	public void initialize(StateInfo stateInfo)
	{
		selected = 0;
		
		drawPoints = new Point[4];
		currentSprite = stateInfo.getCurrentSprite();
		
		width = currentSprite.getItem(0).getImage().getWidth();
		height = currentSprite.getItem(0).getImage().getHeight();
		
		int x = (stateInfo.getGc().getWidth() - width) / 2;
		int y = stateInfo.getGc().getHeight() - height * 2 - 25;
		
		drawPoints[0] = new Point(x, y);
		drawPoints[1] = new Point(x - width, y + height / 2);
		drawPoints[2] = new Point(x + width, y + height / 2);
		drawPoints[3] = new Point(x, y + height);
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		if (input.isKeyDown(KeyMapping.BUTTON_UP))
		{
			if (selected != 0)
				stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
			selected = 0;			
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_DOWN))
		{
			if (currentSprite.getItemsSize() > 3)
			{
				if (selected != 3)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
				selected = 3;				
			}
		}		
		else if (input.isKeyDown(KeyMapping.BUTTON_LEFT))
		{
			if (currentSprite.getItemsSize() > 1)
			{
				if (selected != 1)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
				selected = 1;				
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT))
		{
			if (currentSprite.getItemsSize() > 2)
			{
				if (selected != 2)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
				selected = 2;				
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_2))
		{
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuback", 1f, false));
			stateInfo.sendMessage(Message.MESSAGE_SHOW_BATTLEMENU);
			return MenuUpdate.MENU_CLOSE;
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_3))
		{	
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
			stateInfo.sendMessage(new IntMessage(Message.MESSAGE_SHOW_ITEM_OPTION_MENU, selected));			
			return MenuUpdate.MENU_CLOSE;
		}
		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) 
	{
		Panel.drawPanelBox(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 198 + gc.getDisplayPaddingX(), 
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 200 - 22, 
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 100, 
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 30, 
				graphics);
		
		for (int i = 0; i < 4; i++)
		{			
			if (currentSprite.getItemsSize() > i)
				graphics.drawImage(currentSprite.getItem(i).getImage(), drawPoints[i].x, drawPoints[i].y);
			else
				graphics.drawImage(emptySpot, drawPoints[i].x, drawPoints[i].y);
			
			if (selected == i)
			{
				graphics.setColor(Color.red);
				graphics.drawRect(drawPoints[i].x, drawPoints[i].y, width, height);
				graphics.drawRect(drawPoints[i].x + 1, drawPoints[i].y + 1, width - 2, height - 2);
				graphics.setColor(COLOR_FOREFRONT);
				graphics.drawString(currentSprite.getItem(i).getName(), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 205 + gc.getDisplayPaddingX(), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 195 - 22);
				
				if (currentSprite.getEquipped().get(i))
				{
					graphics.setColor(Color.pink);
					graphics.drawString("Equipped", CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 205 + gc.getDisplayPaddingX(), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 205 - 22);
				}
			}						
		}			
	}

}
