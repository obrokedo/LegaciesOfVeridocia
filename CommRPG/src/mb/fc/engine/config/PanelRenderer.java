package mb.fc.engine.config;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;

/**
 * Interface to call the PanelRender python method that renders the background/borders
 * of all default panels in the game.
 *
 * NOTE: Despite the fact that the name contains the term "JPanel", this class in no
 * way interacts with the swing component by the same name
 *
 *@see /scripts/PanelRender.py
 *
 * @author Broked
 *
 */
public interface PanelRenderer
{
	public void render(SpriteSheet menuBorder, int x, int y, int width, int height, Graphics graphics);
	public void reload();
}
