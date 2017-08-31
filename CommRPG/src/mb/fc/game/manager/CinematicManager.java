package mb.fc.game.manager;

import org.newdawn.slick.Graphics;

import mb.fc.cinematic.Cinematic;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.ShowCinMessage;
import mb.fc.game.trigger.Trigger;

public class CinematicManager extends Manager
{
	private Cinematic cinematic;
	private boolean initializeCamera = true;
	private int exitTrigId = Trigger.TRIGGER_NONE;

	public CinematicManager(boolean initializeCamera) {
		super();
		this.initializeCamera = initializeCamera;
	}

	@Override
	public void initialize() {

	}

	public void update(int delta)
	{
		if (cinematic != null && cinematic.update(delta, stateInfo.getCamera(),
				stateInfo.getInput(), stateInfo.getCurrentMap(), stateInfo))
		{
			cinematic.endCinematic(stateInfo);
			cinematic = null;
			if (exitTrigId != Trigger.TRIGGER_NONE) {
				stateInfo.getResourceManager().getTriggerEventById(exitTrigId).perform(stateInfo);
				exitTrigId = Trigger.TRIGGER_NONE;
			}
			// stateInfo.getCamera().centerOnSprite(stateInfo.getCurrentSprite(), stateInfo.getCurrentMap());
		}

	}

	public void render(Graphics g)
	{
		if (cinematic != null)
			cinematic.render(g, stateInfo.getCamera(), stateInfo.getFCGameContainer(), stateInfo);
	}

	public void renderPostEffects(Graphics g)
	{
		if (cinematic != null)
			cinematic.renderPostEffects(g, stateInfo.getCamera(), stateInfo.getFCGameContainer(), stateInfo);
	}

	@Override
	public void recieveMessage(Message message)
	{
		switch (message.getMessageType())
		{
			case SHOW_CINEMATIC:
				ShowCinMessage im = (ShowCinMessage) message;
				this.exitTrigId = im.getExitTrigId();
				cinematic = stateInfo.getResourceManager().getCinematicById(im.getCinId()).duplicateCinematic();
				cinematic.initialize(stateInfo, initializeCamera);
				break;
			case CIN_NEXT_ACTION:
				if (cinematic != null)
					cinematic.nextAction(stateInfo);
				break;
			default:
				break;
		}
	}

	public boolean isBlocking()
	{
		return cinematic != null;
	}
}
