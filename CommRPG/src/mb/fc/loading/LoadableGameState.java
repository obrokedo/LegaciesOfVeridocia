package mb.fc.loading;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import mb.fc.engine.CommRPG;
import mb.fc.game.menu.UIDebugMenu;
import mb.fc.game.ui.PaddedGameContainer;

public abstract class LoadableGameState extends BasicGameState
{
	protected boolean loading = false;
	
	protected boolean paused = false;
	
	protected UIDebugMenu uiDebugMenu = new UIDebugMenu();

	public abstract void stateLoaded(FCResourceManager resourceManager);

	public abstract void initAfterLoad();
	
	public abstract void doUpdate(PaddedGameContainer container, StateBasedGame game, int delta) throws SlickException;
	
	public abstract void doRender(PaddedGameContainer container, StateBasedGame game, Graphics g);
	
	public boolean isPaused(GameContainer gc) {
		if (gc.getInput().isKeyPressed(Input.KEY_PAUSE))
		{
			paused = !paused;
		}
		return paused;
	}

	public boolean isLoading() {
		return loading;
	}

	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		g.clearClip();
		g.setColor(Color.black);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		g.translate(((PaddedGameContainer) container).getDisplayPaddingX(), 0);
		g.scale(CommRPG.GAME_SCREEN_SCALE, CommRPG.GAME_SCREEN_SCALE);
		g.setClip(((PaddedGameContainer) container).getDisplayPaddingX(), 0, 
				((PaddedGameContainer) container).getPaddedWidth(), container.getHeight());
		doRender((PaddedGameContainer) container, game, g);
		
		if (paused) {
			uiDebugMenu.render(g);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (!isPaused(container)) {
			doUpdate((PaddedGameContainer) container, game, delta);
		} else {
			uiDebugMenu.update(container, game, delta);
		}
	}
}
