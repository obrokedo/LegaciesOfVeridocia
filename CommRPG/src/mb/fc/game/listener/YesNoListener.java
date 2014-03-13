package mb.fc.game.listener;

import mb.fc.engine.state.StateInfo;

public interface YesNoListener 
{
	public boolean valueSelected(StateInfo stateInfo, boolean value);
}
