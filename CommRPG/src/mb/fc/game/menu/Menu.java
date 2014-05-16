package mb.fc.game.menu;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;

/**
 * A special type of panel container that allows for user interaction and generally
 * maintains a certain state about the users action. These menus will have user "focus"
 * which means that certain other actions will not be possible while one of these menus
 * is up.
 */
public abstract class Menu extends Panel
{
	public enum MenuUpdate
	{
		MENU_NO_ACTION,
		MENU_CLOSE,
		MENU_ACTION_SHORT,
		MENU_ACTION_LONG,
		MENU_NEXT_ACTION
	}

	public Menu(int menuType) {
		super(menuType);
	}

	public abstract MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo);
}
