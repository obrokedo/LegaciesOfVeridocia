package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.BattleSelectionMessage;
import mb.fc.engine.message.ChatMessage;
import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
public class SpellMenu extends Menu
{
	// TODO PING COMMAND
	private ArrayList<SpellIcon> spellIcons;
	private CombatSprite currentSprite;
	private int selected = 0;
	private boolean hasSelected = false;
	private Image emptySpot;
	
	public SpellMenu(StateInfo stateInfo) 
	{
		super(Panel.PANEL_SPELL);
		spellIcons = new ArrayList<SpellIcon>();
		emptySpot = stateInfo.getResourceManager().getSpriteSheets().get("spellicons").getSubImage(15, 0);
	}
	
	public void initialize(StateInfo stateInfo)
	{				
		spellIcons.clear();
		currentSprite = stateInfo.getCurrentSprite();
		
		Image im = stateInfo.getResourceManager().getSpriteSheets().get("spellicons").getSubImage(
				stateInfo.getCurrentSprite().getSpellsDescriptors().get(0).getSpellId(), 0);
		int iconWidth = im.getWidth();
		int iconHeight = im.getHeight();
		
		int x = (stateInfo.getGc().getWidth() - iconWidth) / 2;
		int y = stateInfo.getGc().getHeight() - iconHeight * 2 - 25;
		
		for (int i = 0; i < 4; i++)
		{
			int iX = x;
			int iY = y;
			
			if (i == 1)
			{
				iX -= iconWidth;
				iY += iconHeight / 2;
			}
			else if (i == 2)
			{
				iX += iconWidth;
				iY += iconHeight / 2;
			}
			else if (i == 3)
				iY += iconHeight;
			
			if (currentSprite.getSpellsDescriptors() != null && stateInfo.getCurrentSprite().getSpellsDescriptors().size() > i)
			{			
				KnownSpell sd  = stateInfo.getCurrentSprite().getSpellsDescriptors().get(i); 
				spellIcons.add(new SpellIcon(iX, iY, 
						sd.getSpell().getSpellIcon(), 
						sd.getMaxLevel(), i, 
						sd.getSpell().getName()));
			}
			// Otherwise just add the empty space
			else
				spellIcons.add(new SpellIcon(iX, iY, emptySpot, -1, -1, null));
			
			hasSelected = false;
			selected = 0;
		}
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) 
	{
		graphics.setColor(Panel.COLOR_FOREFRONT);
		
		Panel.drawPanelBox(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 198 + gc.getDisplayPaddingX(), 
				CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 200 - 40, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 75, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 36 + 18, graphics);		
		
		/*
		if (hasSelected)
		{
			graphics.setColor(Color.red);
			graphics.drawRect(405, 393, 132, 25);
		}
		*/
		
		for (SpellIcon sml : spellIcons)
		{
			sml.render(gc, graphics);
		}
	}
	
	private class SpellIcon
	{
		private Rectangle iconArea;
		private Image icon;
		private int level = 0;
		private int maxLevel;
		private int index;
		private String spellName;
		private int cost;

		public SpellIcon(int locX, int locY, Image icon, int maxLevel, int index, String spellName) 
		{
			iconArea = new Rectangle(locX, locY, icon.getWidth() - 2, icon.getHeight() - 2);
			this.icon = icon;
			this.maxLevel = maxLevel;
			this.index = index;
			this.spellName = spellName;
		}
		
