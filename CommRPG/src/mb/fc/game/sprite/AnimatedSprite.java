package mb.fc.game.sprite;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.constants.Direction;
import mb.fc.game.move.MovingSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.utils.AnimSprite;
import mb.fc.utils.Animation;
import mb.fc.utils.SpriteAnims;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class AnimatedSprite extends Sprite
{
	private static final long serialVersionUID = 1L;

	public final transient static Color SHADOW_COLOR = new Color(0, 0, 0, 120);
	protected final static int SHADOW_OFFSET = 8;

	protected transient int imageIndex;
	protected transient int animationDelay = 0;
	protected transient SpriteAnims spriteAnims;

	protected transient Animation currentAnim;
	protected String imageName;
	private Direction facing;
	private int animationUpdate = 10;

	public AnimatedSprite(int locX, int locY, String imageName, int id) {
		super(locX, locY, id);
		this.imageName = imageName;
	}

	public Image getCurrentImage()
	{
		return spriteAnims.getImageAtIndex(currentAnim.frames.get(imageIndex).sprites.get(0).imageIndex);
	}

	@Override
	public void render(Camera camera, Graphics graphics, FCGameContainer cont) {
		for (AnimSprite as : currentAnim.frames.get(imageIndex).sprites)
		{
			AnimatedSprite.drawShadow(spriteAnims.getImageAtIndex(as.imageIndex), this.getLocX(), this.getLocY(), cont.getDisplayPaddingX(), camera, true);

			graphics.drawImage(spriteAnims.getImageAtIndex(as.imageIndex), this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(),
					this.getLocY() - camera.getLocationY() - stateInfo.getResourceManager().getMap().getTileEffectiveHeight() / 2);
		}
	}

	public static void drawShadow(Image originalIm, int locX, int locY, int displayPadding, Camera camera, boolean tileOffset)
	{
		drawShadow(originalIm, locX, locY, displayPadding, camera, tileOffset, stateInfo);
	}

	public static void drawShadow(Image originalIm, int locX, int locY, int displayPadding, Camera camera, boolean tileOffset, StateInfo stateInfo)
	{
		Image i = (originalIm).getScaledCopy(originalIm.getWidth(), (int) (originalIm.getHeight() * .65));
		i.drawSheared((int) (locX - camera.getLocationX() + displayPadding - CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 13 * (1.0 * originalIm.getHeight() / stateInfo.getTileHeight())),
				locY - camera.getLocationY() - (tileOffset ? stateInfo.getResourceManager().getMap().getTileEffectiveHeight() / 2 : 0) + originalIm.getHeight() -
					i.getHeight(), (int) (CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 13 * (1.0 * originalIm.getHeight() / stateInfo.getTileHeight())),
				0, SHADOW_COLOR);
	}

	@Override
	public void initializeSprite(StateInfo stateInfo) {
		super.initializeSprite(stateInfo);

		imageIndex = 0;
		spriteAnims = stateInfo.getResourceManager().getSpriteAnimations().get(imageName);
		currentAnim = spriteAnims.getAnimation("UnDown");
		facing = Direction.DOWN;
		animationUpdate = MovingSprite.STAND_ANIMATION_SPEED;
	}

	@Override
	public void update()
	{
		animationDelay++;
		if (animationDelay >= animationUpdate)
		{

			if (imageIndex % 2 == 1)
				imageIndex--;
			else
				imageIndex++;

			animationDelay = 0;
		}
	}

	@Override
	public void setLocX(float locX) {
		// Moving right
		if (locX > this.getLocX())
			setFacing(Direction.RIGHT);
		// Moving left
		else if (locX < this.getLocX())
			setFacing(Direction.LEFT);
		super.setLocX(locX);
	}

	@Override
	public void setLocY(float locY) {
		// Moving down
		if (locY > this.getLocY())
			setFacing(Direction.DOWN);
		// Moving up
		else if (locY < this.getLocY())
			setFacing(Direction.UP);
		super.setLocY(locY);
	}

	public void setFacing(Direction dir)
	{
		switch (dir)
		{
			case UP:
				currentAnim = spriteAnims.getAnimation("UnUp");
				break;
			case DOWN:
				currentAnim = spriteAnims.getAnimation("UnDown");
				break;
			case LEFT:
				currentAnim = spriteAnims.getAnimation("UnLeft");
				break;
			case RIGHT:
				currentAnim = spriteAnims.getAnimation("UnRight");
				break;
		}
		facing = dir;
	}

	/**
	 * Sets the location of the sprite
	 *
	 * @param locX
	 * @param locY
	 */
	public void setLocation(int locX, int locY)
	{
		super.setLocX(locX);
		super.setLocY(locY);
	}

	public Direction getFacing() {
		return facing;
	}

	public void setAnimationUpdate(int animationUpdate) {
		this.animationUpdate = animationUpdate;
	}

	public SpriteAnims getSpriteAnims() {
		return spriteAnims;
	}

	public Animation getCurrentAnim() {
		return currentAnim;
	}

	public void doneMoving() {

	}
}
