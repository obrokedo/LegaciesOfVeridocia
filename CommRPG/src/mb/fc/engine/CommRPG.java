package mb.fc.engine;

import java.util.Random;

import javax.swing.JOptionPane;

import mb.fc.engine.log.FileLogger;
import mb.fc.engine.state.AttackCinematicState;
import mb.fc.engine.state.DevelMenuState;
import mb.fc.engine.state.PersistentStateInfo;
import mb.fc.engine.state.TestState;
import mb.fc.game.ui.FCGameContainer;
import mb.fc.loading.FCLoadingRenderSystem;
import mb.fc.loading.FCResourceManager;
import mb.gl2.loading.LoadableGameState;
import mb.gl2.loading.LoadingState;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

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

	public static final int STATE_GAME_TEST = 7;

	public static final int STATE_GAME_MENU_DEVEL = 8;

	/**
	 * A global random number generator
	 */
	public static Random RANDOM = new Random();

	private LoadingState loadingState;

	public static String IP;

	private PersistentStateInfo persistentStateInfo;

	public static final int[] GLOBAL_WORLD_SCALE = new int[] {3, 2};

	private static int fullScreenWidth, fullScreenHeight;

	public static final String GAME_TITLE = "Legacies of Veridocia";

	private static DEBUG_HOLDER DH;

	private class DEBUG_HOLDER
	{
		int value;

		public DEBUG_HOLDER(int value) {
			super();
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	public static int getGameInstance()
	{
		return DH.getValue();
	}

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

			// TODO We want to keep the same screen resolution ratio but then just expand the vertical black bars. Potentially put menus in the bars
			fullScreenWidth = 0;
			fullScreenHeight = Integer.MAX_VALUE;


			try {
				double ratio =  container.getScreenWidth() * 1.0 / container.getScreenHeight();
				System.out.println(ratio);
				DisplayMode[] modes = Display.getAvailableDisplayModes();

				for (DisplayMode dm : modes)
				{
					double sRatio = 1.0 * dm.getWidth() / dm.getHeight();
					if (sRatio == ratio && fullScreenHeight > dm.getHeight()
							&& dm.getHeight() % 240 == 0 && dm.getHeight() > 240)
					{
						fullScreenWidth = dm.getWidth();
						fullScreenHeight = dm.getHeight();
					}
				}

				System.out.println(fullScreenWidth + " " +fullScreenHeight);

				GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] = fullScreenHeight / 240;
				container.setDisplayPaddingX((fullScreenWidth - (GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 320)) / 2);


			} catch (LWJGLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			fullScreenWidth = 0;

			if (fullScreenWidth == 0)
			{
				System.out.println("Unable to enter full screen");
				for (DisplayMode dm : Display.getAvailableDisplayModes())
					System.out.println(dm);
				GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] = 3;
				container.setDisplayPaddingX(0);
				try
				{
					container.setDisplayMode(320 * GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 240 * GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], false);
				}
				catch (SlickException se)
				{
					GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] = 2;
					container.setDisplayMode(320 * GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 240 * GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], false);
				}
			}
			else
				container.setDisplayMode(fullScreenWidth, fullScreenHeight, true);
			// container.setDisplayPaddingX(100);
			// container.setDisplayMode(200 + 320 * GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], 240 * GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], false);

			container.setShowFPS(false);
			container.setVSync(true);
			container.setAlwaysRender(false);
			container.setTargetFrameRate(60);
			container.start();
		}
		catch (Throwable ex)
		{
			JOptionPane.showMessageDialog(null, "An error has occurred: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public CommRPG()
	{
		super(GAME_TITLE);
		DH =  new DEBUG_HOLDER(1);
		Log.setLogSystem(new FileLogger());
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
		// this.addState(new MenuState());
		this.addState(new DevelMenuState());

		this.addState(new AttackCinematicState());
		this.addState(loadingState);

		this.addState(new TestState());

		/*
		loadingState.setLoadingInfo("/loader/Test", null, false, true,
				new FCResourceManager(),
					(LoadableGameState) this.getState(STATE_GAME_TEST),
						new FCLoadingRenderSystem(gameContainer));
						*/



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
					(LoadableGameState) this.getState(STATE_GAME_MENU_DEVEL),
						new FCLoadingRenderSystem(gameContainer));


		this.enterState(STATE_GAME_LOADING);
	}

	public PersistentStateInfo getPersistentStateInfo() {
		return persistentStateInfo;
	}

	/**
	 * Sets the loading state to use existing resources that are already contained in the resource manager
	 * and to just load the specified text and map. It then transtions into the specifed next state.
	 *
	 * @param text The text file to load
	 * @param map The map file to load
	 * @param nextState The next state that should be entered once the loading is done
	 * @param fcResourceManager Existing resource manager that contains all resources already loaded
	 */
	public void setLoadingInfo(String text, String map, LoadableGameState nextState,
			FCResourceManager fcResourceManager)
	{
		loadingState.setLoadingInfo(text, map, true, false,
				fcResourceManager,
					nextState,
						new FCLoadingRenderSystem(this.getContainer()));
	}

	/**
	 * Configures the loading state to load all resoruces and once it has will load the specified
	 * map and text files. It will then move into the next specified state. This should only be called
	 * once per execution as we don't need to load the files every time.
	 *
	 * @param text The text file to load
	 * @param map The map file to load
	 * @param nextState The state that should be entered once the loading is done
	 */
	public void setLoadingInfo(String text, String map, LoadableGameState nextState)
	{
		loadingState.setLoadingInfo(text, map, true, true,
				new FCResourceManager(),
					nextState,
						new FCLoadingRenderSystem(this.getContainer()));
	}

	public void toggleFullScreen() throws SlickException
	{
		if (this.getContainer().isFullscreen())
		{
			 ((FCGameContainer)this.getContainer()).setDisplayMode(320 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()],
					240 * CommRPG.GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()], false);
			 ((FCGameContainer)this.getContainer()).setDisplayPaddingX(0);
			 this.getContainer().setMouseGrabbed(false);
		}
		else
		{
			if (fullScreenWidth != 0)
			{
				 ((FCGameContainer)this.getContainer()).setDisplayMode(fullScreenWidth, fullScreenHeight, true);
				 ((FCGameContainer)this.getContainer()).setDisplayPaddingX((fullScreenWidth - (GLOBAL_WORLD_SCALE[CommRPG.getGameInstance()] * 320)) / 2);
			}
		}
	}
}
