package mb.fc.game.listener;

import mb.fc.engine.state.StateInfo;

public interface MenuListener
{
	public void valueSelected(StateInfo stateInfo, Object value);

	public void menuClosed();
}
