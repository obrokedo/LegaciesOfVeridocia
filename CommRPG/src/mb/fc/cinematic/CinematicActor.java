package mb.fc.cinematic;

import mb.fc.game.Camera;
import mb.fc.game.constants.Direction;
import mb.fc.game.sprite.AnimatedSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.utils.AnimSprite;
import mb.fc.utils.Animation;
import mb.fc.utils.SpriteAnims;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class CinematicActor 
{
	private static final int INDEFINITE_TIME = -1;
	
	public static final int SE_NONE = 0;
	public static final int SE_SHRINK = 1;
	public static final int SE_GROW = 2;
	public static final int SE_QUIVER = 3;
	public static final int SE_FALL_ON_FACE = 4;
	public static final int SE_LAY_ON_SIDE = 5;
	public static final int SE_FLASH = 6;
	public static final int SE_NOD = 7;
	public static final int SE_HEAD_SHAKE = 8;
	
	private SpriteAnims spriteAnims;
	private Animation currentAnim;
	private int imageIndex;
	private boolean animationLooping;
	private Direction facing;
	private long animUpdate = Long.MAX_VALUE;
	private long animDelta = 0;
	private boolean animHalting;
	private AnimatedSprite sprite;
	private boolean visible = true;
	
	// Moving location
	private float locX;
	private float locY;
	private int moveToLocX;
	private int moveToLocY;
	private boolean haltingMove;
	private float moveSpeed;
	private int movingDelta;
	private static final int MOVE_UPDATE = 30;
	private boolean loopMoving = false;
	private float startLoopX;
	private float startLoopY;
	private boolean forceFacingMove = false;
	
	// Rotate Params
	private int spinSpeed;
	private int spinDuration;
	private int spinDelta;
	private boolean spinning = false;
	
	private int specialEffectType;
	private int specialEffectUpdate;
	private int specialEffectDelta;
	private int specialEffectDuration;
	private float specialEffectCounter;				
	
	private Color flashColor = new Color(255, 255, 255);
	
	private boolean moving;		
	
	public CinematicActor(SpriteAnims spriteAnims, String initialAnimation, int x, int y, boolean visible)
	{
		this.facing = Direction.DOWN;
		this.spriteAnims = spriteAnims;
		currentAnim = this.spriteAnims.getAnimation(initialAnimation);
		animationLooping = true;
		this.locX = x;
		this.locY = y;
		this.visible = visible;
		System.out.println("VISIBLE " + visible + " " + spriteAnims);
	}
	
	public CinematicActor(AnimatedSprite sprite)
	{
		this.sprite = sprite;
	}
	
	public void render(Graphics graphics, Camera camera, FCGameContainer cont)
	{
		if (!visible)
			return;
		
		if (specialEffectType != SE_FALL_ON_FACE && specialEffectType != SE_LAY_ON_SIDE)
		{
			for (AnimSprite as : currentAnim.frames.get(imageIndex).sprites)
			{
				Image im = spriteAnims.getImageAtIndex(as.imageIndex);
				switch (specialEffectType)
				{
					case SE_NONE:
						graphics.drawImage(im, locX - camera.getLocationX() + cont.getDisplayPaddingX(), 
								locY - camera.getLocationY());
						break;
					case SE_GROW:
					case SE_SHRINK:
						Image scaled = im.getScaledCopy(specialEffectCounter);
						
						scaled.draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX() + im.getWidth() - scaled.getWidth(), 
								this.getLocY() - camera.getLocationY() + im.getHeight() - scaled.getHeight());
						break;
					case SE_QUIVER:
							graphics.drawImage(im, locX - camera.getLocationX() + cont.getDisplayPaddingX() + (specialEffectCounter % 2 == 0 ? 0 : -2 + specialEffectCounter), 
								locY - camera.getLocationY());
						break;
					case SE_FLASH:
						Image whiteIm = im.copy();
						
						// 1. bind the sprite sheet
						whiteIm.bind();

						// 2. change texture environment
						GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
								GL11.GL_TEXTURE_ENV_MODE, GL11.GL_ADD);

						// 3. start rendering the sprite sheet
						whiteIm.startUse();

						// 4. bind any colors, draw any sprites
						flashColor.bind();
						whiteIm.drawEmbedded(locX - camera.getLocationX() + cont.getDisplayPaddingX(), 
								locY - camera.getLocationY(), whiteIm.getWidth(), whiteIm.getHeight());

						// 5. stop rendering the sprite sheet
						whiteIm.endUse();

						// 6. reset the texture environment
						GL11.glTexEnvi(GL11.GL_TEXTURE_ENV,
								GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);
								
						/*
						graphics.drawImage(im, locX - camera.getLocationX() + cont.getDisplayPaddingX(), 
								locY - camera.getLocationY());
						graphics.drawImage(whiteIm, locX - camera.getLocationX() + cont.getDisplayPaddingX(), 
								locY - camera.getLocationY(), flashColor);
								*/
						break;
					case SE_NOD:
						im.getSubImage(0, 10, im.getWidth(), 14).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(), 
								this.getLocY() - camera.getLocationY() + 10);
						im.getSubImage(0, 0, im.getWidth(), 10).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(), 
								this.getLocY() - camera.getLocationY() + 1);
						break;
					case SE_HEAD_SHAKE:
						im.getSubImage(0, 10, im.getWidth(), 14).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(), 
								this.getLocY() - camera.getLocationY() + 10);
						
						switch ((int) specialEffectCounter % 4)
						{
							case 0:
							case 2:
								im.getSubImage(0, 0, im.getWidth(), 10).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(), 
									this.getLocY() - camera.getLocationY());
								break;
							case 1:
								im.getSubImage(0, 0, im.getWidth(), 10).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX() + 1, 
										this.getLocY() - camera.getLocationY());
								break;
							case 3:
								im.getSubImage(0, 0, im.getWidth(), 10).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX() - 1, 
										this.getLocY() - camera.getLocationY());
								break;
						}
						
						break;
				}
			}
		}
		else if (specialEffectType == SE_FALL_ON_FACE)
			renderFaceDown(graphics, camera, cont);
		else if (specialEffectType == SE_LAY_ON_SIDE)
			renderOnSide(graphics, camera, cont);
	}

	private void renderFaceDown(Graphics graphics, Camera camera, FCGameContainer cont)
	{
		for (AnimSprite as : spriteAnims.getAnimation("UnUp").frames.get(0).sprites)
		{
			Image im = spriteAnims.getImageAtIndex(as.imageIndex).getFlippedCopy(false, true);
			graphics.drawImage(im, locX - camera.getLocationX() + cont.getDisplayPaddingX(), 
					locY - camera.getLocationY());
		}
	}
	
	private void renderOnSide(Graphics graphics, Camera camera, FCGameContainer cont)
	{
		for (AnimSprite as : spriteAnims.getAnimation("UnDown").frames.get(0).sprites)
		{
			Image im = spriteAnims.getImageAtIndex(as.imageIndex).copy();
			im.rotate(90f);
			graphics.drawImage(im, locX - camera.getLocationX() + cont.getDisplayPaddingX(), 
					locY - camera.getLocationY());
		}
	}
	
	public void update(int delta, Cinematic cinematic) 
	{		
		animDelta += delta;
		while (animDelta > animUpdate)
		{
			animDelta -= animUpdate;
			
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
						this.animUpdate = Long.MAX_VALUE;
					}
				}
			}
			else
				imageIndex++;										
		}
		
		if (spinning)
		{
			spinDelta += delta;
			while (spinDelta > spinSpeed)
			{
				spinDelta -= spinSpeed;
				
				switch (facing)
				{
					case UP:
						setFacing(Direction.LEFT);
						break;
					case LEFT:
						setFacing(Direction.DOWN);
						break;
					case DOWN:
						setFacing(Direction.RIGHT);
						break;
					case RIGHT:
						setFacing(Direction.UP);
						break;
				}
				
				if (spinDuration != INDEFINITE_TIME)
				{
					spinDuration = Math.max(0, spinDuration - spinSpeed);
					if (spinDuration == 0)
						spinning = false;
				}
			}
		}		
		
		if (moving)
		{
			movingDelta += delta;
			while (movingDelta > MOVE_UPDATE)
			{
				movingDelta -= MOVE_UPDATE;
			
				if (locX != moveToLocX)
				{
					if (locX > moveToLocX)
						setLocX(getLocX() - Math.min(moveSpeed, locX - moveToLocX));
					else
						setLocX(getLocX() + Math.min(moveSpeed, moveToLocX - locX));
				}
				else if (locY != moveToLocY)
				{
					if (locY > moveToLocY)
						setLocY(getLocY() - Math.min(moveSpeed, locY - moveToLocY));
					else
						setLocY(getLocY() + Math.min(moveSpeed, moveToLocY - locY));
				}
				else
				{
					if (!loopMoving)
					{
						moving = false;
						this.forceFacingMove = false;
						
						if (haltingMove)
						{						
							haltingMove = false;
							cinematic.decreaseMoves();
						}
						this.animUpdate = Long.MAX_VALUE;
					}
					else
					{
						// Set the directions directly so the facing does not change
						this.locX = startLoopX;
						this.locY = startLoopY;
					}
				}
			}
		}
		
		if (specialEffectType != SE_NONE)
		{
			specialEffectDelta += delta;
			if (specialEffectDuration != INDEFINITE_TIME)
				specialEffectDuration -= delta;
			
			while (specialEffectDelta > specialEffectUpdate)
			{
				specialEffectDelta -= specialEffectUpdate;
				
				switch (specialEffectType)
				{
					case SE_SHRINK:
						specialEffectCounter -= .01f;
						if (specialEffectDuration != INDEFINITE_TIME && specialEffectCounter < .01f)
							specialEffectType = SE_NONE;
						break;
					case SE_GROW:
						specialEffectCounter += .01f;
						if (specialEffectDuration != INDEFINITE_TIME && specialEffectCounter > .99f)
							specialEffectType = SE_NONE;
						break;
					case SE_QUIVER:
						specialEffectCounter = (specialEffectCounter + 1) % 4;
						break;
					case SE_FLASH:
						if (specialEffectDuration == INDEFINITE_TIME || specialEffectDuration > 0)
						{														
							specialEffectCounter = (specialEffectCounter + 5) % 510;
							
							if (specialEffectCounter <= 255)
								flashColor.r = flashColor.g =  flashColor.b = specialEffectCounter / 255;
							else
								flashColor.r = flashColor.g =  flashColor.b = (510 - specialEffectCounter) / 255;
							break;
						}
						else
							specialEffectType = SE_NONE;
					case SE_NOD:
						specialEffectType = SE_NONE;
						break;
					case SE_HEAD_SHAKE:
						if (specialEffectCounter + 1 == 10)
							specialEffectType = SE_NONE;
						else
							specialEffectCounter++;
						break;
				}
			}
		}
	}
	
	public void shakeHead()
	{
		specialEffectType = SE_HEAD_SHAKE;
		specialEffectDelta = 0;
		specialEffectUpdate = 75;
		specialEffectCounter = 0;
	}
	
	public void nodHead()
	{
		specialEffectType = SE_NOD;
		specialEffectDelta = 0;
		specialEffectUpdate = 500;
		specialEffectCounter = 0;
	}
	
	public void fallOnFace()
	{
		specialEffectType = SE_FALL_ON_FACE;
	}
	
	public void layOnSide()
	{
		specialEffectType = SE_LAY_ON_SIDE;
	}
	
	public void flash(int speed, int duration)
	{
		flashColor.r = 0;
		flashColor.g = 0;
		flashColor.b = 0;
		specialEffectType = SE_FLASH;
		specialEffectDuration = duration;
		specialEffectDelta = 0;
		specialEffectUpdate = speed / 102;
		specialEffectCounter = 0;		
	}
	
	public void quiver()
	{
		specialEffectType = SE_QUIVER;
		specialEffectDuration = INDEFINITE_TIME;
		specialEffectDelta = 0;
		specialEffectUpdate = 25;
		specialEffectCounter = 0;
	}
	
	public void shrink(int duration)
	{
		specialEffectType = SE_SHRINK;
		specialEffectDuration = duration;
		specialEffectDelta = 0;
		specialEffectUpdate = duration / 100;
		specialEffectCounter = 1f;
	}
	
	public void grow(int duration)
	{
		specialEffectType = SE_GROW;
		specialEffectDuration = duration;
		specialEffectDelta = 0;
		specialEffectUpdate = duration / 100;
		specialEffectCounter = 0f;
	}
	
	public void stopSpecialEffect()
	{
		specialEffectType = SE_NONE;
	}
	
	private void setLocX(float locX) {
		if (!this.forceFacingMove)
		{
			// Moving right
			if (locX > this.locX)
				setFacing(Direction.RIGHT);
			// Moving left
			else if (locX < this.locX)
				setFacing(Direction.LEFT);
		}
		this.locX = locX;
	}

	private void setLocY(float locY) {
		if (!this.forceFacingMove)
		{
			// Moving down
			if (locY > this.locY)
				setFacing(Direction.DOWN);
			// Moving up
			else if (locY < this.locY)
				setFacing(Direction.UP);
		}
		this.locY = locY;
	}
	
	public void setSpinning(int spinSpeed, int spinDuration)
	{
		this.spinSpeed = spinSpeed;
		this.spinDuration = spinDuration;
		this.animUpdate = Long.MAX_VALUE;
		this.spinDelta = 0;
		spinning = true;
	}
	
	public void stopSpinning()
	{
		this.spinning = false;
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
	
	public void setFacing(int dir)
	{
		if (dir == 0)
			setFacing(Direction.UP);
		else if (dir == 1)
			setFacing(Direction.DOWN);
		else if (dir == 2)
			setFacing(Direction.LEFT);
		else if (dir == 3)
			setFacing(Direction.RIGHT);
	}
	
	public void setAnimation(String animation, int time, boolean halting, boolean looping)
	{
		this.animHalting = halting;
		this.animationLooping = looping;
		this.currentAnim = spriteAnims.getAnimation(animation);
		this.animDelta = 0;
		this.animUpdate = time / currentAnim.frames.size();		
	}
	
	public void moveToLocation(int moveToLocX, int moveToLocY, int speed, boolean haltingMove, int direction)
	{
		this.loopMoving = false;
		this.moveToLocX = moveToLocX;
		this.moveToLocY = moveToLocY;
		this.haltingMove = haltingMove;
		this.moveSpeed = speed;
		this.animDelta = 0;
		this.animUpdate = (long) (469.875 / moveSpeed);
		if (direction != -1)
		{
			this.setFacing(direction);
			this.forceFacingMove = true;
		}
		else
			this.forceFacingMove = false;
		moving = true;
	}
	
	public void loopMoveToLocation(int moveToLocX, int moveToLocY, int speed)
	{
		this.startLoopX = this.locX;
		this.startLoopY = this.locY;
		this.loopMoving = true;
		this.moveToLocX = moveToLocX;
		this.moveToLocY = moveToLocY;
		this.haltingMove = false;
		this.moveSpeed = speed;			
		this.animDelta = 0;
		this.animUpdate = (long) (469.875 / moveSpeed);
		this.forceFacingMove = false;
		moving = true;
	}
	
	public void stopLoopMove()
	{
		this.loopMoving = false;
	}

	public boolean isHaltingMove() {
		return haltingMove;
	}

	public boolean isAnimHalting() {
		return animHalting;
	}

	public float getLocX() {
		return locX;
	}

	public float getLocY() {
		return locY;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
