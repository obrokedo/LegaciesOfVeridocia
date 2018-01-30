package mb.fc.game.menu;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.ui.PaddedGameContainer;

public class PauseMenu extends Menu {

	private static final Color MENU_COLOR = new Color(0, 0, 0, 120);
	
	public PauseMenu() {
		super(PanelType.PANEL_PAUSE);
	}

	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		graphics.setColor(MENU_COLOR);
		graphics.fillRect(0, 0, gc.getWidth(), gc.getHeight());
		graphics.setColor(Color.white);
		graphics.setFont(Panel.PANEL_FONT);
		
		graphics.drawString("Paused", (CommRPG.GAME_SCREEN_SIZE.width 
				- Panel.PANEL_FONT.getWidth("PAUSED")) / 2, CommRPG.GAME_SCREEN_SIZE.height / 3);
	}

}
