package mb.fc.game.menu;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.listener.StringListener;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.ui.Button;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.TextField;

public class StringMenu extends Menu
{
	private int x;
	private int width;
	private StringListener listener;
	private String text;
	private Button okButton;
	private TextField textField;
	
	public StringMenu(GameContainer gc, String text, StringListener listener)
	{
		super(Panel.PANEL_STRING);
		width = gc.getDefaultFont().getWidth(text) + 30;
		this.text = text;
		x = (gc.getWidth() - width) / 2;
		this.listener = listener;
		okButton = new Button(width / 2 - 20 + x, 385, 40, 20, "Ok");
		textField = new TextField(gc, gc.getDefaultFont(), x + 15, 345, width - 30, 25);
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
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) 
	{
		Panel.drawPanelBox(x, 300, width, 120, graphics);
		graphics.setColor(COLOR_FOREFRONT);
		graphics.drawString(text, x + 15, 315);
		okButton.render(gc, graphics);
		textField.render(gc, graphics);
	}
}
