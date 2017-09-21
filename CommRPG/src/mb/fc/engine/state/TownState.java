package mb.fc.engine.state;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.Message;
import mb.fc.engine.message.MessageType;
import mb.fc.game.input.KeyMapping;
import mb.fc.game.manager.CinematicManager;
import mb.fc.game.manager.MenuManager;
import mb.fc.game.manager.PanelManager;
import mb.fc.game.manager.SoundManager;
import mb.fc.game.manager.SpriteManager;
import mb.fc.game.manager.TownMoveManager;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.renderer.MenuRenderer;
import mb.fc.renderer.PanelRenderer;
import mb.fc.renderer.SpriteRenderer;
import mb.fc.renderer.TileMapRenderer;

/**
 * State that handles movement in town/overland, NPCs and
 * town menus
 *
 * @author Broked
 *
 */
public class TownState extends LoadableGameState
{
	private TileMapRenderer tileMapRenderer;
	private SpriteRenderer spriteRenderer;
	private PanelRenderer panelRenderer;
	private MenuRenderer menuRenderer;
	private SpriteManager spriteManager;
	private PanelManager panelManager;
	private MenuManager menuManager;
	private TownMoveManager townMoveManager;
	private CinematicManager cinematicManager;
	private SoundManager soundManager;

	private StateInfo stateInfo;
	
	private Image image;

	public TownState() { }

	public void setPersistentStateInfo(PersistentStateInfo psi)
	{
		this.stateInfo = new StateInfo(psi, false, false);
		this.tileMapRenderer = new TileMapRenderer();
		stateInfo.registerManager(tileMapRenderer);
		this.spriteRenderer = new SpriteRenderer();
		stateInfo.registerManager(spriteRenderer);
		this.panelRenderer = new PanelRenderer();
		stateInfo.registerManager(panelRenderer);
		this.menuRenderer = new MenuRenderer();
		stateInfo.registerManager(menuRenderer);
		this.spriteManager = new SpriteManager();
		stateInfo.registerManager(spriteManager);
		this.panelManager = new PanelManager();
		stateInfo.registerManager(panelManager);
		this.menuManager = new MenuManager();
		stateInfo.registerManager(menuManager);
		this.townMoveManager = new TownMoveManager();
		stateInfo.registerManager(townMoveManager);
		this.cinematicManager = new CinematicManager(false);
		stateInfo.registerManager(cinematicManager);
		this.soundManager = new SoundManager();
		stateInfo.registerManager(soundManager);
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
		// To allow the hero to continue moving between maps, initialize the input
		// with any movement keys that are already pressed
		stateInfo.getInput().setInitialMovementInput(container.getInput());
	}

	@Override
	public void leave(GameContainer container, StateBasedGame game)
			throws SlickException {

		stateInfo.getResourceManager().reinitialize();
		stateInfo.setInitialized(false);
		stateInfo.getInput().clear();

		super.leave(container, game);
	}

	@Override
	public void doRender(PaddedGameContainer container, StateBasedGame game, Graphics g) 
	{
		if (stateInfo.isInitialized())
		{
			float xOffset = stateInfo.getCamera().getLocationX() % stateInfo.getCurrentMap().getTileRenderWidth();
			float yOffset = stateInfo.getCamera().getLocationY() % stateInfo.getCurrentMap().getTileRenderHeight();

			tileMapRenderer.render(xOffset, yOffset, stateInfo.getCamera(), g, stateInfo.getPaddedGameContainer());
			spriteRenderer.render(g);
			cinematicManager.render(g);
			tileMapRenderer.renderForeground(xOffset, yOffset, stateInfo.getCamera(), g, stateInfo.getPaddedGameContainer());
			panelRenderer.render(g);
			cinematicManager.renderPostEffects(g);
			menuRenderer.render(g);
		}
		
		if (image != null) {
			g.resetTransform();
			image.draw(0, 0);
			image = null;
		}
	}

	@Override
	public void doUpdate(PaddedGameContainer container, StateBasedGame game, int delta)
			throws SlickException
	{
		stateInfo.processMessages();

		if (stateInfo.isInitialized() && !stateInfo.isWaiting())
		{
			menuManager.update(delta);
			cinematicManager.update(delta);
			if (!menuManager.isBlocking() && !cinematicManager.isBlocking())
			{
				panelManager.update(delta);
				townMoveManager.update(delta);
			}
			stateInfo.getCurrentMap().update(delta);
			spriteManager.update(delta);
			soundManager.update(delta);

			if (System.currentTimeMillis() > stateInfo.getInputDelay())
			{
				if (container.getInput().isKeyDown(Input.KEY_ESCAPE))
				{
					game.enterState(CommRPG.STATE_GAME_MENU_DEVEL);
				}
				else if (container.getInput().isKeyDown(KeyMapping.BUTTON_3) && !stateInfo.areMenusDisplayed())
				{
					if (!menuManager.isBlocking() && !cinematicManager.isBlocking())
					{
						stateInfo.sendMessage(MessageType.INVESTIGATE);
						stateInfo.setInputDelay(System.currentTimeMillis() + 200);
					}
				}
				else if (container.getInput().isKeyDown(KeyMapping.BUTTON_1) && !stateInfo.areMenusDisplayed())
				{
					stateInfo.sendMessage(new Message(MessageType.SHOW_HEROES));
				}
				else if (container.getInput().isKeyDown(Input.KEY_ENTER))
				{
					stateInfo.sendMessage(MessageType.SHOW_DEBUG);
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				}
				else if (container.getInput().isKeyDown(Input.KEY_F7))
				{
					((CommRPG) game).toggleFullScreen();
				}
				// Key for debugging menus
				else if (container.getInput().isKeyDown(Input.KEY_Z))
				{
					// image = null;
					
					// container.getGraphics().copyArea(image, 0, 0);
					// image.flushPixelData();
					// 	stateInfo.sendMessage(new Message(MessageType.SHOW_HEROES));
					// stateInfo.sendMessage(new ShopMessage(1.2, .8, new int[] {1, 1, 2, 2, 0, 0, 1, 1, 2, 2, 0, 0}, "Noah"));
					
					/*
					ArrayList<CombatSprite> multiJoinSprites = new ArrayList<>();
					multiJoinSprites.add(stateInfo.getHeroById(0));
					multiJoinSprites.add(stateInfo.getHeroById(1));
					stateInfo.sendMessage(new SpriteContextMessage(
							MessageType.SHOW_PANEL_MULTI_JOIN_CHOOSE, multiJoinSprites));
							*/
					
					// stateInfo.sendMessage(MessageType.SHOW_PRIEST);
				}
			}

			stateInfo.getInput().update(delta);
		}
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_TOWN;
	}

	@Override
	public void stateLoaded(FCResourceManager resourceManager) {
		this.stateInfo.setResourceManager(resourceManager);
	}

	public StateInfo getStateInfo() {
		return stateInfo;
	}
	
	public Image getStateImageScreenshot(boolean showHero) throws SlickException {
		PaddedGameContainer container = stateInfo.getPaddedGameContainer();
		Image image = new Image(container.getPaddedWidth(), container.getHeight());
		if (!showHero)
			stateInfo.getCurrentSprite().setVisible(false);
		render(container, null, container.getGraphics());
		container.getGraphics().copyArea(image, 0, 0);
		if (!showHero)
			stateInfo.getCurrentSprite().setVisible(true);
		container.getGraphics().resetTransform();
		return image;
	}
}
