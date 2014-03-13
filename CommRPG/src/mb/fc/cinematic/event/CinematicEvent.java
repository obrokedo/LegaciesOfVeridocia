package mb.fc.cinematic.event;

public class CinematicEvent 
{
	private enum CinematicEventType
	{
		MOVE,
		HALTING_MOVE,
		REMOVE,
		ANIMATION_LOOP,
		ANIMATION,
		HALTING_ANIMATION,
		WAIT,
		CREATE,
		LOAD_MAP,
		LOAD_BATTLE,
		FLASH,
		SPEECH,
		CAMERA_FOLLOW,
		CAMERA_CENTER,
		CAMERA_MOVE,
		FADE
	}
	
	private CinematicEventType type;
	
	protected CinematicEvent(CinematicEventType type)
	{
		this.type = type;
	}
}
