package mb.fc.game.sprite;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.ui.PaddedGameContainer;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class StaticSprite extends Sprite
{
	private static final long serialVersionUID = 1L;

	private Image image;
	private int[] triggerIds;
	private final static String SEARCH_AREA_NAME = "SEARCHAREA--!!";

	public StaticSprite(int locX, int locY, String name, Image image, int[] triggerIds)
	{
		super(locX, locY, Integer.MAX_VALUE);
		this.image = image;
		this.name = name;
		this.triggerIds = triggerIds;
		this.spriteType = Sprite.TYPE_STATIC_SPRITE;
		this.id = Integer.MIN_VALUE;
	}

	public StaticSprite(int locX, int locY, int[] triggerIds)
	{
		this(locX, locY, SEARCH_AREA_NAME, null, triggerIds);
	}

	@Override
	public void render(Camera camera, Graphics graphics, PaddedGameContainer cont) {
		if (image == null)
			return;
		graphics.drawImage(image, Math.round(this.getLocX() - camera.getLocationX()),
			Math.round(this.getLocY() - camera.getLocationY()));
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
}
