package mb.fc.engine.config;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.game.ui.RectUI;
import mb.fc.game.ui.SelectRectUI;
import mb.fc.game.ui.TextUI;

public class DefaultYesNoRenderer implements YesNoMenuRenderer {

	private RectUI yesPanel, noPanel;
	private TextUI yesText, noText;
	private SelectRectUI selectRect;
	
	@Override
	public void initialize(StateInfo stateInfo) {
		yesPanel = new RectUI(120, 146, 32, 32);
		noPanel = new RectUI(170, 146, 32, 32);
		yesText = new TextUI("Yes", 125, 148);
		noText = new TextUI("No", 179, 148);
		selectRect = new SelectRectUI(120, 146, 32, 32);
	}

	@Override
	public void render(PaddedGameContainer gc, Graphics graphics) {
		// Draw background
		yesPanel.drawPanel(graphics);
		noPanel.drawPanel(graphics);
		// Draw temporary YES - NO
		graphics.setColor(Color.white);
		yesText.drawText(graphics);
		noText.drawText(graphics);

		// Draw selection square
		selectRect.draw(graphics, Color.red);
	}

	@Override
	public void update(long delta, StateInfo stateInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void yesPressed() {
		selectRect.setX(120);
	}

	@Override
	public void noPressed() {
		selectRect.setX(170);
	}
	
}
