package mb.fc.engine;

import java.util.Random;

import mb.fc.engine.state.AttackCinematicState;
import mb.fc.engine.state.MenuState;
import mb.fc.engine.state.PersistentStateInfo;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.loading.FCLoadingRenderSystem;
import mb.fc.loading.FCResourceManager;
import mb.gl2.loading.LoadableGameState;
import mb.gl2.loading.LoadingState;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class CommRPG extends StateBasedGame   {
	/**
	 * State in which the game is actually being played
	 */
	public static final int STATE_GAME_TOWN = 1;
	
	/**
	 * State that displays and allows interaction with the "Start Menu"
	 */
	public static final int STATE_GAME_MENU = 2;
	
	/**
	 * State in which the game is actually being played
	 */
	public static final int STATE_GAME_BATTLE = 3;

	public static final int STATE_GAME_CINEMATIC= 4;
	
	public static final int STATE_GAME_LOADING = 5;
	
	/**
	 * State in which the game is actually being played
	 */
	public static final int STATE_GAME_BATTLE_ANIM = 6;
	
	/**
	 * A global random number generator
	 */
	public static Random RANDOM = new Random();
	
	private LoadingState loadingState;
	
	public static String IP;
	
	private PersistentStateInfo persistentStateInfo;
	
	/**
	 * Entry point into Eaton
	 */
	public static void main(String args[])
	{	
		// Setup a game container for Eaton and set it's display mode and target
		// frame rate
		try
		{
			CommRPG fc = new CommRPG();
			FCGameContainer container = new FCGameContainer(fc);
			container.setShowFPS(true);
									
			// TODO We want to keep the same screen resolution ratio but then just expand the vertical black bars. Potentially put menus in the bars
			
			/*
			double ratio =  container.getScreenWidth() * 1.0 / container.getScreenHeight();
			
			// We want the screen to be 768 pixels large
			int preferredScreenWidth = (int) Math.ceil(480 * ratio);

			try
			{
				container.setDisplayMode(640, 480, true);
				// container.setDisplayPaddingX((preferredScreenWidth - 640) / 2);
			}
			catch (SlickException ex)
			{
				System.out.println("ERROR finding resolution");
				container.setDisplayMode(1024, 768, true);
			} 
			*/
			
			
			container.setDisplayMode(640, 480, false);
			// container.setDisplayMode(800, 600, false);
			
			container.setVSync(true);
			container.setAlwaysRender(true);
			container.setTargetFrameRate(60);
			container.start();						
		}
		catch (SlickException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public CommRPG() 
	{
		super("Chronicles of Veridocia");
	}
	
	/**
	 * Initialize all of the game states. This is called automatically
	 * after the game container is initialized
	 */
	@Override
	public void initStatesList(GameContainer gameContainer) throws SlickException 
	{	
		/*
		String name = null;
		if (DEBUG_HOST)
		{
			name = "Broked";
		}
		else
			name = "Test";
		ClientProfile cp = ClientProfile.deserializeFromFile(name + ".profile");
		if (cp == null)
		{
			System.out.println("CREATE NEW PROFILE");
			cp = new ClientProfile(name);
		}
		
		ClientProgress cg = ClientProgress.deserializeFromFile(name + ".progress");
		if (cg == null)
		{
			System.out.println("CREATE NEW PROGRESS");
			cg = new ClientProgress(name);
		}
		
		
		persistentStateInfo = 
				new PersistentStateInfo(cp, cg, this, new Camera(gameContainer.getWidth() - ForsakenChampions.MAP_START_X * 2, 
						gameContainer.getHeight()), gameContainer, gameContainer.getGraphics(), DEBUG_HOST);
						*/
		
		LoadingState.loading = true;
		loadingState = new LoadingState(STATE_GAME_LOADING);		
		this.addState(new MenuState());
		
		this.addState(new AttackCinematicState());	
		this.addState(loadingState);
				
		// TODO Creating a "New Game" will automatically create a new save info with the current map set to the first level
		// Subsequent loads will be from wherever the user left off
		/*
		loadingState.setLoadingInfo("/level/Level1", 
				new FCResourceManager(), 
				(LoadableGameState) this.getState(STATE_GAME_TOWN), 
				new FCLoadingRenderSystem(gameContainer));
				*/
		loadingState.setLoadingInfo("/menu/MainMenu", null, false, true,
				new FCResourceManager(), 
					(LoadableGameState) this.getState(STATE_GAME_MENU), 
						new FCLoadingRenderSystem(gameContainer));
		
		this.enterState(STATE_GAME_LOADING);
	}

	public PersistentStateInfo getPersistentStateInfo() {
		return persistentStateInfo;
	}
	
	public void setLoadingInfo(String text, String map, LoadableGameState nextState, 
			FCResourceManager fcResourceManager)
	{
		loadingState.setLoadingInfo(text, map, true, false,
				fcResourceManager, 
					nextState, 
						new FCLoadingRenderSystem(this.getContainer()));
	}
	
	public void setLoadingInfo(String text, String map, LoadableGameState nextState, boolean loadResources)
	{
		loadingState.setLoadingInfo(text, map, true, true,
				new FCResourceManager(), 
					nextState, 
						new FCLoadingRenderSystem(this.getContainer()));
	}
}
