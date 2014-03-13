package mb.gl2.loading;

import org.newdawn.slick.state.BasicGameState;

public abstract class LoadableGameState extends BasicGameState 
{
	public abstract void stateLoaded(ResourceManager resourceManager);
}
