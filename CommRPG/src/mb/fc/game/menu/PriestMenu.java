package mb.fc.game.menu;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;

public class PriestMenu extends QuadMenu
{
	public PriestMenu(StateInfo stateInfo)
	{
		super(Panel.PANEL_PRIEST, stateInfo);
	}

	@Override
	public void initialize() {

	}

	@Override
	protected MenuUpdate onBack() {
		return null;
	}

	@Override
	protected MenuUpdate onConfirm() {
		return null;
	}
}
