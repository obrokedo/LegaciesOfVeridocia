package mb.fc.game.text;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.resource.EnemyResource;
import mb.fc.game.resource.HeroResource;
import mb.fc.game.sprite.CombatSprite;

public class Speech
{
	private String message;
	private int[] requires;
	private int[] excludes;
	private int triggerId;
	private int portraitId;
	private int heroPortrait;
	private int enemyPortrait;

	public Speech(String message, int[] requires, int[] excludes, int triggerId, int portraitId,
			int heroPortrait, int enemyPortrait) {
		super();
		this.message = message;
		this.requires = requires;
		this.excludes = excludes;
		this.triggerId = triggerId;
		this.portraitId = portraitId;
		this.heroPortrait = heroPortrait;
		this.enemyPortrait = enemyPortrait;
	}

	public String getMessage() {
		return message;
	}

	public int[] getRequires() {
		return requires;
	}

	public int[] getExcludes() {
		return excludes;
	}

	public int getTriggerId() {
		return triggerId;
	}

	public int getPortraitId(StateInfo stateInfo) {
		if (portraitId != -1)
			return portraitId;
		else if (enemyPortrait != -1)
			return EnemyResource.getPortraitIndex(enemyPortrait);
		else if (heroPortrait != -1)
		{
			for (CombatSprite cs : stateInfo.getPsi().getClientProfile().getHeroes())
			{
				if (cs.getHeroProgression().getHeroID() == heroPortrait)
				{
					return cs.getCurrentProgression().getPortraitIndex();
				}
			}

			return HeroResource.getPortraitIndex(heroPortrait);
		}
		return -1;
	}
}
