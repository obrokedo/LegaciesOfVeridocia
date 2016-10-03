package mb.fc.game;

import mb.fc.game.sprite.Sprite;
import mb.fc.map.Map;

import org.newdawn.slick.geom.Rectangle;

/**
 * Contains information that indicates which portion of a map should be displayed at any given time.
 * Also contains helper methods to move the camera around.
 *
 * @author Broked
 *
 */
public class Camera
{
	private Rectangle viewport;

	public Camera(int width, int height) {
		super();
		viewport = new Rectangle(0, 0, width, height);
	}

	private Camera(Camera camera)
	{
		this.viewport = new Rectangle(camera.viewport.getX(), camera.viewport.getY(),
				camera.viewport.getWidth(), camera.viewport.getHeight());
	}

	public Rectangle getViewport() {
		return viewport;
	}

	public float getLocationX()
	{
		return viewport.getX();
	}

	public float getLocationY()
	{
		return viewport.getY();
	}

	public void setLocation(float x, float y)
	{
		viewport.setLocation(x, y);
	}

	public int getViewportWidth()
	{
		return (int) viewport.getWidth();
	}

	public int getViewportHeight()
	{
		return (int) viewport.getHeight();
	}

	public void centerOnSprite(Sprite sprite, Map map)
	{
		if (sprite.getLocX() < getViewportWidth() / 2)
			viewport.setX(0);
		else if (sprite.getLocX() > map.getMapWidthInPixels() - getViewportWidth() / 2)
		{
			viewport.setX(Math.max(0, map.getMapWidthInPixels() - getViewportWidth()));
		}
		else
			viewport.setX(Math.max(0, sprite.getLocX() - getViewportWidth() / 2));

		if (sprite.getLocY() < getViewportHeight() / 2)
			viewport.setY(0);
		else if (sprite.getLocY() > map.getMapHeightInPixels() - getViewportHeight() / 2)
		{
			viewport.setY(Math.max(0, map.getMapHeightInPixels() - getViewportHeight()));
		}
		else
			viewport.setY(Math.max(0, sprite.getLocY() - getViewportHeight() / 2));
	}

	public void centerOnPoint(float locX, float locY, Map map)
	{
		if (locX < getViewportWidth() / 2)
			viewport.setX(0);
		else if (locX > map.getMapWidthInPixels() - getViewportWidth() / 2)
		{
			viewport.setX(Math.max(0, map.getMapWidthInPixels() - getViewportWidth()));
		}
		else
			viewport.setX(Math.max(0, locX - getViewportWidth() / 2));

		if (locY < getViewportHeight() / 2)
			viewport.setY(0);
		else if (locY >  map.getMapHeightInPixels() - getViewportHeight() / 2)
		{
			viewport.setY(Math.max(0, map.getMapHeightInPixels() - getViewportHeight()));
		}
		else
			viewport.setY(Math.max(0, locY - getViewportHeight() / 2));
	}

	public Camera duplicate()
	{
		return new Camera(this);
	}
}
