package mb.fc.engine.config;

import org.newdawn.slick.Graphics;

import mb.fc.engine.state.StateInfo;
import mb.fc.game.ui.PaddedGameContainer;

public interface YesNoMenuRenderer {
	public void initialize(StateInfo stateInfo);
	public void render(PaddedGameContainer gc, Graphics graphics);
	public void update(long delta, StateInfo stateInfo);
	public void yesPressed();
	public void noPressed();
}
