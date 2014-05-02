package mb.jython;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

public interface JPanelRender 
{
	public void render(SpriteSheet menuBorder, int x, int y, int width, int height, Graphics graphics);
	public void reload();
}
