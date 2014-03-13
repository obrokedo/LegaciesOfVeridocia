package mb.fc.engine.message;

import mb.fc.game.battle.BattleResults;

public class BattleResultsMessage extends Message
{
	private static final long serialVersionUID = 1L;
	
	private BattleResults battleResults;
	
	public BattleResultsMessage(BattleResults battleResults) 
	{
		super(Message.MESSAGE_BATTLE_RESULTS);
		this.battleResults = battleResults;
	}

	public BattleResults getBattleResults() {
		return battleResults;
	}
}
