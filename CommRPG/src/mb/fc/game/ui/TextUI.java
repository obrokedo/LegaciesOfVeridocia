package mb.fc.game.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

public class TextUI
{
	private String text;
	private int x, y;

	public TextUI(String text, int x, int y, int unscaleX)
	{
		this.text = text;
		this.x = x + unscaleX;
		this.y = y;
	}

	public TextUI(String text, int x, int y)
	{
		this(text, x, y, 0);
	}

	public void drawText(Graphics g)
	{
		g.drawString(text, x, y);
	}

	public void drawText(Graphics g, Color color)
	{
		g.setColor(Color.white);
		g.drawString(text, x, y);
	}

	public void drawText(Graphics g, Color color, Font font)
	{
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString(text, x, y);
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
}
