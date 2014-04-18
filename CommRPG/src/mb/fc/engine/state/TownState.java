package mb.fc.engine.state;

import mb.fc.engine.CommRPG;
import mb.fc.engine.message.Message;
import mb.fc.game.hudmenu.Panel;
import mb.fc.game.manager.CinematicManager;
import mb.fc.game.manager.PanelManager;
import mb.fc.game.manager.MenuManager;
import mb.fc.game.manager.SoundManager;
import mb.fc.game.manager.SpriteManager;
import mb.fc.game.manager.TownMoveManager;
import mb.fc.loading.FCResourceManager;
import mb.fc.renderer.PanelRenderer;
import mb.fc.renderer.MenuRenderer;
import mb.fc.renderer.SpriteRenderer;
import mb.fc.renderer.TileMapRenderer;
import mb.gl2.loading.LoadableGameState;
import mb.gl2.loading.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

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
	
	public TownState(PersistentStateInfo psi)
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
		this.cinematicManager = new CinematicManager();
		stateInfo.registerManager(cinematicManager);
		this.soundManager = new SoundManager();
		stateInfo.registerManager(soundManager);
	}
	
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
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException {
		if (stateInfo.isInitialized())
		{
			tileMapRenderer.render(stateInfo.getCamera(), g, stateInfo.getGc());
			spriteRenderer.render(g);
			cinematicManager.render(g);
			tileMapRenderer.renderForeground(stateInfo.getCamera(), g, stateInfo.getGc());
			panelRenderer.render();
			menuRenderer.render();
			cinematicManager.renderMenu(g);
			cinematicManager.renderPostEffects(g);
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException 	
	{						
		stateInfo.processMessages();
		if (stateInfo.isInitialized())
		{				
			menuManager.update();
			cinematicManager.update(delta);
			if (!menuManager.isBlocking() && !cinematicManager.isBlocking())
			{
				panelManager.update();
				townMoveManager.update(delta);
			}
			spriteManager.update(delta);	
			soundManager.update(delta);
			
			if (System.currentTimeMillis() > stateInfo.getInputDelay())
			{
				if (container.getInput().isKeyDown(Input.KEY_ESCAPE))
				{
					stateInfo.sendMessage(Message.MESSAGE_SHOW_SYSTEM_MENU);
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				}
				/*
				else if (container.getInput().isKeyDown(Input.KEY_C))
				{
					stateInfo.sendMessage(Message.MESSAGE_SHOW_HEROES);
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				}
				else if (container.getInput().isKeyDown(Input.KEY_S))
				{
					stateInfo.sendMessage(Message.MESSAGE_SHOW_PRIEST);
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				}
				*/
				else if (container.getInput().isKeyDown(Input.KEY_D) && !stateInfo.areMenusDisplayed())
				{
					stateInfo.sendMessage(Message.MESSAGE_INVESTIGATE);
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				}
				else if (container.getInput().isKeyDown(Input.KEY_ENTER))
				{					
					stateInfo.sendMessage(Message.MESSAGE_SHOW_DEBUG);
					stateInfo.setInputDelay(System.currentTimeMillis() + 200);
				}
				else if (container.getInput().isKeyDown(Input.KEY_F7))
				{					
					((CommRPG) game).toggleFullScreen();
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
	public void stateLoaded(ResourceManager resourceManager) {	
		this.stateInfo.setResourceManager((FCResourceManager) resourceManager);
		Panel.intialize(stateInfo.getResourceManager());
		// Menu.menuBackground = this.stateInfo.getResourceManager().getImages().get("menubg");
	}
}
