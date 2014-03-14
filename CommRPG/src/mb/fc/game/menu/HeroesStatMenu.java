package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.battle.spell.KnownSpell;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.item.EquippableItem;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.Button;
import mb.fc.game.ui.CellRenderer;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.game.ui.Table;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class HeroesStatMenu extends Menu implements CellRenderer<CombatSprite>
{
	private CombatSprite selectedHero = null;
	private Table<CombatSprite> heroNames;
	private ArrayList<Button> itemButtons;
	private int x;
	
	public HeroesStatMenu(GameContainer gc, ArrayList<CombatSprite> heroes) 
	{
		super(Panel.PANEL_HEROS_OVERVIEW);
		x = (gc.getWidth() - 700) / 2;
		heroNames = new Table<CombatSprite>(x + 15, 65, new int[] {150}, new String[] {"Names"}, 19, heroes, this);
		this.itemButtons = new ArrayList<Button>();
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) {
		Panel.drawPanelBox(x, 50, 700, 635, graphics);
		heroNames.render(gc, graphics);
		
		graphics.setColor(COLOR_FOREFRONT);
		graphics.drawRect(x + 195, 65, 490, 600);
		
		if (selectedHero != null)
			displayHero(gc, graphics);				
	}

	private void displayHero(GameContainer gc, Graphics graphics)
	{
		if (selectedHero.getCurrentHP() > 0)
			graphics.drawString(selectedHero.getName(), x + 210, 80);
		else
			graphics.drawString(selectedHero.getName() + " (DEAD)", x + 210, 80);
		graphics.drawString("HP: " + selectedHero.getCurrentHP() + " / " + selectedHero.getMaxHP(), x + 400, 110);
		graphics.drawString("MP: " + selectedHero.getCurrentMP() + " / " + selectedHero.getMaxMP(), x + 400, 140);
		graphics.drawString("LVL: " + selectedHero.getLevel(), x + 400, 170);
		graphics.drawString("EXP: " + selectedHero.getExp(), x + 400, 200);
		graphics.drawString("Atk: " + selectedHero.getCurrentAttack(), x + 560, 110);
		graphics.drawString("Def: " + selectedHero.getCurrentDefense(), x + 560, 140);
		graphics.drawString("Spd: " + selectedHero.getCurrentSpeed(), x + 560, 170);
		graphics.drawString("Mov: " + selectedHero.getCurrentMove(), x + 560, 200);
		
		// Draw Items
		graphics.drawString("Items", x + 210, 300);
		graphics.drawLine(x + 210, 320, x + 380, 320);
		for (int i = 0; i < selectedHero.getItemsSize(); i++)
		{
			graphics.drawImage(selectedHero.getItem(i).getImage(), x + 210, 330 + i * 35);
			itemButtons.get(i).render(gc, graphics);
		}
		
		// Draw Spells
		graphics.drawString("Spells", x + 500, 300);
		graphics.drawLine(x + 500, 320, x + 680, 320);
		if (selectedHero.getSpellsDescriptors() != null)
		{
			for (int i = 0; i < selectedHero.getSpellsDescriptors().size(); i++)
			{
				graphics.setColor(Panel.COLOR_FOREFRONT);
				KnownSpell sd = selectedHero.getSpellsDescriptors().get(i);
				graphics.drawString(sd.getSpell().getName(), x + 500, 330 + i * 30);
				for (int j = 0; j < sd.getMaxLevel(); j++)
				{
					graphics.setColor(Color.yellow);
					graphics.fillRect(x + 600 + j * 20, 332 + i * 30, 15, 15);
				}
			}
		}
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) 
	{
		/*
		if (rightClick)
			return true;
		
		CombatSprite cs = heroNames.handleUserInput(mouseX, mouseY, leftClick);
		if (cs != null)
		{
			selectedHero = cs;
			reinitItems();
		}
		
		for (int i = 0; i < itemButtons.size(); i++)
		{			
			if (itemButtons.get(i).handleUserInput(mouseX, mouseY, leftClick))
			{
				if (selectedHero.getEquipped().get(i))
				{
					selectedHero.unequipItem((EquippableItem) selectedHero.getItem(i));
					reinitItems();
				}
				else if (selectedHero.getItem(i).isEquippable() && selectedHero.isEquippable((EquippableItem) selectedHero.getItem(i)))
				{
					selectedHero.equipItem((EquippableItem) selectedHero.getItem(i));
					reinitItems();
				}
			}
		*/
		
		return MenuUpdate.MENU_CLOSE;
	}
	
	private void reinitItems()
	{
		itemButtons.clear();
		for (int i = 0; i < selectedHero.getItemsSize(); i++)
		{
			if (selectedHero.getEquipped().get(i))
				itemButtons.add(new Button(x + 250, 330 + i * 35, 225, 35, "(EQ) " + selectedHero.getItem(i).getName()));
			else
				itemButtons.add(new Button(x + 250, 330 + i * 35, 225, 35, selectedHero.getItem(i).getName()));
			itemButtons.get(i).displayBorder(false);
		}
	}

	@Override
	public String getColumnValue(CombatSprite item, int column, Graphics graphics) {
		if (item.getCurrentHP() <= 0)
			graphics.setColor(Color.red);
		else
			graphics.setColor(Panel.COLOR_FOREFRONT);
		return item.getName();
	}
}
