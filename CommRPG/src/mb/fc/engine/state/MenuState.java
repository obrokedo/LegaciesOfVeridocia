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
import mb.fc.loading.FCResourceManager;
import mb.gl2.loading.LoadableGameState;
import mb.gl2.loading.ResourceManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class MenuState extends LoadableGameState implements StringListener
{
	private StateBasedGame game;
	private UninitializedStringMenu mapNameMenu;
	private FCInput input;
	private GameContainer gc;
	private String version = "0.03";
	private Font font;
	private Font smallFont;
	private boolean initialized = false;
	private int stateIndex = 0;
	private int menuIndex = 0;
	private int updateDelta = 0;

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

		if (initialized)
		{
			if (stateIndex == 0)
			{
				if (menuIndex == 0)
					g.setColor(Color.red);
				else
					g.setColor(Color.white);
				g.drawString("Press Enter to Start Demo", (container.getWidth() - g.getFont().getWidth("Press Enter to Start Demo")) / 2, container.getHeight() * .33f + 90);

				if (menuIndex == 1)
					g.setColor(Color.red);
				else
					g.setColor(Color.white);
				g.drawString("Credits", (container.getWidth() - g.getFont().getWidth("Credits")) / 2, container.getHeight() * .33f + 120);

				if (menuIndex == 2)
					g.setColor(Color.red);
				else
					g.setColor(Color.white);
				g.drawString("Exit", (container.getWidth() - g.getFont().getWidth("Exit")) / 2, container.getHeight() * .33f + 150);
			}
			else if (stateIndex == 1)
			{
				g.setColor(Color.lightGray);
				g.drawString("Thanks to Musical Contributions from Newgrounds:", (container.getWidth() - g.getFont().getWidth("Thanks to Musical Contributions from Newgrounds:")) / 2, container.getHeight() * .33f + 90);
				g.setColor(Color.white);
				g.drawString("Remote Attack by dem0lecule", (container.getWidth() - g.getFont().getWidth("Remote Attack by dem0lecule")) / 2, container.getHeight() * .33f + 120);
				g.drawString("The Tense Battle by Sephirot24", (container.getWidth() - g.getFont().getWidth("The Tense Battle by Sephirot24")) / 2, container.getHeight() * .33f + 150);
				g.drawString("Shark Patrol by Ben Tibbetts", (container.getWidth() - g.getFont().getWidth("Shark Patrol by Ben Tibbetts")) / 2, container.getHeight() * .33f + 180);
				g.drawString("Hero Music by Benmode", (container.getWidth() - g.getFont().getWidth("Hero Music by Benmode")) / 2, container.getHeight() * .33f + 210);
				g.setColor(Color.lightGray);
				g.drawString("Special Thanks to Everyone at SFC!", (container.getWidth() - g.getFont().getWidth("Special Thanks to Everyone at SFC!")) / 2, container.getHeight() * .33f + 270);
				g.setColor(Color.red);
				g.drawString("Back", (container.getWidth() - g.getFont().getWidth("Back")) / 2, container.getHeight() * .33f + 330);
			}

			g.setColor(Color.white);
			g.drawString("Version: " + version, 15, container.getHeight() - 30);
			g.setFont(font);
			g.drawString(CommRPG.GAME_TITLE, (container.getWidth() - font.getWidth(CommRPG.GAME_TITLE)) / 2, container.getHeight() * .33f);
		}
	}

	public void start(GameContainer gc, boolean cin, String map)
	{
		gameSetup(game, gc);

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
			throws SlickException {

		/*
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
		*/

		if (initialized)
		{
			if (updateDelta != 0)
			{
				updateDelta = Math.max(0, updateDelta - delta);
				return;
			}

			if (container.getInput().isKeyDown(Input.KEY_ENTER))
			{
				if (menuIndex == 0 && stateIndex == 0)
					start(container, true, "eriumcastle");
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
			else if (container.getInput().isKeyDown(Input.KEY_F7))
			{
				((CommRPG) game).toggleFullScreen();
				updateDelta = 200;
			}
			else if (container.getInput().isKeyDown(Input.KEY_UP) && menuIndex > 0)
			{
				menuIndex--;
				updateDelta = 200;
			}
			else if (container.getInput().isKeyDown(Input.KEY_DOWN) && menuIndex < 2)
			{
				menuIndex++;
				updateDelta = 200;
			}
		}
	}

	@Override
	public void stateLoaded(ResourceManager resourceManager) {
		font = ((FCResourceManager) resourceManager).getFontByName("menufont");
		smallFont = ((FCResourceManager) resourceManager).getFontByName("smallfont");
		initialized = true;

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

	public static void gameSetup(StateBasedGame game, GameContainer gc)
	{
		ClientProgress clientProgress = null;
		ClientProfile clientProfile = null;
		String map = "";

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
	}
}
