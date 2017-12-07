package mb.fc.game.sprite;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;

public class StaticSprite extends Sprite
{
	private static final long serialVersionUID = 1L;

	private Image image;
	private int[] triggerIds;
	private boolean offsetUp = false;

	public StaticSprite(int locX, int locY, String name, Image image, int[] triggerIds)
	{
		super(locX, locY, Integer.MAX_VALUE);
		this.image = image;
		this.name = name;
		this.triggerIds = triggerIds;
		this.spriteType = Sprite.TYPE_STATIC_SPRITE;
		this.id = Integer.MIN_VALUE;
	}

	@Override
	public void render(Camera camera, Graphics graphics, GameContainer cont, int tileHeight) {
		if (image == null)
			return;
		
		float yPos = this.getLocY() - camera.getLocationY();
		
		if (offsetUp)
			yPos -= tileHeight / 2;
		
		graphics.drawImage(image, Math.round(this.getLocX() - camera.getLocationX()),
			Math.round(yPos));
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void triggerButton1Event(StateInfo stateInfo)
	{
		if (triggerIds != null)
		{
			for (Integer triggerId : triggerIds)
				if (triggerId != -1)
					stateInfo.getResourceManager().getTriggerEventById(triggerId).perform(stateInfo);
		}
	}

	public void setOffsetUp(boolean offsetUp) {
		this.offsetUp = offsetUp;
	}
}
