package mb.fc.engine.state;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.MessageType;
import mb.fc.game.manager.InitiativeManager;
import mb.fc.game.manager.KeyboardManager;
import mb.fc.game.manager.MenuManager;
import mb.fc.game.manager.PanelManager;
import mb.fc.game.manager.SoundManager;
import mb.fc.game.manager.SpriteManager;
import mb.fc.game.manager.TurnManager;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.renderer.MenuRenderer;
import mb.fc.renderer.PanelRenderer;
import mb.fc.renderer.SpriteRenderer;
import mb.fc.renderer.TileMapRenderer;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

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

	private InitiativeManager initManager;
	private TurnManager turnManager;

	private StateInfo stateInfo;

	/*
	private float musicVolume = 0;
	private String music = null;
	*/

	public BattleState(PersistentStateInfo psi)
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
	}

	/**
	 * Initializes this state, this only gets called when coming
	 * from a loading state
	 */
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException
	{
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
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		if (stateInfo.isInitialized())
		{
			tileMapRenderer.render(stateInfo.getCamera(), g, stateInfo.getGc());
			turnManager.render(g);
			spriteRenderer.render(g);
			tileMapRenderer.renderForeground(stateInfo.getCamera(), g, stateInfo.getGc());
			turnManager.renderCursor(g);
			panelRenderer.render();
			menuRenderer.render();
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException
	{
		stateInfo.processMessages();
		if (stateInfo.isInitialized())
		{
			menuManager.update(delta);
			if (!menuManager.isBlocking())
			{
				//hudMenuManager.update();
				keyboardManager.update();
			}

			turnManager.update(game, delta);
			spriteManager.update(delta);
			soundManager.update(delta);


			if (System.currentTimeMillis() > stateInfo.getInputDelay())
			{
				if (container.getInput().isKeyDown(Input.KEY_ENTER))
				{
					stateInfo.sendMessage(MessageType.SHOW_DEBUG);
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				}
				/*
				if (container.getInput().isKeyDown(Input.KEY_ESCAPE))
				{
					stateInfo.sendMessage(MessageType.SHOW_SYSTEM_MENU);
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				}*/
				else if (container.getInput().isKeyDown(Input.KEY_F7))
				{
					((CommRPG) game).toggleFullScreen();
				}
			}


			stateInfo.getInput().update(delta);
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
}
