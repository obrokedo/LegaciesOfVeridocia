package mb.fc.game.menu;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.input.FCInput;
import mb.fc.game.listener.StringListener;
import mb.fc.game.ui.Button;
import mb.fc.game.ui.PaddedGameContainer;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.TextField;

public class StringMenu extends Menu
{
	protected int x;
	protected int width;
	protected StringListener listener;
	protected String text;
	protected Button okButton;
	protected TextField textField;
	protected String action;

	public StringMenu(GameContainer gc, String text, StringListener listener)
	{
		super(PanelType.PANEL_STRING);
		width = gc.getDefaultFont().getWidth(text) + 30;
		this.text = text;
		x = (CommRPG.GAME_SCREEN_SIZE.width - width) / 2;
		this.listener = listener;
		okButton = new Button(width / 2 - 20 + x, 235, 40, 20, "Ok");
		textField = new TextField(gc, gc.getDefaultFont(), x + 15, 195, width - 30, 25);
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo)
	{
		/*
		if (okButton.handleUserInput(input.g, mouseY, leftClick))
		{
			listener.stringEntered(textField.getText());
			return true;
		}
		*/
		textField.setFocus(true);
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics)
	{
		graphics.setColor(Color.blue);
		// Panel.drawPanelBox(x, 150, width, 120, graphics);
		graphics.setColor(COLOR_FOREFRONT);
		graphics.drawString(text, x + 15, 165);
		okButton.render(graphics);
		textField.render(gc, graphics);
	}
}
