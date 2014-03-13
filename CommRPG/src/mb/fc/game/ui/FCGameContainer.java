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
}
