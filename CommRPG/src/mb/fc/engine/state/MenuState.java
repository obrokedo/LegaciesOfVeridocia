package mb.fc.engine.state;

import java.io.File;

import javax.swing.JOptionPane;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import mb.fc.engine.CommRPG;
import mb.fc.engine.config.EngineConfigurationValues;
import mb.fc.game.Camera;
import mb.fc.game.dev.DevParams;
import mb.fc.game.input.FCInput;
import mb.fc.game.persist.ClientProfile;
import mb.fc.game.persist.ClientProgress;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.loading.FCLoadingRenderSystem;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.loading.LoadingState;
import mb.fc.utils.StringUtils;

/**
 * State that handles the main menu
 *
 * @author Broked
 *
 */
public class MenuState extends LoadableGameState
{
	public enum LoadTypeEnum
	{
		TOWN,
		CINEMATIC,
		BATTLE
	}

	protected StateBasedGame game;
	protected GameContainer gc;
	protected String version = CommRPG.VERSION;
	protected Font font;
	protected boolean initialized = false;
	protected FCInput input;
	protected int stateIndex = 0;
	protected int menuIndex = 0;
	protected int updateDelta = 0;
	protected PersistentStateInfo persistentStateInfo;
	private Image bgImage;
	private Image flor;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {

		this.game = game;
		this.gc = container;
		input = new FCInput();
	}
	
	

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		super.enter(container, game);
		gc.getInput().removeAllKeyListeners();
		gc.getInput().addKeyListener(this);
	}



	/**
	 * Initializes this state, this only gets called when coming
	 * from a loading state
	 */
	@Override
	public void initAfterLoad() {
	}

	@Override
	public void doRender(PaddedGameContainer container, StateBasedGame game, Graphics g)
	{

		if (initialized)
		{
			// g.setColor(new Color(171, 194, 208));
			// g.fillRect(0, 0, gc.getWidth(), gc.getHeight());
			// g.drawImage(bgImage, 0, 0);
			bgImage.draw(0, 70, .62f);
			if (stateIndex == 0)
			{
				if (menuIndex == 0)
					g.setColor(Color.blue);
				else
					g.setColor(Color.white);
				StringUtils.drawString("Press Enter to Start Demo", 120, 180, g);

				if (menuIndex == 1)
					g.setColor(Color.blue);
				else
					g.setColor(Color.white);
				StringUtils.drawString("Credits", 145, 200, g);

				if (menuIndex == 2)
					g.setColor(Color.blue);
				else
					g.setColor(Color.white);
				StringUtils.drawString("Exit", 150, 220, g);
			}
			else if (stateIndex == 1)
			{
				g.setColor(Color.black);
				g.drawString("Thanks to Musical Contributions from Newgrounds:", (container.getWidth() - g.getFont().getWidth("Thanks to Musical Contributions from Newgrounds:")) / 2, container.getHeight() * .005f + 90);
				g.setColor(Color.yellow);
				g.drawString("Remote Attack by dem0lecule", (container.getWidth() - g.getFont().getWidth("Remote Attack by dem0lecule")) / 2, container.getHeight() * .005f + 120);
				g.drawString("The Tense Battle by Sephirot24", (container.getWidth() - g.getFont().getWidth("The Tense Battle by Sephirot24")) / 2, container.getHeight() * .005f + 150);
				g.drawString("Shark Patrol by Ben Tibbetts", (container.getWidth() - g.getFont().getWidth("Shark Patrol by Ben Tibbetts")) / 2, container.getHeight() * .005f + 180);
				g.drawString("Hero Music by Benmode", (container.getWidth() - g.getFont().getWidth("Hero Music by Benmode")) / 2, container.getHeight() * .005f + 210);
				g.setColor(Color.black);
				g.drawString("Special Thanks to Everyone at SFC!", (container.getWidth() - g.getFont().getWidth("Special Thanks to Everyone at SFC!")) / 2, container.getHeight() * .005f + 270);
				g.setColor(Color.red);
				g.drawString("Back", (container.getWidth() - g.getFont().getWidth("Back")) / 2, container.getHeight() * .005f + 330);
			}

			g.setColor(Color.white);
			g.drawString("Version: " + version, 15, container.getHeight() - 30);
			g.setFont(font);
			g.drawString(CommRPG.GAME_TITLE, (container.getWidth() - font.getWidth(CommRPG.GAME_TITLE)) / 2, container.getHeight() * .005f - 15);
		}
	}
	
	public void startCinematic( String mapData, int cinematicId)
	{
		persistentStateInfo.loadCinematic(mapData, cinematicId);
		
		if (gc.isFullscreen())
			gc.setMouseGrabbed(true);

		game.enterState(CommRPG.STATE_GAME_LOADING);
	}

	public void start(LoadTypeEnum loadType, String mapData, String entrance)
	{		
		switch (loadType)
		{
			case CINEMATIC:
				persistentStateInfo.loadCinematic(mapData, 0);
				break;
			case TOWN:
				persistentStateInfo.loadMap(mapData, entrance);
				break;
			case BATTLE:
				persistentStateInfo.loadBattle(mapData, entrance, 0);
			break;
		}

		if (gc.isFullscreen())
			gc.setMouseGrabbed(true);

		game.enterState(CommRPG.STATE_GAME_LOADING);
	}
	
	@Override
	public void keyPressed(int key, char c) {
		super.keyPressed(key, c);
		
		if (initialized) {
			if (key == Input.KEY_F1)
			{
				((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setLoadingInfo("/menu/MainMenu", false, true,
						new FCResourceManager(),
							(LoadableGameState) game.getState(CommRPG.STATE_GAME_MENU_DEVEL),
								new FCLoadingRenderSystem(gc));

				game.enterState(CommRPG.STATE_GAME_LOADING);
			}

			if (updateDelta != 0)
				return;

			if (key == Input.KEY_ENTER)
			{
				EngineConfigurationValues jcv = CommRPG.engineConfiguratior.getConfigurationValues();
				if (menuIndex == 0 && stateIndex == 0)
					start(LoadTypeEnum.valueOf(jcv.getStartingState()), 
							jcv.getStartingMapData(), jcv.getStartingLocation());
				else if (menuIndex == 0 && stateIndex == 1)
				{
					stateIndex = 0;
					menuIndex = 1;
					updateDelta = 200;
				}
				else if (menuIndex == 1)
				{
					stateIndex = 1;
					menuIndex = 0;
					updateDelta = 200;
				}
				else if (menuIndex == 2)
					System.exit(0);
			}
			else if (key == Input.KEY_F7)
			{
				try {
					((CommRPG) this.game).toggleFullScreen();
				} catch (SlickException e) {
					Log.error("Unable to toggle fullscreen mode: " + e.getMessage());
					JOptionPane.showMessageDialog(null, "Unable to toggle fullscreen mode: " + e.getMessage());
				}
				updateDelta = 200;
			}
			else if (key == Input.KEY_UP && menuIndex > 0)
			{
				menuIndex--;
				updateDelta = 200;
			}
			else if (key == Input.KEY_DOWN && menuIndex < 2)
			{
				menuIndex++;
				updateDelta = 200;
			}
		}
	}

	@Override
	public void doUpdate(PaddedGameContainer container, StateBasedGame game, int delta)
			throws SlickException {
		if (initialized)
		{
			if (updateDelta != 0)
			{
				updateDelta = Math.max(0, updateDelta - delta);
				return;
			}
		}
	}

	@Override
	public void stateLoaded(FCResourceManager resourceManager) {
		gameSetup(game, gc);
		font = resourceManager.getFontByName("menufont");
		bgImage = resourceManager.getImage("mainbg");
		initialized = true;
		
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_MENU;
	}

	public void gameSetup(StateBasedGame game, GameContainer gc)
	{
		ClientProgress clientProgress = null;
		ClientProfile clientProfile = null;

		File file = new File(".");


		for (String s : file.list())
		{
			if (s.endsWith(ClientProfile.PROFILE_EXTENSION))
			{
				clientProfile = ClientProfile.deserializeFromFile(s);
			}
			else if (s.endsWith(ClientProgress.PROGRESS_EXTENSION))
			{
				clientProgress =  ClientProgress.deserializeFromFile(s);
			}
		}

		// Check to see if a client profile has been loaded.
		if (clientProfile == null)
		{
			clientProfile = new ClientProfile("Test");

			// If Dev mode is enabled, check to see if Dev Params
			// were specified, if so then apply them to the client profile.
			// If this is "Test" mode then don't apply the dev params as
			// it may screw up the heroes in the party
			if (CommRPG.DEV_MODE_ENABLED && !CommRPG.TEST_MODE_ENABLED)
			{
				DevParams devParams = DevParams.parseDevParams();
				if (devParams != null)
					clientProfile.setDevParams(devParams);
			}

			Log.debug("Profile was created");
		}

		if (clientProgress == null)
		{
			Log.debug("Create Progress");
			clientProgress = new ClientProgress("Test");
			clientProgress.serializeToFile();
		}

		try {
			persistentStateInfo =
				new PersistentStateInfo(clientProfile, clientProgress,
						(CommRPG) game,
						new Camera(CommRPG.GAME_SCREEN_SIZE.width, CommRPG.GAME_SCREEN_SIZE.height), gc);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			System.exit(0);
		}

		((BattleState) game.getState(CommRPG.STATE_GAME_BATTLE)).setPersistentStateInfo(persistentStateInfo);
		((TownState) game.getState(CommRPG.STATE_GAME_TOWN)).setPersistentStateInfo(persistentStateInfo);
		((CinematicState) game.getState(CommRPG.STATE_GAME_CINEMATIC)).setPersistentStateInfo(persistentStateInfo);
	}
}
