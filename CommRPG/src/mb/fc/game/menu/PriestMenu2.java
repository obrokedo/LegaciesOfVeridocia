package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.listener.MenuListener;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.game.ui.Button;
import mb.fc.game.ui.CellRenderer;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.game.ui.Table;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class PriestMenu2 extends Menu implements CellRenderer<CombatSprite>, MenuListener
{
	private CombatSprite selectedHero = null;
	private Table<CombatSprite> heroNames;
	private ArrayList<Button> buttons;
	private int x;
	private StateInfo stateInfo;

	public PriestMenu2(StateInfo stateInfo, GameContainer gc, Iterable<CombatSprite> heroes) {
		super(PanelType.PANEL_PRIEST);
		x = (CommRPG.GAME_SCREEN_SIZE.width - 390) / 2;
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
			stateInfo.sendMessage(MessageType.SAVE);
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
	public void render(PaddedGameContainer gc, Graphics graphics)
	{
		Panel.drawPanelBox(x, 200, 390, 270, graphics);
		heroNames.render(gc, graphics);

		for (Button b : buttons)
			b.render( graphics);

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
	public boolean valueSelected(StateInfo stateInfo, Object value)
	{
		if ((boolean) value)
		{
			selectedHero.setCurrentHP(selectedHero.getMaxHP());
			buttons.get(3).setEnabled(false);
			stateInfo.getClientProfile().setGold(stateInfo.getClientProfile().getGold() - selectedHero.getLevel() * 10);
		}
		return false;
	}

	@Override
	public void menuClosed() {

	}
}