package mb.fc.engine;

import java.awt.Dimension;
import java.util.Random;

import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import mb.fc.engine.config.DefaultEngineConfiguration;
import mb.fc.engine.config.EngineConfigurator;
import mb.fc.engine.config.LOVEngineConfigration;
import mb.fc.engine.log.FileLogger;
import mb.fc.engine.state.BattleState;
import mb.fc.engine.state.CinematicState;
import mb.fc.engine.state.LOVAttackCinematicState;
import mb.fc.engine.state.MenuState;
import mb.fc.engine.state.TownState;
import mb.fc.engine.state.devel.DevelAnimationViewState;
import mb.fc.engine.state.devel.DevelBattleAnimViewState;
import mb.fc.engine.state.devel.DevelMenuState;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCLoadingRenderSystem;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.loading.LoadingState;
import mb.fc.loading.TextParser;

/**
 * Entry point to the CommRPG game
 *
 * @author Broked
 *
 */
public class CommRPG extends StateBasedGame   {
	public static final int STATE_GAME_MENU_DEVEL = 1;

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

	public static final int STATE_GAME_ANIM_VIEW = 8;
	public static final int STATE_GAME_MENU_MULTI = 9;
	/**
	 * State in which the game is actually being played
	 */
	public static final int STATE_GAME_TOWN = 10;
	
	public static final int STATE_GAME_BATTLE_ANIM_VIEW = 11;

	public static final Dimension GAME_SCREEN_SIZE = new Dimension(320, 240);
	// public static final Dimension GAME_SCREEN_SIZE = new Dimension(256, 192);
	
	public static int GAME_SCREEN_SCALE = 1;
	public static int GAME_SCREEN_PADDING = 0;

	/**
	 * A global random number generator
	 */
	public static Random RANDOM = new Random();

	private LoadingState loadingState;

	public static String IP;

	private static int fullScreenWidth, fullScreenHeight;

	public static final String VERSION = "DEV 1.372 Feb 2, 2018";
	public static final String FILE_VERSION = "LoV-Dev";

	public static final String GAME_TITLE = "Legacies of Veridocia";

	public static boolean TEST_MODE_ENABLED = false; //true;

	public static boolean DEV_MODE_ENABLED = true;
	
	public static boolean BATTLE_MODE_OPTIMIZE = false;
	
	public static boolean MUTE_MUSIC = false;

	private static DEBUG_HOLDER DH;
	
	public static TextParser TEXT_PARSER = new TextParser();
	
	public static EngineConfigurator engineConfiguratior = new DefaultEngineConfiguration();

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
	 * Entry point into the game
	 */
	public static void main(String args[])
	{
		CommRPG rpg = new CommRPG();
		rpg.engineConfiguratior = new LOVEngineConfigration();
		rpg.setup();
	}
	
	public void setup() {
		// Setup a game container: set it's display mode and target
				// frame rate
				try
				{
					Log.debug("Starting engine version " + VERSION) ;
					PaddedGameContainer container = new PaddedGameContainer(this);

					// TODO We want to keep the same screen resolution ratio but then just expand the vertical black bars. Potentially put menus in the bars
					fullScreenWidth = 0;
					fullScreenHeight = Integer.MAX_VALUE;


					try {
						double ratio =  container.getScreenWidth() * 1.0 / container.getScreenHeight();
						Log.debug("Screen Ratio: " + ratio);
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

						Log.debug("Fullscreen dimensions: " + fullScreenWidth + " " + fullScreenHeight);
					} catch (LWJGLException e) {
						e.printStackTrace();
					}

					if (fullScreenWidth == 0)
					{
						Log.debug("Unable to enter full screen");
					}
					
					for (DisplayMode dm : Display.getAvailableDisplayModes())
						Log.debug("Supported display modes " + dm);
					container.setDisplayPaddingX(0);
					
					try
					{
						GAME_SCREEN_SCALE = 3;
						// container.setDisplayMode(GAME_SCREEN_SIZE.width * GAME_SCREEN_SCALE, 
							//	GAME_SCREEN_SIZE.height * GAME_SCREEN_SCALE, false);
						container.setDisplayMode(GAME_SCREEN_SIZE.width * GAME_SCREEN_SCALE, 
								GAME_SCREEN_SIZE.height * GAME_SCREEN_SCALE, false);
						GAME_SCREEN_PADDING = 0;
					}
					catch (SlickException se)
					{
						container.setDisplayMode(GAME_SCREEN_SIZE.width, GAME_SCREEN_SIZE.height, false);
					}
	
					container.setShowFPS(true);
					container.setVSync(true);
					container.setAlwaysRender(true);
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
		DH =  new DEBUG_HOLDER(0);
		Log.setLogSystem(new FileLogger());
	}

	/**
	 * Initialize all of the game states. This is called automatically
	 * after the game container is initialized
	 */
	@Override
	public void initStatesList(GameContainer gameContainer) throws SlickException
	{
		loadingState = new LoadingState(STATE_GAME_LOADING);
		this.addState(new MenuState());
		this.addState(new LOVAttackCinematicState());
		this.addState(new DevelMenuState());
		this.addState(new DevelAnimationViewState());
		this.addState(loadingState);
		this.addState(new DevelBattleAnimViewState());
		addState(new BattleState());
		addState(new TownState());
		addState(new CinematicState());

		// this.addState(new TestState());

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


		/*************************************/
		/* Uncomment for multiplayer support */
		/*************************************/
		/*
		this.addState(new MultiplayerMenuState());
		loadingState.setLoadingInfo("/menu/MainMenu", null, false, true,
				new FCResourceManager(),
					(LoadableGameState) this.getState(STATE_GAME_MENU_MULTI),
						new FCLoadingRenderSystem(gameContainer));
						*/

		/******************************/
		/* Comment during multiplayer */
		/******************************/

		loadingState.setLoadingInfo("/menu/MainMenu", false, true,
				new FCResourceManager(),
					(LoadableGameState) this.getState(STATE_GAME_MENU_DEVEL),
						new FCLoadingRenderSystem(gameContainer));





		this.enterState(STATE_GAME_LOADING);
	}

	public void toggleFullScreen() throws SlickException
	{
		if (this.getContainer().isFullscreen())
		{
			 ((PaddedGameContainer)this.getContainer()).setDisplayMode(320 * 3,
					240 * 3, false);
			 ((PaddedGameContainer)this.getContainer()).setDisplayPaddingX(0);
			 this.getContainer().setMouseGrabbed(false);
			 GAME_SCREEN_PADDING = 0;
			 GAME_SCREEN_SCALE = 3;
		}
		else
		{
			if (fullScreenWidth != 0)
			{
				GAME_SCREEN_SCALE = fullScreenHeight / 240;
				((PaddedGameContainer) this.getContainer()).setDisplayPaddingX((fullScreenWidth - GAME_SCREEN_SIZE.width * GAME_SCREEN_SCALE) / 2);
				GAME_SCREEN_PADDING = ((PaddedGameContainer) this.getContainer()).getDisplayPaddingX();
				 ((PaddedGameContainer)this.getContainer()).setDisplayMode(fullScreenWidth, fullScreenHeight, true);
			}
		}
	}
	
	public static int getTestMultiplier()
	{
		return 15;
	}

	public void setEngineConfiguratior(EngineConfigurator engineConfiguratior) {
		this.engineConfiguratior = engineConfiguratior;
	}
}
