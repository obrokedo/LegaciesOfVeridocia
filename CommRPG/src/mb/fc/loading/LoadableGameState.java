package mb.fc.loading;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import mb.fc.engine.CommRPG;
import mb.fc.game.menu.Menu;
import mb.fc.game.menu.PauseMenu;
import mb.fc.game.menu.UIDebugMenu;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.utils.StringUtils;

public abstract class LoadableGameState extends BasicGameState
{
	protected boolean loading = false;
	
	protected boolean paused = false;
	
	protected UIDebugMenu uiDebugMenu = new UIDebugMenu();
	
	protected Menu defaultPauseMenu;
	protected Menu pauseMenu = null;
	
	protected float updateSpeed = 1.0f;

	public abstract void stateLoaded(ResourceManager resourceManager);

	public abstract void initAfterLoad();
	
	public abstract void doUpdate(PaddedGameContainer container, StateBasedGame game, int delta) throws SlickException;
	
	public abstract void doRender(PaddedGameContainer container, StateBasedGame game, Graphics g);
	
	private int inputTimer = 0;
	
	public boolean isPaused(GameContainer gc) {
		if (gc.getInput().isKeyDown(Input.KEY_ENTER))
		{
			
			if (paused) {
				pauseMenuClosed();
			}
			else {
				pauseMenu = getPauseMenu();
				if (pauseMenu == null)
					return false;
			}
			paused = !paused;
			inputTimer = 500;
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
		
		if (updateSpeed != 1)
		{
			g.setColor(Color.red);
			StringUtils.drawString("Update speed: " + updateSpeed, 15, 15, g);
		}
		
		if (paused) {
			pauseMenu.render((PaddedGameContainer) container, g);
			// uiDebugMenu.render(g);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
		if (inputTimer > 0)
			inputTimer -= delta;
		
		if (inputTimer <= 0)
			isPaused(container);
		
		if (!paused) {
			if (inputTimer <= 0) {				
				if (CommRPG.DEV_MODE_ENABLED && container.getInput().isKeyDown(Input.KEY_F11))
				{
					updateSpeed /= 2;
					inputTimer = 200;
				}
				else if (CommRPG.DEV_MODE_ENABLED && container.getInput().isKeyDown(Input.KEY_F12))
				{
					updateSpeed *= 2;
					inputTimer = 200;
				}
				else if (container.getInput().isKeyDown(Input.KEY_F7))
				{
					((CommRPG) game).toggleFullScreen();
				}
				else if (CommRPG.DEV_MODE_ENABLED && container.getInput().isKeyPressed(Input.KEY_ESCAPE))
				{
					game.enterState(CommRPG.STATE_GAME_MENU_DEVEL);
				}
			}
			
			doUpdate((PaddedGameContainer) container, game, (int) (delta * updateSpeed));
		} else {
			pauseMenu.update(delta, null);
			// uiDebugMenu.update(container, game, delta);
		}
	}
	
	protected abstract Menu getPauseMenu();
	
	protected void pauseMenuClosed() {
		
	}
}
