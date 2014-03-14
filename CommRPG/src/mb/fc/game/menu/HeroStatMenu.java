package mb.fc.game.menu;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class HeroStatMenu extends Menu
{
	private CombatSprite selectedSprite;
	private int x;
	private int y;
	private String gold;

	public HeroStatMenu(GameContainer gc, CombatSprite selectedSprite, StateInfo stateInfo) {
		super(Panel.PANEL_HEROS_STATS);
		x = (gc.getWidth() - 424) / 2;
		y = (gc.getHeight() - 385) / 2;
		this.selectedSprite = selectedSprite;
		if (selectedSprite.isHero())
			this.gold = stateInfo.getClientProfile().getGold() + "";
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {			
		if (input.isKeyDown(KeyMapping.BUTTON_2))
			return MenuUpdate.MENU_CLOSE;
		return MenuUpdate.MENU_NO_ACTION;
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) 
	{
		/*****************************/
		/* Draw the main stat window */
		/*****************************/
		Panel.drawPanelBox(x + 124, y, 300, 385, graphics);		
		graphics.setColor(COLOR_FOREFRONT);
		graphics.drawString(selectedSprite.getName(), x + 140, y + -3);
		
		graphics.drawString("LVL: " + selectedSprite.getLevel(), x + 150, y + 27);
		graphics.drawString("HP: " + selectedSprite.getCurrentHP() + " / " + selectedSprite.getMaxHP(), x + 150, y + 57);
		graphics.drawString("MP: " + selectedSprite.getCurrentMP() + " / " + selectedSprite.getMaxMP(), x + 150, y + 87);		
		graphics.drawString("EXP: " + selectedSprite.getExp(), x + 150, y + 117);
		
		graphics.drawString("ATK: " + selectedSprite.getCurrentAttack(), x + 305, y + 27);
		graphics.drawString("DEF: " + selectedSprite.getCurrentDefense(), x + 305, y + 57);
		graphics.drawString("SPD: " + selectedSprite.getCurrentSpeed(), x + 305, y + 87);
		graphics.drawString("MOV: " + selectedSprite.getCurrentMove(), x + 305, y + 117);
		
		// Draw Spells
		graphics.drawString("MAGIC", x + 140, y + 147);		
		if (selectedSprite.getSpellsDescriptors() != null)
		{
			for (int i = 0; i < selectedSprite.getSpellsDescriptors().size(); i++)
			{
				graphics.setColor(Panel.COLOR_FOREFRONT);
				KnownSpell sd = selectedSprite.getSpellsDescriptors().get(i);
				graphics.drawImage(sd.getSpell().getSpellIcon(), x + 140, y + 185 + i * 47);
				graphics.drawString(sd.getSpell().getName(), x + 175, y + 167 + i * 47);
				for (int j = 0; j < sd.getMaxLevel(); j++)
				{
					graphics.setColor(Color.yellow);
					graphics.fillRect(x + 175 + j * 20, y + 210 + i * 45, 15, 15);
				}
			}
		}
		
		// Draw Items
		graphics.setColor(Panel.COLOR_FOREFRONT);		
		graphics.drawString("ITEM", x + 270, y + 147);
		for (int i = 0; i < selectedSprite.getItemsSize(); i++)
		{
			graphics.setColor(Panel.COLOR_FOREFRONT);
			graphics.drawImage(selectedSprite.getItem(i).getImage(), x + 270, y + 183 + i * 48);
			String[] itemSplit = selectedSprite.getItem(i).getName().split(" ");
			graphics.drawString(itemSplit[0], x + 310, y + 166 + i * 47);
			if (itemSplit.length > 1)
				graphics.drawString(itemSplit[1], x + 310, y + 180 + i * 47);
			
			if (selectedSprite.getEquipped().get(i))
			{
				graphics.setColor(Color.pink);
				graphics.drawString("EQPD", x + 310, y + 194 + i * 47);
			}
			
		}	
		
		/****************************/
		/* Draw the portrait window	*/
		/****************************/
		if (selectedSprite.getPortraitImage() != null)
		{
			graphics.drawImage(selectedSprite.getPortraitImage(), x, y);
		}
		
		/*****************************/
		/* Draw the statistic window */
		/*****************************/
		Panel.drawPanelBox(x, y + 156, 124, 160, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawImage(selectedSprite.getAnimationImageAtIndex(selectedSprite.getAnimation("UnDown").frames.get(0).sprites.get(0).imageIndex), x + 49, y + 170);
		graphics.drawString("Kills", x + 15, y + 190);		
		graphics.drawString("Defeat", x + 15, y + 240);
		if (selectedSprite.isHero())
		{
			graphics.drawString(selectedSprite.getKills() + "", x + 30, y + 210);
			graphics.drawString(selectedSprite.getDefeat() + "", x + 30, y + 260);
		}
		else
		{
			graphics.drawString("?", x + 30, y + 210);
			graphics.drawString("?", x + 30, y + 260);
		}
		
		/************************/
		/* Draw the gold window */
		/************************/
		if (selectedSprite.isHero())
		{			
			Panel.drawPanelBox(x, y + 316, 124, 69, graphics);
			graphics.setColor(Panel.COLOR_FOREFRONT);
			graphics.drawString("Gold", x + 15, y + 311);
			graphics.drawString(gold, x + 15, y + 335);
		}
	}
}
