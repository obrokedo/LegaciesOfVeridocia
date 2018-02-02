package mb.fc.game.trigger;

import mb.fc.engine.state.StateInfo;

public interface Triggerable 
{
	/**
	 * Perform the triggerable action
	 * 
	 * @param stateInfo
	 * @return true if this triggereable action completed successfully
	 */
	public boolean perform(StateInfo stateInfo);
}
