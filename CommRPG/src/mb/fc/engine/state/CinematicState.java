package mb.fc.engine.state;

import mb.fc.cinematic.Cinematic;
import mb.fc.engine.CommRPG;
import mb.fc.game.manager.SoundManager;
import mb.fc.loading.FCResourceManager;
import mb.fc.renderer.TileMapRenderer;
import mb.gl2.loading.LoadableGameState;
import mb.gl2.loading.ResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class CinematicState extends LoadableGameState
{
	private TileMapRenderer tileMapRenderer;
	private SoundManager soundManager;
	private Cinematic cinematic;

	// TODO THIS IS A DEBUG TOOL
	public static float cinematicSpeed = 1;

	private StateInfo stateInfo;

	public CinematicState(PersistentStateInfo psi)
	{
		this.stateInfo = new StateInfo(psi, false, true);
		this.tileMapRenderer = new TileMapRenderer();
		stateInfo.registerManager(tileMapRenderer);
		this.soundManager = new SoundManager();
		stateInfo.registerManager(soundManager);
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		stateInfo.initState();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.enter(container, game);

		// Get the first cinematic
		cinematic = stateInfo.getResourceManager().getCinematicById(stateInfo.getPsi().getCinematicID());
		cinematic.initialize(stateInfo);
		stateInfo.setInitialized(true);
		stateInfo.getInput().clear();
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.leave(container, game);
		stateInfo.setInitialized(false);
		stateInfo.getResourceManager().reinitialize();
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException
	{
		if (stateInfo.isInitialized())
		{
			tileMapRenderer.render(stateInfo.getCamera(), g, stateInfo.getGc());
			cinematic.render(g, stateInfo.getCamera(), stateInfo.getGc(), stateInfo);
			tileMapRenderer.renderForeground(stateInfo.getCamera(), g, stateInfo.getGc());
			cinematic.renderPostEffects(g, stateInfo.getCamera(), stateInfo.getGc(), stateInfo);
			cinematic.renderMenus(stateInfo.getGc(), g);
			if (cinematicSpeed != 1)
			{
				g.setColor(Color.red);
				g.drawString("Cinematic speed: " + cinematicSpeed, 15, 15);
			}
		}

	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (stateInfo.isInitialized())
		{
			stateInfo.processMessages();
			cinematic.update((int) (delta * cinematicSpeed), stateInfo.getCamera(), stateInfo.getInput(), stateInfo.getGc(), stateInfo.getResourceManager().getMap(), stateInfo);

			if (System.currentTimeMillis() > stateInfo.getInputDelay())
			{
				if (stateInfo.getInput().isKeyDown(Input.KEY_F11))
				{
					cinematicSpeed /= 2;
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				}
				else if (stateInfo.getInput().isKeyDown(Input.KEY_F12))
				{
					cinematicSpeed *= 2;
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				}
			}
		}
		stateInfo.getInput().update(delta);
	}

	@Override
	public void stateLoaded(ResourceManager resourceManager) {
		stateInfo.setResourceManager((FCResourceManager) resourceManager);
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_CINEMATIC;
	}
}
