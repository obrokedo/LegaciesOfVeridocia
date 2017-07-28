package mb.fc.game.trigger;

import mb.fc.engine.state.StateInfo;

public interface Triggerable 
{
	public boolean perform(StateInfo stateInfo);
}
