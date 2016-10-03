package mb.fc.game.menu;

import java.util.ArrayList;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class SystemMenu extends Menu
{
	private ArrayList<SystemMenuMouseHandler> systemMenuMouseHandler;
	private int x = 200;
	private int y = 200;

	public SystemMenu(GameContainer gc) {
		super(PanelType.PANEL_SYSTEM);
		x = (gc.getWidth() - 250) / 2;
		systemMenuMouseHandler = new ArrayList<SystemMenuMouseHandler>();
		systemMenuMouseHandler.add(new SystemMenuMouseHandler(new Rectangle(x + 20, y + 20, 210, 50), x + 75, "Connections", 0));
		systemMenuMouseHandler.add(new SystemMenuMouseHandler(new Rectangle(x + 20, y + 90, 210, 50), x + 40, "Return to Main Menu", 1));
		systemMenuMouseHandler.add(new SystemMenuMouseHandler(new Rectangle(x + 20, y + 160, 210, 50), x + 70, "Exit Program", 2));
	}

	@Override
	public void render(FCGameContainer gc, Graphics graphics) {

		Panel.drawPanelBox(x, y, 250, 230, graphics);
		for (SystemMenuMouseHandler s : systemMenuMouseHandler)
			s.render(gc, graphics);
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		/*
		if (rightClick)
			return true;

		for (SystemMenuMouseHandler s : systemMenuMouseHandler)
			s.checkMouseInput(mouseX, mouseY, leftClick, stateInfo);
			*/
		return MenuUpdate.MENU_CLOSE;
	}

	private class SystemMenuMouseHandler
	{
		private Rectangle rect;
		private int textX;
		private String text;
		private boolean mouseOver = false;

		public SystemMenuMouseHandler(Rectangle rect, int textX, String text, int index) {
			super();
			this.rect = rect;
			this.textX = textX;
			this.text = text;
		}

		public void render(GameContainer gc, Graphics graphics)
		{
			if (mouseOver)
			{
				graphics.setColor(Panel.COLOR_FOREFRONT);
				graphics.drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
				graphics.setColor(Panel.COLOR_MOUSE_OVER);
				graphics.fillRect(rect.getX() + 1, rect.getY() + 1, rect.getWidth() - 1, rect.getHeight() - 1);
				graphics.setColor(Panel.COLOR_FOREFRONT);
				graphics.drawString(text, textX, rect.getY() + 15);
			}
			else
			{
				graphics.setColor(Panel.COLOR_FOREFRONT);
				graphics.drawRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
				graphics.drawString(text, textX, rect.getY() + 15);
			}
		}
	}
}
