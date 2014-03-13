package mb.fc.game;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.sprite.Sprite;

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
	
	public void setLocation(int x, int y)
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
	
	public void centerOnSprite(Sprite sprite, StateInfo stateInfo)
	{						
		if (sprite.getLocX() < getViewportWidth() / 2)
			viewport.setX(0);
		else if (sprite.getLocX() > stateInfo.getResourceManager().getMap().getMapWidthInPixels() - getViewportWidth() / 2)
		{
			viewport.setX(Math.max(0, stateInfo.getResourceManager().getMap().getMapWidthInPixels() - getViewportWidth()));
		}
		else
			viewport.setX(Math.max(0, sprite.getLocX() - getViewportWidth() / 2));
		
		if (sprite.getLocY() < getViewportHeight() / 2)
			viewport.setY(0);
		else if (sprite.getLocY() > stateInfo.getResourceManager().getMap().getMapHeightInPixels() - getViewportHeight() / 2)
		{
			viewport.setY(Math.max(0, stateInfo.getResourceManager().getMap().getMapHeightInPixels() - getViewportHeight()));
		}
		else
			viewport.setY(Math.max(0, sprite.getLocY() - getViewportHeight() / 2));
	}
	
	public void centerOnPoint(int locX, int locY, StateInfo stateInfo)
	{						
		if (locX < getViewportWidth() / 2)
			viewport.setX(0);
		else if (locX > stateInfo.getResourceManager().getMap().getMapWidthInPixels() - getViewportWidth() / 2)
		{
			viewport.setX(Math.max(0, stateInfo.getResourceManager().getMap().getMapWidthInPixels() - getViewportWidth()));
		}
		else
			viewport.setX(Math.max(0, locX - getViewportWidth() / 2));
		
		if (locY < getViewportHeight() / 2)
			viewport.setY(0);
		else if (locY > stateInfo.getResourceManager().getMap().getMapHeightInPixels() - getViewportHeight() / 2)
		{
			viewport.setY(Math.max(0, stateInfo.getResourceManager().getMap().getMapHeightInPixels() - getViewportHeight()));
		}
		else
			viewport.setY(Math.max(0, locY - getViewportHeight() / 2));
	}
}
