package mb.fc.game.menu;

import org.newdawn.slick.Image;

import mb.fc.engine.message.AudioMessage;
import mb.fc.engine.message.MessageType;
import mb.fc.engine.message.SpeechMessage;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.constants.Direction;
import mb.fc.game.trigger.Trigger;

public class BattleOptionMenu extends QuadMenu {
	public BattleOptionMenu(StateInfo stateInfo) {
		super(PanelType.PANEL_BATTLE_OPTIONS, stateInfo);

		icons = new Image[4];

		for (int i = 0; i < icons.length; i++)
			icons[i] = stateInfo.getResourceManager().getSpriteSheet("battleoptions").getSprite(i, 0);

		text = new String[] {"Member", "Map", "TxtSpd", "Quit"};
		enabled = new boolean[] {true, true, true, true};
		paintSelectionCursor = true;
	}

	@Override
	public void initialize() {
		this.selected = Direction.UP;
	}

	@Override
	public MenuUpdate onBack() {
		return MenuUpdate.MENU_CLOSE;
	}

	@Override
	public MenuUpdate onConfirm() {
		stateInfo.sendMessage(new AudioMessage(MessageType.SOUND_EFFECT, "menuselect", 1f, false));
		switch (selected)
		{
			case UP:
				stateInfo.sendMessage(MessageType.SHOW_HEROES);
				break;
			case LEFT:
				stateInfo.sendMessage(MessageType.SHOW_MINI_MAP);
				break;
			case RIGHT:
				SpeechMenu.SPEECH_SPEED = (SpeechMenu.SPEECH_SPEED % 3) + 1;
				stateInfo.sendMessage(new SpeechMessage("Text speed has been set to " + SpeechMenu.SPEECH_SPEED + "x.<hardstop>"));
				break;
			case DOWN:
				stateInfo.sendMessage(new SpeechMessage("Are you sure you want to exit?", 
						Trigger.TRIGGER_ID_SAVE_AND_EXIT, Trigger.TRIGGER_NONE, null));
		}

		return MenuUpdate.MENU_ACTION_LONG;
	}

	@Override
	public boolean displayWhenNotTop() {
		return false;
	}
}
