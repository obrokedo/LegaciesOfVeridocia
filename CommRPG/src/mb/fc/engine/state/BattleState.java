package mb.fc.engine.state;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.MessageType;
import mb.fc.game.manager.CinematicManager;
import mb.fc.game.manager.InitiativeManager;
import mb.fc.game.manager.KeyboardManager;
import mb.fc.game.manager.MenuManager;
import mb.fc.game.manager.PanelManager;
import mb.fc.game.manager.SoundManager;
import mb.fc.game.manager.SpriteManager;
import mb.fc.game.manager.TurnManager;
import mb.fc.game.menu.DebugMenu;
import mb.fc.game.menu.Menu;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.renderer.MenuRenderer;
import mb.fc.renderer.PanelRenderer;
import mb.fc.renderer.SpriteRenderer;
import mb.fc.renderer.TileMapRenderer;

/**
 * State that drives and renders battle movement and menus. This does not
 * handle attack cinematics
 *
 * @author Broked
 *
 */
public class BattleState extends LoadableGameState
{
	private TileMapRenderer tileMapRenderer;
	private SpriteRenderer spriteRenderer;
	private PanelRenderer panelRenderer;
	private MenuRenderer menuRenderer;
	private SpriteManager spriteManager;
	private PanelManager panelManager;
	private MenuManager menuManager;
	private KeyboardManager keyboardManager;
	private SoundManager soundManager;
	private CinematicManager cinematicManager;

	private InitiativeManager initManager;
	private TurnManager turnManager;

	private StateInfo stateInfo;

	/*
	private float musicVolume = 0;
	private String music = null;
	*/

	public BattleState()
	{

	}

	public void setPersistentStateInfo(PersistentStateInfo psi)
	{
		this.stateInfo = new StateInfo(psi, true, false);
		this.tileMapRenderer = new TileMapRenderer();
		stateInfo.registerManager(tileMapRenderer);
		this.spriteRenderer = new SpriteRenderer();
		stateInfo.registerManager(spriteRenderer);
		this.panelRenderer = new PanelRenderer();
		stateInfo.registerManager(panelRenderer);
		this.menuRenderer = new MenuRenderer();
		stateInfo.registerManager(menuRenderer);
		this.initManager = new InitiativeManager();
		stateInfo.registerManager(initManager);
		this.spriteManager = new SpriteManager();
		stateInfo.registerManager(spriteManager);
		this.panelManager = new PanelManager();
		stateInfo.registerManager(panelManager);
		this.menuManager = new MenuManager();
		stateInfo.registerManager(menuManager);
		this.keyboardManager = new KeyboardManager();
		stateInfo.registerManager(keyboardManager);
		this.turnManager = new TurnManager();
		stateInfo.registerManager(turnManager);
		this.soundManager = new SoundManager();
		stateInfo.registerManager(soundManager);
		this.cinematicManager = new CinematicManager(false);
		stateInfo.registerManager(cinematicManager);
	}


	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException
	{

	}

	/**
	 * Initializes this state, this only gets called when coming
	 * from a loading state
	 */
	@Override
	public void initAfterLoad() {
		stateInfo.initState();
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game)
			throws SlickException {
		super.enter(container, game);

		if (stateInfo.isShowAttackCinematic())
		{
			stateInfo.setWaiting();
			stateInfo.sendMessage(MessageType.WAIT);
			stateInfo.getInput().clear();
			container.getInput().addKeyListener(stateInfo.getInput());
			this.stateInfo.setInputDelay(System.currentTimeMillis() + 200);
			stateInfo.setShowAttackCinematic(false);
			stateInfo.sendMessage(MessageType.RESUME_MUSIC);
		}
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {
		if (!stateInfo.isShowAttackCinematic())
		{
			stateInfo.getResourceManager().reinitialize();
			stateInfo.setInitialized(false);
			stateInfo.getInput().clear();
		}

		super.leave(container, game);
	}

	@Override
	public void doRender(PaddedGameContainer container, StateBasedGame game, Graphics g) {
		if (stateInfo.isInitialized())
		{
			float xOffset = stateInfo.getCamera().getLocationX() % stateInfo.getCurrentMap().getTileRenderWidth();
			float yOffset = stateInfo.getCamera().getLocationY() % stateInfo.getCurrentMap().getTileRenderHeight();

			tileMapRenderer.render(xOffset, yOffset, stateInfo.getCamera(), g, stateInfo.getPaddedGameContainer());
			turnManager.render(g);
			spriteRenderer.render(g);
			cinematicManager.render(g);
			tileMapRenderer.renderForeground(xOffset, yOffset, stateInfo.getCamera(), g, stateInfo.getPaddedGameContainer());
			turnManager.renderCursor(g);
			cinematicManager.renderPostEffects(g);
			panelRenderer.render(g);
			menuRenderer.render(g);
		}
	}

	@Override
	public void doUpdate(PaddedGameContainer container, StateBasedGame game, int delta)
			throws SlickException
	{
		if (CommRPG.TEST_MODE_ENABLED)
			delta *= CommRPG.getTestMultiplier();

		// delta /= 2;

		if (stateInfo.getTopMenu() == null || !(stateInfo.getTopMenu() instanceof DebugMenu)) {
			stateInfo.processMessages();
		}
		
		
		if (stateInfo.isInitialized() && !stateInfo.isWaiting())
		{
			
			menuManager.update(delta);
			cinematicManager.update(delta);
			
			if (!menuManager.isBlocking() && !cinematicManager.isBlocking())
			{
				//hudMenuManager.update();
				keyboardManager.update();
				turnManager.update(game, delta);
			}

			stateInfo.getCurrentMap().update(delta);
			spriteManager.update(delta);
			soundManager.update(delta);
			stateInfo.getInput().update(delta, container.getInput());
		}
	}

	@Override
	public void stateLoaded(FCResourceManager resourceManager) {
		this.stateInfo.setResourceManager(resourceManager);
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_BATTLE;
	}
	
	@Override
	protected Menu getPauseMenu() {
		stateInfo.sendMessage(MessageType.PAUSE_MUSIC);
		return new DebugMenu(stateInfo);
	}

	@Override
	protected void pauseMenuClosed() {
		stateInfo.getCamera().centerOnSprite(stateInfo.getCurrentSprite(), stateInfo.getCurrentMap());
		stateInfo.sendMessage(MessageType.RESUME_MUSIC);
	}
}
