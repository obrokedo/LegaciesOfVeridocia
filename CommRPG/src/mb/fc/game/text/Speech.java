package mb.fc.game.text;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.menu.Portrait;

public class Speech
{
	private String message;
	private int[] requires;
	private int[] excludes;
	private int triggerId;
	private int heroPortrait;
	private int enemyPortrait;

	public Speech(String message, int[] requires, int[] excludes, int triggerId,
			int heroPortrait, int enemyPortrait) {
		super();
		this.message = message;
		this.requires = requires;
		this.excludes = excludes;
		this.triggerId = triggerId;
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

	public Portrait getPortrait(StateInfo stateInfo) {
		return Portrait.getPortrait(heroPortrait, enemyPortrait, stateInfo);
	}
}
