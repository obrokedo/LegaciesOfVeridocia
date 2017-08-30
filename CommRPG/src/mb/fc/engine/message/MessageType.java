package mb.fc.engine.message;

public enum MessageType
{
	NEXT_TURN,
	COMBATANT_TURN,
	ATTACK_PRESSED,
	HIDE_ATTACKABLE,
	SHOW_MOVEABLE,
	SHOW_BATTLEMENU,
	SELECT_ITEM,
	RESET_SPRITELOC,
	MOVETO_SPRITELOC,
	SET_INIT_ORDER,
	TURN_ACTIONS,
	CLIENT_ID,
	TARGET_SPRITE,
	BATTLE_RESULTS,
	SELECT_SPELL,
	SHOW_SPELLMENU,
	SEND_INTERNAL_MESSAGE,
	OVERLAND_MOVE_MESSAGE,
	SPEECH,
	COMPLETE_QUEST,
	LOAD_MAP,
	START_BATTLE,
	WAIT,
	CONTINUE,
	SHOW_WAIT,
	HIDE_WAIT,
	SHOW_SYSTEM_MENU,
	SHOW_SHOP,
	SHOW_SHOP_BUY,
	SHOW_SHOP_DEALS,
	SHOW_SHOP_SELL,
	SHOW_SHOP_REPAIR,
	SHOW_HEROES,
	SHOW_HERO,
	INTIIALIZE,
	SEND_HEROES,
	SHOW_PRIEST,
	SAVE,
	SAVE_BATTLE,
	CLIENT_REGISTRATION,
	ASSIGN_HERO,
	SHOW_ASSIGN_HERO,
	PLAYER_LIST,
	START_GAME,
	PLAYER_END_TURN,
	SHOW_ITEM_MENU,
	SHOW_ITEM_OPTION_MENU,
	SHOW_PANEL_MULTI_JOIN_CHOOSE,
	SHOW_CINEMATIC,
	INVESTIGATE,
	SOUND_EFFECT,
	PAUSE_MUSIC,
	RESUME_MUSIC,
	PLAY_MUSIC,
	FADE_MUSIC,
	SHOW_DEBUG,
	LOAD_CINEMATIC,
	BATTLE_COND,
	PUBLIC_SPEECH,
	CLIENT_BROADCAST_HERO,
	GAME_READY,
	HIDE_ATTACK_AREA,
	DISPLAY_MAP_ENTRY,
	SET_SELECTED_SPRITE,
	CIN_NEXT_ACTION,
	CIN_END
}