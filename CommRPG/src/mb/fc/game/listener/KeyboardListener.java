package mb.fc.game.listener;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.input.FCInput;

public interface KeyboardListener 
{
	public boolean handleKeyboardInput(FCInput input, StateInfo stateInfo);
}
