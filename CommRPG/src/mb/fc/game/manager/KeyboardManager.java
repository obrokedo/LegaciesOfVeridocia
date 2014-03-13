package mb.fc.game.manager;

import mb.fc.engine.message.Message;
import mb.fc.game.listener.KeyboardListener;

public class KeyboardManager extends Manager
{
	@Override
	public void initialize() {
		
	}
	
	public void update()
	{
		KeyboardListener kl = stateInfo.getKeyboardListener();
		
		if (kl != null)
		{
			if (System.currentTimeMillis() > stateInfo.getInputDelay())
				if (kl.handleKeyboardInput(stateInfo.getInput(), stateInfo))
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
		}
	}

	@Override
	public void recieveMessage(Message message) {
		
	}
}
