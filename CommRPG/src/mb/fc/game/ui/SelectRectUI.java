package mb.fc.game.ui;

import mb.fc.engine.CommRPG;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

public class SelectRectUI extends Rectangle
{
	public SelectRectUI(float x, float y, float width, float height) {
		super(x * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				y * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				width * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
				height * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
	}

	public void draw(Graphics g, Color color)
	{
		g.setColor(color);
		for (int i = 0; i < CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]; i++)
			g.drawRect(x + i, y + i, width - i * 2, height - i * 2);
	}

	@Override
	public void setX(float x) {
		super.setX(x * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
	}
}
