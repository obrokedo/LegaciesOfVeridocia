package mb.fc.game.manager;

import org.newdawn.slick.Graphics;

import mb.fc.cinematic.Cinematic;
import mb.fc.engine.message.IntMessage;
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
		this.cinematic = null;
		this.exitTrigId = Trigger.TRIGGER_NONE;
	}

	public void update(int delta)
	{
		if (cinematic != null)
		{
			stateInfo.getCurrentMap().checkRoofs((int) stateInfo.getCamera().getCenterOfCamera(stateInfo.getCurrentMap()).getX(),
					(int) stateInfo.getCamera().getCenterOfCamera(stateInfo.getCurrentMap()).getY());
			
			if (cinematic.update(delta, stateInfo.getCamera(),
				stateInfo.getInput(), stateInfo.getCurrentMap(), stateInfo)) {
				cinematic.endCinematic(stateInfo);
				cinematic = null;
				stateInfo.getCurrentMap().setDisableRoofs(false);
				if (exitTrigId != Trigger.TRIGGER_NONE) {
					stateInfo.getResourceManager().getTriggerEventById(exitTrigId).perform(stateInfo);
					exitTrigId = Trigger.TRIGGER_NONE;
				}
			}
		}

	}

	public void render(Graphics g)
	{
		if (cinematic != null)
			cinematic.render(g, stateInfo.getCamera(), stateInfo.getPaddedGameContainer(), stateInfo);
	}

	public void renderPostEffects(Graphics g)
	{
		if (cinematic != null)
			cinematic.renderPostEffects(g, stateInfo.getCamera(), stateInfo.getPaddedGameContainer(), stateInfo);
	}

	@Override
	public void recieveMessage(Message message)
	{
		switch (message.getMessageType())
		{
			case SHOW_CINEMATIC:
				if (stateInfo.isInCinematicState())
					stateInfo.getCurrentMap().setDisableRoofs(true);
				int cinId = -1;
				if (message instanceof ShowCinMessage) {
					ShowCinMessage m = ((ShowCinMessage) message);
					cinId = m.getCinId();
					this.exitTrigId = m.getExitTrigId();
				} else if (message instanceof IntMessage) {
					cinId = ((IntMessage) message).getValue();
					this.exitTrigId = Trigger.TRIGGER_NONE;
				}
				
				Cinematic cin = stateInfo.getResourceManager().getCinematicById(cinId).duplicateCinematic();
				cin.initialize(stateInfo, initializeCamera);
				cinematic = cin;
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