		public void render(FCGameContainer gc, Graphics graphics)
		{
			graphics.setColor(Panel.COLOR_FOREFRONT);
			graphics.drawImage(icon, iconArea.getX(), iconArea.getY());
						
			if (index == selected)
			{
				graphics.setColor(Color.red);
				Menu.drawRect(iconArea, graphics);
				graphics.drawRect(iconArea.getX() + 1, 
						iconArea.getY() + 1, 
						iconArea.getWidth() - 2, 
						iconArea.getHeight() - 2);
				
				graphics.setColor(COLOR_FOREFRONT);
				graphics.drawString(spellName, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 205 + gc.getDisplayPaddingX(), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 195 - 35);
				for (int i = 0; i < maxLevel; i++)
				{
					if (hasSelected)
					{
						if (i < level)
						{
							graphics.setColor(Color.yellow);
							graphics.fillRoundRect(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 200 + 10 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 15 + gc.getDisplayPaddingX(),
									CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 215 - 30, 
									CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 14, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7, 4);	
							graphics.setColor(COLOR_FOREFRONT);
						}
					}					
					graphics.drawRoundRect(CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 200 + 10 + i * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 15 + gc.getDisplayPaddingX(), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 215 - 30, 
							CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 14, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7, 4);					
				}
				// graphics.drawString(spellName, 410, 399);
				graphics.drawString("Cost:", CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 205 + gc.getDisplayPaddingX(), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 215 - 30);
				
				if (hasSelected)
				{
					if (currentSprite.getCurrentMP() < cost)
						graphics.setColor(Color.red);
						graphics.drawString(cost + "", CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 245 + gc.getDisplayPaddingX(), CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 215 - 30);
				}
			}
		}

		public int getIndex() {
			return index;
		}

		public void setLevel(int level) {
			this.level = level;
			cost = currentSprite.getSpellsDescriptors().get(selected).getSpell().getCosts()[level - 1];
		}
	}

	public void setVisible(boolean visible) {
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) 
	{
		if (input.isKeyDown(KeyMapping.BUTTON_UP))
		{
			if (!hasSelected)
			{
				if (selected != 0)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
				selected = 0;
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_DOWN))
		{
			if (!hasSelected && spellIcons.get(3).getIndex() != -1)
			{
				if (selected != 3)
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
				selected = 3;
			}
		}		
		else if (input.isKeyDown(KeyMapping.BUTTON_LEFT))
		{			
			if (!hasSelected)
			{
				if (spellIcons.get(1).getIndex() != -1)
				{
					if (selected != 1)
						stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
					selected = 1;					
				}
			}
			else
			{
				if (spellIcons.get(selected).level > 1)
				{
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
					spellIcons.get(selected).setLevel(spellIcons.get(selected).level - 1);
					return MenuUpdate.MENU_ACTION_SHORT;
				}
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_RIGHT))
		{
			if (!hasSelected)
			{
				if (spellIcons.get(2).getIndex() != -1)
				{
					if (selected != 2)
						stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
					selected = 2;					
				}
			}
			else
			{
				if (spellIcons.get(selected).level < spellIcons.get(selected).maxLevel)
				{
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menumove", 1f, false));
					spellIcons.get(selected).setLevel(spellIcons.get(selected).level + 1);
					return MenuUpdate.MENU_ACTION_SHORT;
				}
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_2))
		{
			stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuback", 1f, false));
			if (hasSelected)
			{
				hasSelected = false;
				return MenuUpdate.MENU_ACTION_LONG;
			}
			else	
			{
				stateInfo.sendMessage(Message.MESSAGE_SHOW_BATTLEMENU);
				return MenuUpdate.MENU_CLOSE;
			}
		}
		else if (input.isKeyDown(KeyMapping.BUTTON_3))
		{								
			if (hasSelected)
			{
				if (spellIcons.get(selected).cost <= stateInfo.getCurrentSprite().getCurrentMP())
				{
					stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "targetselect", 1f, false));
					stateInfo.sendMessage(new BattleSelectionMessage(Message.MESSAGE_SELECT_SPELL, selected, spellIcons.get(selected).level));
					return MenuUpdate.MENU_CLOSE;
				}
				else
				{
					stateInfo.sendMessage(new ChatMessage(Message.MESSAGE_SEND_INTERNAL_MESSAGE, "SYSTEM", "SYSTEM: You do not have enough MP to cast that spell"));
				}
			}
			else
			{
				stateInfo.sendMessage(new AudioMessage(Message.MESSAGE_SOUND_EFFECT, "menuselect", 1f, false));
				spellIcons.get(selected).setLevel(1);
				hasSelected = true;				
				return MenuUpdate.MENU_ACTION_LONG;
			}
		}
		
		
		
		return MenuUpdate.MENU_NO_ACTION;
	}
}
