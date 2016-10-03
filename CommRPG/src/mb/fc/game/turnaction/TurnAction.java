package mb.fc.game.turnaction;

import java.io.Serializable;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.manager.TurnManager;

public class TurnAction implements Serializable
{
	private static final long serialVersionUID = 1L;

	/*
	 * These were implemented as int values because enums were not being
	 * transported correctly across the "wire"
	 */
	public static final int ACTION_MOVE_TO = 1;
	public static final int ACTION_WAIT = 2;
	public static final int ACTION_END_TURN = 3;
	public static final int ACTION_HIDE_MOVE_AREA = 4;
	public static final int ACTION_ATTACK_SPRITE = 5;
	public static final int ACTION_PERFORM_ATTACK = 6;
	public static final int ACTION_CHECK_DEATH = 7;
	public static final int ACTION_TARGET_SPRITE = 8;
	public static final int ACTION_MOVE_CURSOR_TO_ACTOR = 9;
	public static final int ACTION_MANUAL_MOVE_CURSOR = 10;
	public static final int ACTION_CHECK_SPEECH_END_TURN = 11;
	public static final int ACTION_WAIT_FOR_SPEECH = 12;
	
	public int action;

	public TurnAction(int action) {
		super();
		this.action = action;
	}

	public boolean perform(TurnManager turnManager, StateInfo stateInfo) {return false;}
}
