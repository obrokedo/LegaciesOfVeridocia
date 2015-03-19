package mb.fc.game.menu;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.resource.EnemyResource;
import mb.fc.game.resource.HeroResource;
import mb.fc.game.sprite.CombatSprite;
import mb.fc.utils.AnimationWrapper;
import mb.fc.utils.SpriteAnims;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Portrait
{
	private AnimationWrapper blinkAnim, talkAnim, currentAnim;

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
		if ((promoted || spriteAnim.getAnimation("UnPort") == null) && spriteAnim.getAnimation("ProPort") != null)
		{
			Portrait p = new Portrait();
			p.blinkAnim = new AnimationWrapper(spriteAnim, "ProPort", true);
			p.talkAnim = new AnimationWrapper(spriteAnim, "ProPortTalk", true);
			p.currentAnim = p.blinkAnim;
			return p;
		}
		else if (spriteAnim.getAnimation("UnPort") != null)
		{
			Portrait p = new Portrait();
			p.blinkAnim = new AnimationWrapper(spriteAnim, "UnPort", true);
			p.talkAnim = new AnimationWrapper(spriteAnim, "UnPortTalk", true);
			p.currentAnim = p.blinkAnim;
			return p;
		}

		return null;
	}

	public void render(int x, int y, Graphics graphics)
	{
		Panel.drawPanelBox(x, y, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 62, CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 78, graphics, Color.black);
		currentAnim.drawAnimationIgnoreOffset(x + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7, y + CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 7, graphics);
	}

	public void update(long delta)
	{
		delta = (int) (delta * (CommRPG.RANDOM.nextFloat() * 2));
		currentAnim.update(delta);
	}

	public void setTalking()
	{
		currentAnim = talkAnim;
		blinkAnim.resetCurrentAnimation();
	}

	public void setBlinking()
	{
		currentAnim = blinkAnim;
		talkAnim.resetCurrentAnimation();
	}
}
