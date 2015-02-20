package mb.fc.game.ui;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Game;
import org.newdawn.slick.SlickException;

public class FCGameContainer extends AppGameContainer
{
	private int displayPaddingX;

	public FCGameContainer(Game game) throws SlickException
	{
		super(game);
	}

	public int getDisplayPaddingX() {
		return displayPaddingX;
	}

	public void setDisplayPaddingX(int displayPaddingX) {
		this.displayPaddingX = displayPaddingX;
	}



	/**
	 * Strategy for overloading game loop context handling
	 *
	 * @throws SlickException Indicates a game failure
	 */
	/*
	@Override
	protected void gameLoop() throws SlickException {
		int delta = getDelta();
		if (!Display.isVisible() && updateOnlyOnVisible) {
			try { Thread.sleep(100); } catch (Exception e) {}
		} else {
			try {
				updateAndRender(delta);
			} catch (Throwable e) {

				running = false;
				// ((CommRPG) game).enterState(CommRPG.STATE_GAME_MENU_DEVEL);

				return;
			}
		}

		updateFPS();

		Display.update();

		if (Display.isCloseRequested()) {
			if (game.closeRequested()) {
				running = false;
			}
		}
	}
	*/
}
