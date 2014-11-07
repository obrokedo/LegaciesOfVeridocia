package mb.fc.loading;

import org.newdawn.slick.state.BasicGameState;

public abstract class LoadableGameState extends BasicGameState
{
	public abstract void stateLoaded(FCResourceManager resourceManager);
}
