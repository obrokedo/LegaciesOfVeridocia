package mb.fc.engine.message;

import mb.fc.game.battle.BattleResults;

/**
 * A message that indicates the results of a battle actions
 *
 * @author Broked
 *
 */
public class BattleResultsMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private BattleResults battleResults;

	public BattleResultsMessage(BattleResults battleResults)
	{
		super(MessageType.BATTLE_RESULTS);
		this.battleResults = battleResults;
	}

	public BattleResults getBattleResults() {
		return battleResults;
	}
}
