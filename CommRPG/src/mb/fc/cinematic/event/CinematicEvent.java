package mb.fc.cinematic.event;

import java.util.ArrayList;

public class CinematicEvent 
{
	public enum CinematicEventType
	{
		MOVE,
		HALTING_MOVE,
		MOVE_ENFORCE_FACING,
		LOOP_MOVE,
		STOP_LOOP_MOVE,
		REMOVE_ACTOR,
		ADD_ACTOR,
		ASSOCIATE_NPC_AS_ACTOR,
		ASSOCIATE_HERO_AS_ACTOR,
		ASSOCIATE_ENEMY_AS_ACTOR,
		ANIMATION_LOOP,
		ANIMATION,
		HALTING_ANIMATION,
		WAIT,
		SPIN,
		STOP_SPIN,
		CREATE,
		LOAD_MAP,
		LOAD_BATTLE,
		FLASH,
		NOD,
		HEAD_SHAKE,
		FACING,
		SHRINK,
		GROW,
		QUIVER,
		FALL_ON_FACE,
		LAY_ON_SIDE,
		STOP_SE,
		VISIBLE,
		SPEECH,
		CAMERA_SHAKE,
		CAMERA_FOLLOW,
		CAMERA_CENTER,
		CAMERA_MOVE,
		STARTLOOPMAP,
		ENDLOOPMAP,
		FADE,
		PLAY_MUSIC,
		PAUSE_MUSIC,
		RESUME_MUSIC,
		FADE_MUSIC,
		PLAY_SOUND
	}
	
	private CinematicEventType type;
	private ArrayList<Object> params;
	
	public CinematicEvent(CinematicEventType type, Object... params)
	{
		this.type = type;
		this.params = new ArrayList<Object>();
		
		for (Object o : params)
			this.params.add(o);
	}

	public CinematicEventType getType() {
		return type;
	}
	
	public Object getParam(int i)
	{
		return params.get(i);
	}
}
