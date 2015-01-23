package mb.fc.game.ui;

import mb.fc.engine.CommRPG;
import mb.fc.game.hudmenu.Panel;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class RectUI extends Rectangle
{
	private static final long serialVersionUID = 1L;

	public RectUI(float x, float y, float width, float height,
			int nonScaleX, int nonScaleY, int nonScaleWidth, int nonScaleHeight) {
		super(x * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] + nonScaleX,
				y * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] + nonScaleY,
					width * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] + nonScaleWidth,
						height * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] + nonScaleHeight);
	}

	public RectUI(float x, float y, float width, float height)
	{
		this(x, y, width, height, 0, 0, 0, 0);
	}

	public void fillRect(Graphics g, Color color)
	{
		g.setColor(color);
		g.fill(this);
	}

	public void drawRect(Graphics g, Color color)
	{
		g.setColor(color);
		g.draw(this);
	}

	public void drawPanel(Graphics g)
	{
		Panel.drawPanelBox((int) x, (int) y, (int) width, (int) height, g);
	}

	public void drawPanel(Graphics g, Color color)
	{
		Panel.drawPanelBox((int) x, (int) y, (int) width, (int) height, g, color);
	}

	@Override
	public void setX(float x) {
		super.setX(x * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
	}

	@Override
	public void setY(float y) {
		super.setY(y * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
	}


}
