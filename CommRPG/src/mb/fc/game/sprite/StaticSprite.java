package mb.fc.game.sprite;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.ui.FCGameContainer;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class StaticSprite extends Sprite
{
	private static final long serialVersionUID = 1L;
	
	private Image image;
	private int[] triggerIds;
	
	public StaticSprite(int locX, int locY, String name, Image image, int[] triggerIds)
	{
		super(locX, locY);
		this.image = image;
		this.name = name;
		this.triggerIds = triggerIds;
		this.spriteType = Sprite.TYPE_STATIC_SPRITE;
	}

	@Override
	public void render(Camera camera, Graphics graphics, FCGameContainer cont) {
		graphics.drawImage(image, this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(), 
				this.getLocY() - camera.getLocationY());
	}

	public void setImage(Image image) {
		this.image = image;
	}
	
	public void triggerButton1Event(StateInfo stateInfo)
	{
		if (triggerIds != null)
		{
			for (Integer triggerId : triggerIds)
				stateInfo.getResourceManager().getTriggerEventById(triggerId).perform(stateInfo);
		}
	}
}
