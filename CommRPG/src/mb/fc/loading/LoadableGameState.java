package mb.fc.loading;

import org.newdawn.slick.state.BasicGameState;

public abstract class LoadableGameState extends BasicGameState
{
	protected boolean loading = false;

	public abstract void stateLoaded(FCResourceManager resourceManager);

	public abstract void initAfterLoad();

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}


}
