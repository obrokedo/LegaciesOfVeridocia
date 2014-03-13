package mb.fc.game.manager;

import mb.fc.engine.message.Message;
import mb.fc.engine.state.StateInfo;

public abstract class Manager 
{
	public StateInfo stateInfo;
	
	public void initializeSystem(StateInfo stateInfo)
	{
		this.stateInfo = stateInfo;
		this.initialize();
	}
	
	public abstract void initialize();
	
	public abstract void recieveMessage(Message message);
}
