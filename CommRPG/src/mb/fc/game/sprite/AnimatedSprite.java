package mb.fc.game.sprite;

import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class AnimatedSprite extends Sprite 
{
	private static final long serialVersionUID = 1L;
	
	protected transient Image image;
	protected transient int imageIndex;
	protected transient SpriteSheet spriteSheet;
	protected transient int animationDelay = 0;
	protected String imageName;
	
	public AnimatedSprite(int locX, int locY, String imageName) {
		super(locX, locY);
		this.imageName = imageName;
	}
	
	public Image getCurrentImage()
	{
		return image;
	}	
	
	@Override
	public void update() 
	{
		animationDelay++;
		if (animationDelay == 4)
		{
			if (imageIndex % 2 == 1)
			{
				imageIndex--;
				this.image = spriteSheet.getSprite(imageIndex, 0);		
			}
			else
			{
				imageIndex++;
				this.image = spriteSheet.getSprite(imageIndex, 0);
			}
			animationDelay = 0;
		}				
	}
}
