package mb.fc.game.manager;

import mb.fc.cinematic.Cinematic;
import mb.fc.engine.message.IntMessage;
import mb.fc.engine.message.Message;

import org.newdawn.slick.Graphics;

public class CinematicManager extends Manager
{
	private Cinematic cinematic;
	
	@Override
	public void initialize() {
		
	}
	
	public void update(int delta)
	{
		if (cinematic != null && cinematic.update(delta, stateInfo.getCamera(), stateInfo.getInput(), 
				stateInfo.getGc(), stateInfo.getCurrentMap(), stateInfo))
		{
			cinematic = null;
			stateInfo.getCamera().centerOnSprite(stateInfo.getCurrentSprite(), stateInfo.getCurrentMap());
		}
			
	}
	
	public void render(Graphics g)
	{
		if (cinematic != null)
			cinematic.render(g, stateInfo.getCamera(), stateInfo.getGc(), stateInfo);
	}

	public void renderMenu(Graphics g)
	{
		if (cinematic != null)
			cinematic.renderMenus(stateInfo.getGc(), g);
	}
	
	public void renderPostEffects(Graphics g)
	{
		if (cinematic != null)
			cinematic.renderPostEffects(g);
	}

	@Override
	public void recieveMessage(Message message) 
	{
		switch (message.getMessageType())
		{
			case Message.MESSAGE_SHOW_CINEMATIC:
				IntMessage im = (IntMessage) message;
				cinematic = stateInfo.getResourceManager().getCinematicById(im.getValue());
				cinematic.initialize(stateInfo);
				break;
		}
	}
	
	public boolean isBlocking()
	{
		return cinematic != null;
	}
}
