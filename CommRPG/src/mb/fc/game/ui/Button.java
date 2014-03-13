package mb.fc.game.ui;

import mb.fc.game.hudmenu.Panel;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class Button 
{
	private Rectangle rect;
	private boolean mouseOver;
	private String text;
	private boolean displayBorder = true;
	private boolean enabled = true;
	
	public Button(int x, int y, int width, int height, String text) {
		super();
		this.rect = new Rectangle(x, y, width, height);
		this.mouseOver = false;
		this.text = text;
	}

	public boolean handleUserInput(int mouseX, int mouseY, boolean leftClick)
	{
		if (enabled && rect.contains(mouseX, mouseY))
		{
			mouseOver = true;
			if (leftClick)
				return true;
		}
		else
			mouseOver = false;
		return false;
	}
	
	public void render(GameContainer gc, Graphics graphics)
	{
		if (mouseOver)
		{
			graphics.setColor(Panel.COLOR_MOUSE_OVER);				
			Panel.fillRect(rect, graphics);
		}
		
		if (enabled)
			graphics.setColor(Panel.COLOR_FOREFRONT);
		else
			graphics.setColor(Color.lightGray);
		if (displayBorder)
			Panel.drawRect(rect, graphics);
		graphics.drawString(text, rect.getX() + 5, rect.getY() + 2);
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void displayBorder(boolean displayBorder)
	{
		this.displayBorder = displayBorder;
	}
	
	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}
}
