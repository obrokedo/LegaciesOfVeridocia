package mb.fc.game.sprite;

import org.newdawn.slick.Image;

public class StaticSprite extends Sprite
{
	private static final long serialVersionUID = 1L;
	
	private Image image;
	
	public StaticSprite(Image image, int locX, int locY)
	{
		super(locX, locY);
		this.image = image;
	}	
}
