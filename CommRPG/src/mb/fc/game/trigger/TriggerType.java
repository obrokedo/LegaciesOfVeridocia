package mb.fc.game.trigger;

import mb.fc.engine.state.StateInfo;

public abstract class TriggerType 
{
	public abstract boolean perform(StateInfo stateInfo);
}
