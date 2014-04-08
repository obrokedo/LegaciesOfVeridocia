package mb.fc.game.menu;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.input.FCInput;
import mb.fc.game.listener.StringListener;
import mb.fc.game.ui.Button;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class UninitializedStringMenu extends StringMenu
{
	protected Button cinButton;

	public UninitializedStringMenu(GameContainer gc, String text,
			StringListener listener) {
		super(gc, text, listener);
		okButton = new Button(width / 2 - 60 + x, 235, 50, 20, "Town");
		cinButton = new Button(width / 2 + 20  + x, 235, 50, 20, "Cin");
	}
	
	public boolean handleMouseInput(int mouseX, int mouseY, boolean leftClick)
	{		
		if (okButton.handleUserInput(mouseX, mouseY, leftClick))
		{
			listener.stringEntered(textField.getText(), "OK");
			return true;
		}
		
		if (cinButton.handleUserInput(mouseX, mouseY, leftClick))
		{
			listener.stringEntered(textField.getText(), "CIN");
			return true;
		}
		textField.setFocus(true);
		return false;
	}
	
	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) 
	{
		/*
		if (okButton.handleUserInput(mouseX, mouseY, leftClick))
		{
			listener.stringEntered(textField.getText());
			return true;
		}
		textField.setFocus(true);
		*/
		return MenuUpdate.MENU_NO_ACTION;
	}


	@Override
	public void render(FCGameContainer gc, Graphics graphics) {
		graphics.setColor(Color.blue);
		graphics.fillRect(x, 150, width, 120);
		graphics.setColor(COLOR_FOREFRONT);
		graphics.drawString(text, x + 15, 165);
		okButton.render(gc, graphics);
		cinButton.render(gc, graphics);
		textField.render(gc, graphics);
	}	
}
