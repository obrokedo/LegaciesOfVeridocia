package mb.fc.game.menu;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.listener.YesNoListener;
import mb.fc.game.ui.Button;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class YesNoMenu extends Menu
{
	private int x;
	private int width;
	private String text;
	private Button yesButton;
	private Button noButton;
	// private YesNoListener listener;

	public YesNoMenu(GameContainer gc, String text, YesNoListener listener) {
		super(Panel.PANEL_YES_NO);
		width = gc.getDefaultFont().getWidth(text) + 30;
		this.text = text;
		x = (gc.getWidth() - width) / 2;		
		yesButton = new Button(width / 3 - 20 + x, 355, 40, 20, "Yes");
		noButton = new Button(width / 3 * 2- 20 + x, 355, 40, 20, "No");
		// this.listener = listener;
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		/*
		if (yesButton.handleUserInput(mouseX, mouseY, leftClick))
		{
			listener.valueSelected(stateInfo, true);
			return true;
		}
		if (noButton.handleUserInput(mouseX, mouseY, leftClick))
		{
			listener.valueSelected(stateInfo, false);
			return true;
		}
		*/
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) 
	{
		Panel.drawPanelBox(x, 300, width, 100, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawString(text, x + 15, 315);
		yesButton.render(gc, graphics);
		noButton.render(gc, graphics);
	}

}
