package mb.fc.game.hudmenu;

import mb.fc.engine.CommRPG;
import mb.fc.game.ui.PaddedGameContainer;

import org.newdawn.slick.Graphics;

public class TextPanel extends Panel
{
	public static int LOCATION_RIGHT = 0;
	public static int LOCATION_CENTER = 1;

	private String text;
	private int location;

	public TextPanel(String text, int location) {
		super(PanelType.PANEL_TEXT);
		this.text = text;

		this.location = location;
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics)
	{
		int x = 275;

		if (location == LOCATION_CENTER)
			x = 140;
		Panel.drawPanelBox(x, CommRPG.GAME_SCREEN_SIZE.height - 140, CommRPG.GAME_SCREEN_SIZE.width - 280 , 135, graphics);
		graphics.setColor(Panel.COLOR_FOREFRONT);
		graphics.drawString(text, x + 15, CommRPG.GAME_SCREEN_SIZE.height - 125);
	}
}
