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
		// TODO Auto-generated method stub

	}

	@Override
	protected MenuUpdate onBack() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected MenuUpdate onConfirm() {
		// TODO Auto-generated method stub
		return null;
	}
}
