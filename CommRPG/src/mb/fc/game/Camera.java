package mb.fc.game;

import mb.fc.game.sprite.Sprite;
import mb.fc.map.Map;

import org.newdawn.slick.geom.Rectangle;

public class Camera 
{
	private Rectangle viewport;

	public Camera(int width, int height) {
		super();
		viewport = new Rectangle(0, 0, width, height);
	}

	public Rectangle getViewport() {
		return viewport;
	}
	
	public int getLocationX()
	{
		return (int) viewport.getX();
	}
	
	public int getLocationY()
	{
		return (int) viewport.getY();
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
}
