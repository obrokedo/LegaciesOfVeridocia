package mb.fc.engine.state;

import java.io.File;

import mb.fc.engine.CommRPG;
import mb.fc.game.Camera;
import mb.fc.game.input.FCInput;
import mb.fc.game.listener.StringListener;
import mb.fc.game.menu.UninitializedStringMenu;
import mb.fc.game.persist.ClientProfile;
import mb.fc.game.persist.ClientProgress;
import mb.fc.game.ui.FCGameContainer;
import mb.gl2.loading.LoadableGameState;
import mb.gl2.loading.ResourceManager;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class MenuState extends LoadableGameState implements StringListener
{
	
	private ClientProfile clientProfile = null;
	private ClientProgress clientProgress = null;

	private StateBasedGame game;
	private boolean init = false;
	private UninitializedStringMenu mapNameMenu;
	private FCInput input;
	private GameContainer gc;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
		
		this.game = game;
		this.gc = container;
	}

	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException 
	{
		if (mapNameMenu != null)
		{
			mapNameMenu.render((FCGameContainer) container, g);			
		}
	}
	
	public void start(GameContainer gc, boolean cin, String map)
	{
		clientProgress = null;
		File file = new File(".");		
		for (String s : file.list())
		{
			if (s.endsWith(".profile"))
			{
				clientProfile = ClientProfile.deserializeFromFile(s);
			}
			else if (s.endsWith(".progress"))
			{
				clientProgress =  ClientProgress.deserializeFromFile(s);
			}
		}
		
		if (clientProfile == null)
		{
			clientProfile = new ClientProfile("Test");
			// clientProfile.serializeToFile();
			System.out.println("CREATE AND SAVE PROFILE");
		}
		
		if (clientProgress == null)
		{
			System.out.println("CREATE PROGRESS");
			clientProgress = new ClientProgress("Quest");
			clientProgress.serializeToFile(map, "north");			
		}
		
		PersistentStateInfo persistentStateInfo = 
				new PersistentStateInfo(clientProfile, clientProgress, (CommRPG) game, new Camera(gc.getWidth() - ((FCGameContainer) gc).getDisplayPaddingX() * 2, 
						gc.getHeight()), gc, gc.getGraphics(), true);
		
		game.addState(new BattleState(persistentStateInfo));
		game.addState(new TownState(persistentStateInfo));
		game.addState(new CinematicState(persistentStateInfo));				
		
		if (cin)
			((CommRPG) game).setLoadingInfo(map, map,
				(LoadableGameState) game.getState(CommRPG.STATE_GAME_CINEMATIC));
		else
		{
			((CommRPG) game).setLoadingInfo(map, map,
					(LoadableGameState) game.getState(CommRPG.STATE_GAME_TOWN));
		}
		
		if (gc.isFullscreen())
			gc.setMouseGrabbed(true);
		
		game.enterState(CommRPG.STATE_GAME_LOADING);
		
		/*
		((CommRPG) game).setLoadingInfo("GHLand1", "GHLand1",
				(LoadableGameState) game.getState(CommRPG.STATE_GAME_TOWN), true);
		game.enterState(CommRPG.STATE_GAME_LOADING);
		
		
		
		((CommRPG) game).setLoadingInfo("Town1", "Town1",
				(LoadableGameState) game.getState(CommRPG.STATE_GAME_TOWN), true);
		game.enterState(CommRPG.STATE_GAME_LOADING);
		*/
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException 
	{
		if (!init)
		{
			mapNameMenu = new UninitializedStringMenu(gc, "Map name to start:", this);
			init = true;
			input = new FCInput();
			container.getInput().addKeyListener(input);
		}
		else
		{
			mapNameMenu.handleMouseInput(container.getInput().getMouseX(), container.getInput().getMouseY(), 
					container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON));
		}
	}

	@Override
	public void stateLoaded(ResourceManager resourceManager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return CommRPG.STATE_GAME_MENU;
	}

	@Override
	public void stringEntered(String string, String action) {
		gc.getInput().removeKeyListener(input);
		start(game.getContainer(), action.equalsIgnoreCase("CIN"), string);				
	}
}
