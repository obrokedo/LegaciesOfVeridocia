package mb.fc.game.sprite;

import mb.fc.game.Camera;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class StaticSprite extends Sprite
{
	private static final long serialVersionUID = 1L;
	
	private Image image;
	
	public StaticSprite(int locX, int locY, String name, Image image)
	{
		super(locX, locY);
		this.image = image;
		this.name = name;
	}

	@Override
	public void render(Camera camera, Graphics graphics, FCGameContainer cont) {
		graphics.drawImage(image, this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(), 
				this.getLocY() - camera.getLocationY());
	}			
}
