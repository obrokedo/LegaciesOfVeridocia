package mb.fc.cinematic;

import mb.fc.game.Camera;
import mb.fc.game.constants.Direction;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.utils.AnimSprite;
import mb.fc.utils.Animation;
import mb.fc.utils.SpriteAnims;

import org.newdawn.slick.Graphics;

public class CinematicActor 
{
	private String name;
	private SpriteAnims spriteAnims;
	private Animation currentAnim;
	private int imageIndex;
	private boolean animationLooping;
	private Direction facing;
	private long animTimer;
	private boolean animHalting;	
	
	// Moving location
	private int locX;
	private int locY;
	private int moveToLocX;
	private int moveToLocY;
	private boolean haltingMove;
	
	private boolean moving;	
	
	public CinematicActor(String actorName, SpriteAnims spriteAnims, String initialAnimation)
	{
		this.name = actorName;
		this.spriteAnims = spriteAnims;
		this.spriteAnims.getAnimation(initialAnimation);
		animationLooping = true;
	}
	
	public void render(Graphics graphics, Camera camera, FCGameContainer cont)
	{
		for (AnimSprite as : currentAnim.frames.get(imageIndex).sprites)
		{
			graphics.drawImage(spriteAnims.getImageAtIndex(as.imageIndex), locX - camera.getLocationX() + cont.getDisplayPaddingX(), 
					locY - camera.getLocationY());
		}
	}
	
	public void update(int delta, Cinematic cinematic) 
	{		
		animTimer -= delta;
		
		if (animTimer <= 0)
		{
			if (imageIndex % currentAnim.frames.size() == currentAnim.frames.size() - 1)
			{
				imageIndex = 0;

				// If this isn't a looping animation then
				if (!animationLooping)
				{
					setFacing(facing);
					if (animHalting)
					{
						animHalting = false;
						cinematic.decreaseAnims();
					}
				}
			}
			else
				imageIndex++;
			
			if (moving)
			{
				if (locX != moveToLocX)
				{
					if (locX > moveToLocX)
						locX -= 8;
					else
						locX += 8;
				}
				else if (locY != moveToLocY)
				{
					if (locY > moveToLocY)
						locY -= 8;
					else
						locY += 8;
				}
				else
				{
					moving = false;
					
					if (haltingMove)
					{
						haltingMove = false;
						cinematic.decreaseMoves();
					}
				}
			}
			
			animTimer += 100;
		}	
	}
	
	public void setLocX(int locX) {
		// Moving right
		if (locX > this.locX)
			setFacing(Direction.RIGHT);
		// Moving left
		else if (locX < this.locX)
			setFacing(Direction.LEFT);
		this.locX = locX;
	}

	public void setLocY(int locY) {
		// Moving down
		if (locY > this.locY)
			setFacing(Direction.DOWN);
		// Moving up
		else if (locY < this.locY)
			setFacing(Direction.UP);
		this.locY = locY;
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
	
	public void moveToLocation(int moveToLocX, int moveToLocY, boolean haltingMove)
	{
		this.moveToLocX = moveToLocX;
		this.moveToLocY = moveToLocY;
		this.haltingMove = haltingMove;
		animTimer = 100;
		moving = true;
	}

	public boolean isHaltingMove() {
		return haltingMove;
	}

	public boolean isAnimHalting() {
		return animHalting;
	}
}
