package mb.fc.utils;

import mb.fc.engine.CommRPG;
import mb.fc.game.exception.BadAnimationException;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class AnimationWrapper
{
	protected static float SCREEN_SCALE = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];

	protected SpriteAnims spriteAnims;
	protected Animation animation;
	protected int animationIndex;
	protected int animationDelta;
	protected boolean loops;
	protected Image weapon = null;

	protected AnimationWrapper(SpriteAnims spriteAnims)
	{
		this.spriteAnims = spriteAnims;
	}

	public AnimationWrapper(SpriteAnims spriteAnims, String animationName)
	{
		this.spriteAnims = spriteAnims;
		setAnimation(animationName, false);
	}

	public AnimationWrapper(SpriteAnims spriteAnims, String animationName, boolean loops)
	{
		this.spriteAnims = spriteAnims;
		setAnimation(animationName, loops);
	}

	public AnimationWrapper(SpriteAnims spriteAnims, String animationName, boolean loops, Image weapon)
	{
		this.spriteAnims = spriteAnims;
		this.weapon = weapon;
		setAnimation(animationName, loops);
	}

	public void setAnimation(String animationName, boolean loops)
	{
		this.animation = spriteAnims.getAnimation(animationName);
		if (animation == null)
			throw new BadAnimationException("No animation for the action: " + animationName + " could be found.");
		this.loops = loops;
		animationIndex = 0;
		animationDelta = 0;

	}

	public void resetCurrentAnimation()
	{
		animationIndex = 0;
		animationDelta = 0;
	}

	public boolean update(long delta)
	{
		animationDelta += delta;
		while (animationDelta >= animation.frames.get(animationIndex).delay)
		{
			animationDelta -= animation.frames.get(animationIndex).delay;
			if (animationIndex + 1 >= animation.frames.size())
			{
				if (loops)
					animationIndex = 0;
				else
				{
					animationDelta = animationDelta -= animation.frames.get(animationIndex).delay;
					return true;
				}
			}
			else
			{
				animationIndex++;
			}
		}
		return false;
	}

	public void drawAnimation(int x, int y, Graphics g)
	{
		drawAnimation(x, y, null, g);
	}

	public void drawAnimationIgnoreOffset(int x, int y, Graphics g)
	{
		if (animation != null)
		{
			for (AnimSprite as : animation.frames.get(animationIndex).sprites)
			{
				if (as.imageIndex != -1)
				{
					g.drawImage(getRotatedImageIfNeeded(spriteAnims.getImageAtIndex(as.imageIndex), as), x, y);
				}
				else
					drawWeapon(as, x, y, null, g);
			}
		}
	}

	public void drawAnimationPortrait(int x, int y, int ySecond, Graphics g)
	{
		boolean first = true;
		if (animation != null)
		{
			for (AnimSprite as : animation.frames.get(animationIndex).sprites)
			{
				if (as.imageIndex != -1)
				{
					if (first)
						g.drawImage(getRotatedImageIfNeeded(spriteAnims.getImageAtIndex(as.imageIndex), as), x, y);
					else
						g.drawImage(getRotatedImageIfNeeded(spriteAnims.getImageAtIndex(as.imageIndex), as), x, y + ySecond);
				}
				else
					drawWeapon(as, x, y, null, g);

				first = false;
			}
		}
	}

	public void drawAnimation(int x, int y, Color filter, Graphics g)
	{
		if (animation != null)
		{
			for (AnimSprite as : animation.frames.get(animationIndex).sprites)
			{
				if (as.imageIndex != -1)
				{
					if (filter == null)
						g.drawImage(getRotatedImageIfNeeded(spriteAnims.getImageAtIndex(as.imageIndex), as),
								x + as.x * SCREEN_SCALE, y + as.y * SCREEN_SCALE);
					else
						g.drawImage(getRotatedImageIfNeeded(spriteAnims.getImageAtIndex(as.imageIndex), as),
								x + as.x * SCREEN_SCALE, y + as.y * SCREEN_SCALE, filter);
				}
				else
					drawWeapon(as, x, y, filter, g);
			}
		}
	}

	public void drawAnimationNormalize(int x, int y, Color filter, Graphics g)
	{
		if (animation != null)
		{
			int xOff = Integer.MIN_VALUE;
			int yOff = Integer.MIN_VALUE;
			for (AnimSprite as : animation.frames.get(animationIndex).sprites)
			{
				if (as.imageIndex != -1)
				{
					if (xOff == Integer.MIN_VALUE)
					{
						xOff = -as.x;
						yOff = -as.y;
					}

					/*
					System.out.println("----");
					System.out.println(as.y);
					System.out.println(spriteAnims.getImageAtIndex(as.imageIndex).getHeight());
					*/

					if (filter == null)
					{
						if (as.y < 0)
							g.drawImage(getRotatedImageIfNeeded(spriteAnims.getImageAtIndex(as.imageIndex), as),
								x + (as.x + xOff) * SCREEN_SCALE, y + (as.y + yOff) * SCREEN_SCALE);
						else
							g.drawImage(getRotatedImageIfNeeded(spriteAnims.getImageAtIndex(as.imageIndex), as),
									x + (as.x + xOff) * SCREEN_SCALE, y + (as.y - 3 + yOff) * SCREEN_SCALE);
					}
					else
						g.drawImage(getRotatedImageIfNeeded(spriteAnims.getImageAtIndex(as.imageIndex), as),
								x + (as.x + xOff) * SCREEN_SCALE, y + (as.y + yOff) * SCREEN_SCALE, filter);
				}
				else
					drawWeapon(as, x, y, filter, g);
			}
		}
	}

	protected void drawWeapon(AnimSprite as, int x, int y, Color filter, Graphics g)
	{
		if (weapon != null)
		{
			if (filter == null)
				g.drawImage(getRotatedImageIfNeeded(weapon, as),
						x + as.x * SCREEN_SCALE, y + as.y * SCREEN_SCALE);
			else
				g.drawImage(getRotatedImageIfNeeded(weapon, as),
						x + as.x * SCREEN_SCALE, y + as.y * SCREEN_SCALE, filter);
		}
	}

	protected Image getRotatedImageIfNeeded(Image image, AnimSprite as)
	{
		Image im = image;
		if (as.angle != 0)
		{
			im = image.copy();
			im.rotate(as.angle);
		}
		return im;
	}

	public int getAnimationLength()
	{
		if (animation != null)
		{
			return animation.getAnimationLength();
		}
		else
			return -1;
	}

	public boolean hasAnimation(String animationName)
	{
		return this.spriteAnims.getAnimation(animationName) != null;
	}

	public Animation getCurrentAnimation()
	{
		return animation;
	}
}
