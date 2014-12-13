package mb.fc.utils;

import mb.fc.engine.CommRPG;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class AnimationWrapper
{
	private static float SCREEN_SCALE = CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()];

	private SpriteAnims spriteAnims;
	private Animation animation;
	private int animationIndex;
	private int animationDelta;
	private boolean loops;

	public AnimationWrapper(SpriteAnims spriteAnims)
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

	public void setAnimation(String animationName, boolean loops)
	{
		this.animation = spriteAnims.getAnimation(animationName);
		this.loops = loops;
		animationIndex = 0;
		animationDelta = 0;

	}

	public boolean udpate(int delta)
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
				{
					// 30, 4
					g.setColor(Color.yellow);
					g.fillRect(x + as.x * SCREEN_SCALE, y + as.y * SCREEN_SCALE,
							30 * SCREEN_SCALE, 4 * SCREEN_SCALE);
				}
			}
		}
	}

	private Image getRotatedImageIfNeeded(Image image, AnimSprite as)
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
