package mb.fc.game.sprite;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.trigger.Trigger.TriggerStatus;

public class StaticSprite extends Sprite
{
	private static final long serialVersionUID = 1L;

	private Image image;
	private int[] triggerIds;
	private boolean offsetUp = false;
	/**
	 * The id of a trigger that should be activated if no other triggers were
	 * activated on a "search" of this sprite
	 */
	private Integer defaultTriggerId = null;

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
			yPos -= tileHeight / 3;
		
		graphics.drawImage(image, this.getLocX() - camera.getLocationX(), yPos);
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public void triggerButton1Event(StateInfo stateInfo)
	{
		if (triggerIds != null)
		{
			boolean triggerActivated = false;
			
			for (Integer triggerId : triggerIds) {
				if (triggerId != -1) {
					if (stateInfo.getResourceManager().getTriggerEventById(
							triggerId).perform(stateInfo) == TriggerStatus.TRIGGERED) {
						triggerActivated = true;
					}
				}
			}
			
			if (defaultTriggerId != null && !triggerActivated) {
				stateInfo.getResourceManager().getTriggerEventById(
						defaultTriggerId).perform(stateInfo);
			}
		}
	}

	public void setOffsetUp(boolean offsetUp) {
		this.offsetUp = offsetUp;
	}

	/**
	 * Set the id of a trigger that should be activated if no other triggers were
	 * activated on a "search" of this sprite. A value of null means no trigger will
	 * be activated
	 *
	 * @param defaultTriggerId
	 */
	public void setDefaultTriggerId(Integer defaultTriggerId) {
		this.defaultTriggerId = defaultTriggerId;
	}
}
