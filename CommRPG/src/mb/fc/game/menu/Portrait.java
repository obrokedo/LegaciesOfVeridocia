package mb.fc.game.menu;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.resource.EnemyResource;
import mb.fc.game.resource.HeroResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.utils.AnimFrame;
import mb.fc.utils.AnimationWrapper;
import mb.fc.utils.SpriteAnims;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Portrait
{
	private AnimationWrapper idleAnim, blinkAnim, talkAnim;
	private int topHeight;
	private boolean isBlinking = false, isTalking = false;
	int blinkCounter = 0;

	public static Portrait getPortrait(int heroId, int enemyId, StateInfo stateInfo)
	{
		SpriteAnims sa = null;

		if (enemyId != -1)
		{
			sa = stateInfo.getResourceManager().getSpriteAnimations().get(EnemyResource.getAnimation(enemyId));
			return getPortrait(sa, false);
		}
		else if (heroId != -1)
		{
			for (CombatSprite cs : stateInfo.getPsi().getClientProfile().getHeroes())
			{
				if (cs.getHeroProgression().getHeroID() == heroId)
				{
					return getPortrait(cs);
				}
			}

			sa = stateInfo.getResourceManager().getSpriteAnimations().get(HeroResource.getAnimation(enemyId));
			return getPortrait(sa, false);
		}

		return null;
	}

	public static Portrait getPortrait(CombatSprite combatSprite)
	{
		return getPortrait(combatSprite.getSpriteAnims(), combatSprite.isPromoted());
	}

	private static Portrait getPortrait(SpriteAnims spriteAnim, boolean promoted)
	{
		if ((promoted || spriteAnim.getAnimation("UnPortIdle") == null) && spriteAnim.getAnimation("ProPortIdle") != null)
		{
			Portrait p = new Portrait();

			p.idleAnim = new AnimationWrapper(spriteAnim, "ProPortIdle", true);
			p.blinkAnim = new AnimationWrapper(spriteAnim, "ProPortBlink", false);
			p.talkAnim = new AnimationWrapper(spriteAnim, "ProPortTalk", true);

			AnimFrame af = p.blinkAnim.getCurrentAnimation().frames.get(0);
			p.topHeight = spriteAnim.images.get(af.sprites.get(0).imageIndex).getHeight();
			return p;
		}
		else if (spriteAnim.getAnimation("UnPortIdle") != null)
		{
			Portrait p = new Portrait();
			p.idleAnim = new AnimationWrapper(spriteAnim, "UnPortIdle", true);
			p.blinkAnim = new AnimationWrapper(spriteAnim, "UnPortBlink", false);
			p.talkAnim = new AnimationWrapper(spriteAnim, "UnPortTalk", true);

			AnimFrame af = p.blinkAnim.getCurrentAnimation().frames.get(0);
			p.topHeight = spriteAnim.images.get(af.sprites.get(0).imageIndex).getHeight();

			return p;
		}

		return null;
	}

	public void render(int x, int y, Graphics graphics)
	{
		Panel.drawPanelBox(x, y, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 62, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 78, graphics, Color.black);
		idleAnim.drawAnimationPortrait(x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7,
				y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7, topHeight, graphics);

		if (isBlinking)
			blinkAnim.drawAnimationIgnoreOffset(x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7,
				y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7, graphics);

		if (isTalking)
			talkAnim.drawAnimationIgnoreOffset(x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7,
					y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7 + topHeight, graphics);
	}

	public void update(long delta)
	{
		delta = (int) (delta * (CommRPG.RANDOM.nextFloat() * 2));
		if (isBlinking)
		{
			if (blinkAnim.update(delta))
			{
				isBlinking = false;
				blinkAnim.resetCurrentAnimation();
			}
		}
		else
		{
			blinkCounter += delta;
			if (blinkCounter >= 1500)
			{
				isBlinking = true;
				blinkCounter = 0;
			}
		}

		if (isTalking)
			talkAnim.update(delta);
	}

	public void setTalking(boolean talking)
	{
		isTalking = talking;
		talkAnim.resetCurrentAnimation();
	}
}
