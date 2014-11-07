package mb.fc.engine.message;

/**
 * A message that indicates that the current battle has custom battle conditions
 *
 * @author Broked
 *
 */
public class BattleCondMessage extends Message
{
	private static final long serialVersionUID = 1L;

	private int[] leaderIds;
	private boolean killAllLeaders;

	public BattleCondMessage(int[] leaderIds, boolean killAllLeaders) {
		super(Message.MESSAGE_BATTLE_COND);
		this.leaderIds = leaderIds;
		this.killAllLeaders = killAllLeaders;
	}

	public int[] getLeaderIds() {
		return leaderIds;
	}

	public boolean isKillAllLeaders() {
		return killAllLeaders;
	}
}
