package mb.fc.game.menu;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.StateInfo;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.game.manager.SoundManager;
import mb.fc.game.menu.Menu.MenuUpdate;
import mb.fc.game.ui.PaddedGameContainer;

public class PauseMenu extends Menu {

	private static final Color MENU_COLOR = new Color(0, 0, 0, 120);
	private UnicodeFont subMenuFont;
	private StateInfo stateInfo;
	public PauseMenu(StateInfo stateInfo) {
		super(PanelType.PANEL_PAUSE);
		subMenuFont = new UnicodeFont(PANEL_FONT.getFont().deriveFont(16f));
		subMenuFont.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
		subMenuFont.addAsciiGlyphs();
		subMenuFont.addGlyphs(400, 600);
		this.stateInfo = stateInfo;
		try {
			subMenuFont.loadGlyphs();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}

	@Override
	public MenuUpdate update(long delta, StateInfo si) {
		if (stateInfo != null) {
			if (SoundManager.GLOBAL_VOLUME > 0 && stateInfo.getInput().isKeyDown(Input.KEY_LEFT)) {
				SoundManager.GLOBAL_VOLUME -= .01f;
			} else if (SoundManager.GLOBAL_VOLUME < 1 && stateInfo.getInput().isKeyDown(Input.KEY_RIGHT)) {
				SoundManager.GLOBAL_VOLUME += .01f;
			}
		}
		return super.update(delta, stateInfo);
	}



	@Override
	public MenuUpdate handleUserInput(FCInput input, StateInfo stateInfo) {
		
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
		
		if (stateInfo != null) {
			graphics.setFont(subMenuFont);
			int volume = (int) (SoundManager.GLOBAL_VOLUME * 100);
			graphics.drawString("- Volume " + volume +" -", (CommRPG.GAME_SCREEN_SIZE.width 
					- subMenuFont.getWidth("- Volume " + volume +" -")) / 2, CommRPG.GAME_SCREEN_SIZE.height / 3 + 50);
		}
	}

}
