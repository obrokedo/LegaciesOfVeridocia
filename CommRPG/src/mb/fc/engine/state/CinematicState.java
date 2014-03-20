package mb.fc.engine.state;

import mb.fc.cinematic.Cinematic;
import mb.fc.engine.CommRPG;
import mb.fc.game.Camera;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.input.FCInput;
import mb.fc.renderer.TileMapRenderer;
import mb.fc.resource.FCResourceManager;
import mb.gl2.loading.LoadableGameState;
import mb.gl2.loading.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class CinematicState extends LoadableGameState
{
	private TileMapRenderer tileMapRenderer;
	private Camera camera;
	private Cinematic cinematic;
	private boolean initialized = false;
	private PersistentStateInfo psi;
	private FCInput input;

	public CinematicState(PersistentStateInfo psi)
	{
		this.psi = psi;
		tileMapRenderer = new TileMapRenderer();
		camera = new Camera(psi.getGc().getWidth(), psi.getGc().getHeight());
		input = new FCInput();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.enter(container, game);
		
		this.tileMapRenderer.setMap(psi.getResourceManager().getMap());		
		// Get the first cinematic
		cinematic = psi.getResourceManager().getCinematicById(0);
		cinematic.initialize(psi, camera, psi.getResourceManager().getMap(), null);
		initialized = true;						
		
		input.clear();
		container.getInput().addKeyListener(input);
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.leave(container, game);
		initialized = false;
		container.getInput().removeKeyListener(input);
	}

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException 
	{
		if (initialized)
		{
			tileMapRenderer.render(camera, g, psi.getGc());
			cinematic.render(g, camera, psi.getGc());
			tileMapRenderer.renderForeground(camera, g, psi.getGc());
			cinematic.renderMenus(psi.getGc(), g);
			cinematic.renderPostEffects(g);
		}
		
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (initialized)
		{
			cinematic.update(delta, camera, input, psi.getGc(), psi, psi.getResourceManager().getMap(), null);
		}
		input.update();
	}

	@Override
	public void stateLoaded(ResourceManager resourceManager) {
		psi.setResourceManager((FCResourceManager) resourceManager);
		Panel.intialize(psi.getResourceManager());
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_CINEMATIC;
	}
}
