package mb.fc.cinematic;

import java.util.ArrayList;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.Camera;
import mb.fc.game.constants.Direction;
import mb.fc.game.sprite.AnimatedSprite;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.utils.AnimSprite;
import mb.fc.utils.Animation;
import mb.fc.utils.SpriteAnims;
import mb.jython.GlobalPythonFactory;
import mb.jython.JCinematicActor;

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
	public static final int SE_LAY_ON_SIDE_LEFT = 5;
	public static final int SE_FLASH = 6;
	public static final int SE_NOD = 7;
	public static final int SE_HEAD_SHAKE = 8;
	public static final int SE_LAY_ON_BACK = 9;
	public static final int SE_TREMBLE = 10;
	public static final int SE_LAY_ON_SIDE_RIGHT = 11;

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
	private boolean loopMoving = false;
	private float startLoopX;
	private float startLoopY;
	private boolean forceFacingMove = false;
	private boolean moveHorFirst;
	private boolean moveDiag;

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
	private Direction specialEffectDirection;

	private Color flashColor = new Color(255, 255, 255);

	private boolean moving;

	public JCinematicActor jCinematicActor;

	public CinematicActor(SpriteAnims spriteAnims, String initialAnimation, int x, int y, boolean visible)
	{
		jCinematicActor = GlobalPythonFactory.createJCinematicActor();
		this.facing = Direction.DOWN;
		this.spriteAnims = spriteAnims;
		currentAnim = this.spriteAnims.getAnimation(initialAnimation);
		this.setAnimation(initialAnimation, 1000, false, true);
		this.locX = x * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];
		this.locY = y * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];
		this.visible = visible;
	}

	public CinematicActor(AnimatedSprite sprite, StateInfo stateInfo)
	{
		jCinematicActor = GlobalPythonFactory.createJCinematicActor();
		sprite.setVisible(false);
		this.sprite = sprite;
		this.spriteAnims = sprite.getSpriteAnims();
		currentAnim = sprite.getCurrentAnim();
		this.facing = sprite.getFacing();
		this.locX = sprite.getLocX();
		this.locY = sprite.getLocY() - stateInfo.getResourceManager().getMap().getTileRenderHeight();
	}

	public void render(Graphics graphics, Camera camera, FCGameContainer cont, StateInfo stateInfo)
	{
		if (!visible)
			return;

		if (specialEffectType != SE_FALL_ON_FACE && specialEffectType != SE_LAY_ON_SIDE_RIGHT && specialEffectType != SE_LAY_ON_SIDE_LEFT)
		{
			for (AnimSprite as : currentAnim.frames.get(imageIndex).sprites)
			{
				Image im = spriteAnims.getImageAtIndex(as.imageIndex);
				switch (specialEffectType)
				{
					case SE_NONE:

						AnimatedSprite.drawShadow(spriteAnims.getImageAtIndex(as.imageIndex), (int) this.getLocX(), (int) this.getLocY(), cont.getDisplayPaddingX(), camera, false, stateInfo);

						graphics.drawImage(im, locX - camera.getLocationX() + cont.getDisplayPaddingX(),
								locY - camera.getLocationY());
						break;
					case SE_GROW:
					case SE_SHRINK:
						Image scaled = im.getScaledCopy(specialEffectCounter);

						AnimatedSprite.drawShadow(scaled, (int) (this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX() + (im.getWidth() - scaled.getWidth()) / 2),
								(int) (this.getLocY() - camera.getLocationY() + im.getHeight() - scaled.getHeight()), cont.getDisplayPaddingX(), camera, false, stateInfo);

						scaled.draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX() + (im.getWidth() - scaled.getWidth()) / 2,
								this.getLocY() - camera.getLocationY() + im.getHeight() - scaled.getHeight());
						break;
					case SE_QUIVER:

						AnimatedSprite.drawShadow(spriteAnims.getImageAtIndex(as.imageIndex), (int) (this.getLocX() + (specialEffectCounter % 2 == 0 ? 0 : (-2 + specialEffectCounter) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()])),
								(int) this.getLocY(), cont.getDisplayPaddingX(), camera, false, stateInfo);

						graphics.drawImage(im, locX - camera.getLocationX() + cont.getDisplayPaddingX() + (specialEffectCounter % 2 == 0 ? 0 : (-2 + specialEffectCounter) * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]),
								locY - camera.getLocationY());
						break;
					case SE_FLASH:
						AnimatedSprite.drawShadow(spriteAnims.getImageAtIndex(as.imageIndex), (int) this.getLocX(), (int) this.getLocY(), cont.getDisplayPaddingX(), camera, false, stateInfo);

						Image whiteIm = im;

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
						// Only draw the nod if we are on SE counter 1
						if (specialEffectCounter == 1)
						{
							AnimatedSprite.drawShadow(spriteAnims.getImageAtIndex(as.imageIndex), (int) this.getLocX(), (int) this.getLocY(), cont.getDisplayPaddingX(), camera, false, stateInfo);

							// This is the lower portion of the sprite
							im.getSubImage(0, 10 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], im.getWidth(), 14 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(),
									this.getLocY() - camera.getLocationY() + 10 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
							// This is the head of the sprite
							im.getSubImage(0, 0, im.getWidth(), 10 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(),
									this.getLocY() - camera.getLocationY() + 1 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
						}
						else
						{
							AnimatedSprite.drawShadow(spriteAnims.getImageAtIndex(as.imageIndex), (int) this.getLocX(), (int) this.getLocY(), cont.getDisplayPaddingX(), camera, false, stateInfo);

							graphics.drawImage(im, locX - camera.getLocationX() + cont.getDisplayPaddingX(),
									locY - camera.getLocationY());
						}
						break;
					case SE_HEAD_SHAKE:
						AnimatedSprite.drawShadow(spriteAnims.getImageAtIndex(as.imageIndex), (int) this.getLocX(), (int) this.getLocY(), cont.getDisplayPaddingX(), camera, false, stateInfo);

						im.getSubImage(0, 10 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
								im.getWidth(), 14 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(),
								this.getLocY() - camera.getLocationY() + 10 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);

						switch ((int) specialEffectCounter % 4)
						{
							case 0:
							case 2:
								im.getSubImage(0, 0, im.getWidth(), 10 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX(),
									this.getLocY() - camera.getLocationY());
								break;
							case 1:
								im.getSubImage(0, 0, im.getWidth(), 10 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX() + 1 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
										this.getLocY() - camera.getLocationY());
								break;
							case 3:
								im.getSubImage(0, 0, im.getWidth(), 10 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]).draw(this.getLocX() - camera.getLocationX() + cont.getDisplayPaddingX() - 1 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
										this.getLocY() - camera.getLocationY());
								break;
						}

						break;
					case SE_TREMBLE:
						int trembleVal = 4 - (int) Math.abs(specialEffectCounter - 4);
						if (trembleVal > 0)
						{
							im = spriteAnims.getImageAtIndex(as.imageIndex).getScaledCopy(
									spriteAnims.getImageAtIndex(as.imageIndex).getWidth() - trembleVal * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
									spriteAnims.getImageAtIndex(as.imageIndex).getHeight() - trembleVal * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
						}
						else
						{
							im = spriteAnims.getImageAtIndex(as.imageIndex);
							trembleVal = 0;
						}

						AnimatedSprite.drawShadow(im, (int) (this.getLocX() + (trembleVal * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]) / 2),
								(int) this.getLocY() + trembleVal * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], cont.getDisplayPaddingX(), camera, false, stateInfo);

						graphics.drawImage(im, locX - camera.getLocationX() + cont.getDisplayPaddingX() + (trembleVal * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]) / 2,
								locY - camera.getLocationY() + trembleVal * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()]);
						break;
				}
			}
		}
		else if (specialEffectType == SE_FALL_ON_FACE)
			renderFaceDown(graphics, camera, cont);
		else if (specialEffectType == SE_LAY_ON_SIDE_RIGHT)
			renderOnSideRight(graphics, camera, cont);
		else if (specialEffectType == SE_LAY_ON_SIDE_LEFT)
			renderOnSideLeft(graphics, camera, cont);
		else if (specialEffectType == SE_LAY_ON_BACK)
			renderOnBack(graphics, camera, cont);
	}

	private void renderFaceDown(Graphics graphics, Camera camera, FCGameContainer cont)
	{
		renderOnDirection(spriteAnims.getAnimation("UnUp").frames.get(0).sprites, graphics, camera, cont);
	}

	private void renderOnBack(Graphics graphics, Camera camera, FCGameContainer cont)
	{
		renderOnDirection(spriteAnims.getAnimation("UnDown").frames.get(0).sprites, graphics, camera, cont);
	}

	private void renderOnSideLeft(Graphics graphics, Camera camera, FCGameContainer cont)
	{
		renderOnDirection(spriteAnims.getAnimation("UnLeft").frames.get(0).sprites, graphics, camera, cont);
	}

	private void renderOnSideRight(Graphics graphics, Camera camera, FCGameContainer cont)
	{
		renderOnDirection(spriteAnims.getAnimation("UnRight").frames.get(0).sprites, graphics, camera, cont);
	}

	private void renderOnDirection(ArrayList<AnimSprite> sprites, Graphics graphics, Camera camera, FCGameContainer cont)
	{
		for (AnimSprite as : sprites)
		{
			Image im = spriteAnims.getImageAtIndex(as.imageIndex).copy();
			switch (specialEffectDirection)
			{
				case UP:
					break;
				case RIGHT:
					im.rotate(90f);
					break;
				case DOWN:
					im.rotate(180f);
					break;
				case LEFT:
					im.rotate(270f);
					break;

			}

			graphics.drawImage(im, locX - camera.getLocationX() + cont.getDisplayPaddingX(),
					locY - camera.getLocationY());
		}
	}

	private boolean moveHorz()
	{
		if (locX != moveToLocX)
		{
			if (locX > moveToLocX)
				setLocX(getLocX() - Math.min(moveSpeed, locX - moveToLocX));
			else
				setLocX(getLocX() + Math.min(moveSpeed, moveToLocX - locX));
			return true;
		}
		else
			return false;
	}

	private boolean moveVert()
	{
		if (locY != moveToLocY)
		{
			if (locY > moveToLocY)
				setLocY(getLocY() - Math.min(moveSpeed, locY - moveToLocY));
			else
				setLocY(getLocY() + Math.min(moveSpeed, moveToLocY - locY));
			return true;
		}
		else
			return false;
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
			while (movingDelta > jCinematicActor.getMoveUpdate())
			{
				movingDelta -= jCinematicActor.getMoveUpdate();
				boolean moved = false;

				if (moveDiag)
				{
					if (moveVert())
						moved = true;
					if (moveHorz())
						moved = true;
				}
				else
				{
					if (moveHorFirst)
						moved = moveHorz();

					if (!moved)
						moved = moveVert();

					if (!moveHorFirst && !moved)
						moved = moveHorz();
				}


				if (!moved)
				{
					if (!loopMoving)
					{
						moving = false;
						this.forceFacingMove = false;

						if (haltingMove)
						{
							System.out.println("STOP HALTING");
							haltingMove = false;
							cinematic.decreaseMoves();
						}
						this.animUpdate = jCinematicActor.getAnimUpdateAfterSE();
						// this.animUpdate = Long.MAX_VALUE;
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
			{
				specialEffectDuration = Math.max(0, specialEffectDuration - delta);
				// if (specialEffectDuration == IN)
			}

			while (specialEffectDelta > specialEffectUpdate && specialEffectType != SE_NONE)
			{
				specialEffectDelta -= specialEffectUpdate;

				switch (specialEffectType)
				{
					case SE_SHRINK:
						specialEffectCounter -= .01f;
						if (specialEffectDuration == 0 && specialEffectCounter < .01f)
							specialEffectType = SE_NONE;
						break;
					case SE_GROW:
						specialEffectCounter += .01f;
						if (specialEffectDuration == 0 && specialEffectCounter > .99f)
							specialEffectType = SE_NONE;
						break;
					case SE_QUIVER:
						specialEffectCounter = (specialEffectCounter + 1) % 4;
						break;
					case SE_TREMBLE:
						specialEffectCounter = (specialEffectCounter + 1) % 19;
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
						break;
					case SE_NOD:
						specialEffectCounter++;
						if (specialEffectCounter == 3)
						{
							specialEffectType = SE_NONE;
							animUpdate = jCinematicActor.getAnimUpdateAfterSE();
						}
						break;
					case SE_HEAD_SHAKE:
						if (specialEffectCounter + 1 == 10)
						{
							specialEffectType = SE_NONE;
							animUpdate = jCinematicActor.getAnimUpdateAfterSE();
						}
						else
							specialEffectCounter++;
						break;
				}
			}
		}
	}

	public void shakeHead(int speed)
	{
		specialEffectType = SE_HEAD_SHAKE;
		specialEffectDelta = 0;
		specialEffectUpdate = speed / 10;
		specialEffectCounter = 0;
		animUpdate = Long.MAX_VALUE;
	}

	public void nodHead()
	{
		specialEffectType = SE_NOD;
		specialEffectCounter = 0;
		specialEffectDelta = 0;
		specialEffectUpdate = jCinematicActor.getNodHeadDuration();
		specialEffectCounter = 0;
		animUpdate = Long.MAX_VALUE;
	}

	public void fallOnFace(Direction dir)
	{
		specialEffectType = SE_FALL_ON_FACE;
		specialEffectDuration = -1;
		specialEffectUpdate = 500;
		specialEffectDirection = dir;
		animUpdate = Long.MAX_VALUE;
	}

	public void layOnBack(Direction dir)
	{
		specialEffectType = SE_LAY_ON_BACK;
		specialEffectDuration = -1;
		specialEffectUpdate = 500;
		specialEffectDirection = dir;
		animUpdate = Long.MAX_VALUE;
	}

	public void layOnSideRight(Direction dir)
	{
		specialEffectType = SE_LAY_ON_SIDE_RIGHT;
		specialEffectDuration = -1;
		specialEffectUpdate = 500;
		specialEffectDirection = dir;
		animUpdate = Long.MAX_VALUE;
	}

	public void layOnSideLeft(Direction dir)
	{
		specialEffectType = SE_LAY_ON_SIDE_LEFT;
		specialEffectDuration = -1;
		specialEffectUpdate = 500;
		specialEffectDirection = dir;
		animUpdate = Long.MAX_VALUE;
	}

	public void flash(int speed, int duration)
	{
		flashColor.r = 0;
		flashColor.g = 0;
		flashColor.b = 0;
		specialEffectType = SE_FLASH;
		specialEffectDuration = duration;
		specialEffectDelta = 0;
		specialEffectUpdate = Math.max(1, speed / 102);
		specialEffectCounter = 0;
		animUpdate = Long.MAX_VALUE;
	}

	public void quiver()
	{
		specialEffectType = SE_QUIVER;
		specialEffectDuration = INDEFINITE_TIME;
		specialEffectDelta = 0;
		specialEffectUpdate = jCinematicActor.getQuiverUpdate();
		specialEffectCounter = 0;
		animUpdate = Long.MAX_VALUE;
	}

	public void tremble()
	{
		specialEffectType = SE_TREMBLE;
		specialEffectDuration = INDEFINITE_TIME;
		specialEffectDelta = 0;
		System.out.println("TREMBLE " + jCinematicActor.getTrembleUpdate());
		specialEffectUpdate = jCinematicActor.getTrembleUpdate();
		specialEffectCounter = 0;
		animUpdate = Long.MAX_VALUE;
	}

	public void shrink(int duration)
	{
		specialEffectType = SE_SHRINK;
		specialEffectDuration = duration;
		specialEffectDelta = 0;
		specialEffectUpdate = duration / 100;
		specialEffectCounter = 1f;
		animUpdate = Long.MAX_VALUE;
	}

	public void grow(int duration)
	{
		specialEffectType = SE_GROW;
		specialEffectDuration = duration;
		specialEffectDelta = 0;
		specialEffectUpdate = duration / 100;
		specialEffectCounter = 0f;
		animUpdate = Long.MAX_VALUE;
	}

	public void stopAnimation()
	{
		animUpdate = Long.MAX_VALUE;
	}

	public void stopSpecialEffect()
	{
		specialEffectType = SE_NONE;
		animUpdate = jCinematicActor.getAnimUpdateAfterSE();
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

	public void moveToLocation(int moveToLocX, int moveToLocY, float speed, boolean haltingMove, int direction, boolean moveHorFirst, boolean moveDiag)
	{
		this.loopMoving = false;
		this.moveToLocX = moveToLocX * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];
		this.moveToLocY = moveToLocY * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];
		this.haltingMove = haltingMove;
		this.moveSpeed = speed * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];
		this.movingDelta = 0;
		this.animDelta = 0;
		this.animUpdate = (long) jCinematicActor.getAnimSpeedForMoveSpeed(speed);
		this.moveHorFirst = moveHorFirst;
		this.moveDiag = moveDiag;
		if (direction != -1)
		{
			this.setFacing(direction);
			this.forceFacingMove = true;
		}
		else
			this.forceFacingMove = false;
		moving = true;
	}

	public void loopMoveToLocation(int moveToLocX, int moveToLocY, float speed)
	{
		this.startLoopX = this.locX;
		this.startLoopY = this.locY;
		moveToLocation(moveToLocX, moveToLocY, speed, haltingMove, -1, false, false);
		this.loopMoving = true;
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

	public AnimatedSprite getSprite() {
		return sprite;
	}

	public void resetSprite(StateInfo stateInfo)
	{
		if (sprite != null)
		{
			sprite.setLocX(locX);
			sprite.setLocY(locY + stateInfo.getResourceManager().getMap().getTileRenderHeight());
			sprite.setFacing(facing);
			sprite.setVisible(true);
		}
	}
}
