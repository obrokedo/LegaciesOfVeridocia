package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.listener.YesNoListener;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.Button;
import mb.fc.game.ui.CellRenderer;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.game.ui.Table;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class PriestMenu extends Menu implements CellRenderer<CombatSprite>, YesNoListener
{
	private CombatSprite selectedHero = null;
	private Table<CombatSprite> heroNames;
	private ArrayList<Button> buttons;
	private int x;
	private StateInfo stateInfo;
	
	public PriestMenu(StateInfo stateInfo, GameContainer gc, ArrayList<CombatSprite> heroes) {
		super(Panel.PANEL_PRIEST);
		x = (gc.getWidth() - 390) / 2;
		heroNames = new Table<CombatSprite>(x + 15, 215, new int[] {150}, new String[] {"Names"}, 7, heroes, this);
		buttons = new ArrayList<Button>();
		buttons.add(new Button(x + 190, 215, 180, 25, "Save Game"));
		buttons.add(new Button(x + 190, 265, 180, 25, "Promote"));
		buttons.get(1).setEnabled(false);
		buttons.add(new Button(x + 190, 315, 180, 25, "Cure"));
		buttons.get(2).setEnabled(false);
		buttons.add(new Button(x + 190, 365, 180, 25, "Resurrect"));				
		buttons.get(3).setEnabled(false);
		this.stateInfo = stateInfo;
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
			if (selectedHero.getCurrentHP() > 0)
				buttons.get(3).setEnabled(false);
			else
				buttons.get(3).setEnabled(true);
		}
				
		if (buttons.get(0).handleUserInput(mouseX, mouseY, leftClick))
			stateInfo.sendMessage(Message.MESSAGE_SAVE);
		buttons.get(1).handleUserInput(mouseX, mouseY, leftClick);
		buttons.get(2).handleUserInput(mouseX, mouseY, leftClick);		
		if (buttons.get(3).handleUserInput(mouseX, mouseY, leftClick))
		{
			stateInfo.addMenu(new YesNoMenu(stateInfo.getGc(), 
					"Would you like to resurrect " + selectedHero.getName() + " for " + (10 * selectedHero.getLevel() + " gold?"), this));
		}
		*/
		
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) 
	{
		Panel.drawPanelBox(x, 200, 390, 270, graphics);
		heroNames.render(gc, graphics);
		
		for (Button b : buttons)
			b.render(gc, graphics);
		
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawString("Gold: " + stateInfo.getClientProfile().getGold(), x + 190, 415);
	}
	
	@Override
	public String getColumnValue(CombatSprite item, int column, Graphics graphics) 
	{
		if (item.getCurrentHP() <= 0)
			graphics.setColor(Color.red);
		else
			graphics.setColor(Panel.COLOR_FOREFRONT);
		return item.getName();
	}

	@Override
	public boolean valueSelected(StateInfo stateInfo, boolean value) 
	{
		if (value)
		{
			selectedHero.setCurrentHP(selectedHero.getMaxHP());
			buttons.get(3).setEnabled(false);
			stateInfo.getClientProfile().setGold(stateInfo.getClientProfile().getGold() - selectedHero.getLevel() * 10);
		}
		return false;
	}
}
