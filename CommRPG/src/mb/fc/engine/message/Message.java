package mb.fc.engine.message;

import java.io.Serializable;

public class Message implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public static final int MESSAGE_NEXT_TURN = 0;
	public static final int MESSAGE_COMBATANT_TURN = 1;

	
	public static final int MESSAGE_ATTACK_PRESSED = 4;
	public static final int MESSAGE_HIDE_ATTACKABLE = 5;
	public static final int MESSAGE_SHOW_MOVEABLE = 6;
	public static final int MESSAGE_SHOW_BATTLEMENU = 7;
	public static final int MESSAGE_SELECT_ITEM = 8;
	public static final int MESSAGE_RESET_SPRITELOC = 9;
	public static final int MESSAGE_MOVETO_SPRITELOC = 10;
	public static final int MESSAGE_SET_INIT_ORDER = 11;
	public static final int MESSAGE_TURN_ACTIONS = 12;
	public static final int MESSAGE_CLIENT_ID = 13;
	public static final int MESSAGE_TARGET_SPRITE = 14;
	public static final int MESSAGE_BATTLE_RESULTS = 15;
	public static final int MESSAGE_SELECT_SPELL = 16;
	public static final int MESSAGE_SHOW_SPELLMENU = 17;
	public static final int MESSAGE_SEND_INTERNAL_MESSAGE = 18;
	public static final int MESSAGE_OVERLAND_MOVE_MESSAGE = 19;
	public static final int MESSAGE_SPEECH = 21;
	public static final int MESSAGE_COMPLETE_QUEST = 22;
	public static final int MESSAGE_LOAD_MAP = 23;
	public static final int MESSAGE_START_BATTLE = 24;
	public static final int MESSAGE_WAIT = 25;
	public static final int MESSAGE_CONTINUE = 26;
	public static final int MESSAGE_SHOW_WAIT = 27;
	public static final int MESSAGE_HIDE_WAIT = 28;
	public static final int MESSAGE_SHOW_SYSTEM_MENU = 29;
	public static final int MESSAGE_SHOW_SHOP = 30;
	public static final int MESSAGE_SHOW_HEROES = 31;
	public static final int MESSAGE_SHOW_HERO = 32;
	public static final int MESSAGE_INTIIALIZE = 33;
	public static final int MESSAGE_SEND_HEROES = 34;
	public static final int MESSAGE_SHOW_PRIEST = 35;
	public static final int MESSAGE_SAVE = 36;
	public static final int MESSAGE_CLIENT_NAME = 37;
	public static final int MESSAGE_ASSIGN_HERO = 38;
	public static final int MESSAGE_SHOW_ASSIGN_HERO = 39;
	public static final int MESSAGE_PLAYER_LIST = 40;
	public static final int MESSAGE_START_GAME = 41;
	public static final int MESSAGE_PLAYER_END_TURN = 42;
	public static final int MESSAGE_SHOW_ITEM_MENU = 43;
	public static final int MESSAGE_SHOW_ITEM_OPTION_MENU = 44;
	public static final int MESSAGE_SHOW_CINEMATIC = 45;
	public static final int MESSAGE_INVESTIGATE = 46;
	public static final int MESSAGE_SOUND_EFFECT = 47;
	public static final int MESSAGE_PAUSE_MUSIC = 48;
	public static final int MESSAGE_RESUME_MUSIC = 49;
	public static final int MESSAGE_PLAY_MUSIC = 50;
	public static final int MESSAGE_FADE_MUSIC = 51;
	private int messageType;
	private boolean immediate = false;
	
	public Message(int messageType) {
		super();
		this.messageType = messageType;
		
		if (messageType == MESSAGE_INTIIALIZE || messageType == MESSAGE_MOVETO_SPRITELOC)
		{
			immediate = true;
		}
	}

	public int getMessageType() {
		return messageType;
	}

	public boolean isImmediate() {
		return immediate;
	}
}
