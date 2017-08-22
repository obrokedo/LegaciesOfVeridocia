package mb.fc.engine.state.devel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.particles.ParticleSystem;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;

import mb.fc.engine.CommRPG;
import mb.fc.engine.state.MenuState;
import mb.fc.game.ui.Button;
import mb.fc.game.ui.ListUI;
import mb.fc.game.ui.ListUI.ResourceSelectorListener;
import mb.fc.game.ui.PaddedGameContainer;
import mb.fc.game.ui.ResourceSelector;
import mb.fc.loading.FCLoadingRenderSystem;
import mb.fc.loading.FCResourceManager;
import mb.fc.loading.LoadableGameState;
import mb.fc.loading.LoadingState;
import mb.fc.loading.MapParser;
import mb.fc.loading.TilesetParser;
import mb.fc.map.Map;
import mb.fc.map.MapObject;
import mb.fc.utils.XMLParser;
import mb.fc.utils.XMLParser.TagArea;
import mb.fc.utils.gif.GifFrame;
import mb.fc.utils.planner.PlannerFrame;

/**
 * Development menu state that allows loading maps programatically
 *
 * @author Broked
 *
 */
public class DevelMenuState extends MenuState implements ResourceSelectorListener
{
	private ResourceSelector textSelector;
	private ListUI entranceSelector;
	private PlannerFrame plannerFrame = new PlannerFrame(this);
	private GifFrame quickAnimate = new GifFrame(true);
	private ParticleSystem ps;
	private String currentMap;
	private Button loadTownButton = new Button(0, 550, 150, 25, "Load Town");
	private Button loadCinButton = new Button(0, 580, 150, 25, "Load Cin");
	private Button loadBattleButton = new Button(0, 610, 150, 25, "Load Battle");

	@Override
	public void init(GameContainer container, StateBasedGame game) throws SlickException {
		this.game = game;
		this.gc = container;
		// gameSetup(game, gc);
		// mapSelector = new ResourceSelector("Select Map", 0, true, "map", ".tmx", container);
		// mapSelector.setListener(this);
		textSelector = new ResourceSelector("Select Text", 0, true, "mapdata", "", container);
		textSelector.setListener(this);
		loadTownButton.setEnabled(false);
		loadBattleButton.setEnabled(false);
		loadCinButton.setEnabled(false);
		/*
		ps = new ParticleSystem(new Image("sprite/lightning.png"));
		JParticleEmitter emitter = GlobalPythonFactory.createParticleEmitter();
		emitter.initialize(false, new Image("sprite/lightning.png"));
		ps.addEmitter(emitter);
		*/
	}
	
	

	@Override
	public void enter(GameContainer container, StateBasedGame game) throws SlickException {
		Log.debug("Entered DevelMenuState");
		gameSetup(game, container);
	}



	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g)
			throws SlickException
	{
		g.clearClip();
		g.setColor(Color.red);
		g.drawString("DEVELOPMENT MODE", 5, 5);

		g.setColor(Color.white);

		textSelector.render(g);

		if (entranceSelector != null)
			entranceSelector.render(g);

		loadTownButton.render(g);
		loadBattleButton.render(g);
		loadCinButton.render(g);

		g.drawString(version, container.getWidth() / 2, container.getHeight() - 30);
		g.drawString("F1 - Toggle Main/Dev Menu", container.getWidth() - 250, container.getHeight() - 180);
		g.drawString("F2 - Open Planner", container.getWidth() - 250, container.getHeight() - 150);
		g.drawString("F3 - Open Quick Animator", container.getWidth() - 250, container.getHeight() - 120);
		g.drawString("F4 - Open Animation Viewer", container.getWidth() - 250, container.getHeight() - 90);
		g.drawString("F5 - Run Test", container.getWidth() - 250, container.getHeight() - 60);
		g.drawString("F6 - Open Battle Viewer", container.getWidth() - 250, container.getHeight() - 30);

		if (initialized && ps != null)
		{
			g.scale(3, 3);
			ps.render();
			g.resetTransform();
		}
	}

	@Override
	public void update(GameContainer container, StateBasedGame game, int delta)
			throws SlickException
	{
		this.isPaused(container);
		
		if (container.getInput().isKeyPressed(Input.KEY_F1))
		{
			((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setLoadingInfo("/menu/MainMenu", false, true,
					new FCResourceManager(),
						(LoadableGameState) game.getState(CommRPG.STATE_GAME_MENU),
							new FCLoadingRenderSystem(container));

			game.enterState(CommRPG.STATE_GAME_LOADING);
		}



		if (container.getInput().isKeyPressed(Input.KEY_F2) || container.getInput().isKeyPressed(Input.KEY_P))
		{
			if (!plannerFrame.isVisible())
				plannerFrame.setVisible(true);
		}

		if (container.getInput().isKeyPressed(Input.KEY_F3))
		{
			if (!quickAnimate.isVisible())
				quickAnimate.setVisible(true);
		}

		if (container.getInput().isKeyPressed(Input.KEY_F4))
		{
			game.enterState(CommRPG.STATE_GAME_ANIM_VIEW);
		}

		if (container.getInput().isKeyPressed(Input.KEY_F5))
		{
			CommRPG.TEST_MODE_ENABLED = true;
			this.gameSetup(game, container);
			start(LoadTypeEnum.CINEMATIC, "neweriumcastle", null);
		}
		
		if (container.getInput().isKeyPressed(Input.KEY_F6))
		{
			((LoadingState) game.getState(CommRPG.STATE_GAME_LOADING)).setLoadingInfo("eriumjail", true, true,
					new FCResourceManager(),
						(LoadableGameState) game.getState(CommRPG.STATE_GAME_BATTLE_ANIM_VIEW),
							new FCLoadingRenderSystem(container));

			game.enterState(CommRPG.STATE_GAME_LOADING);
		}

		if (container.getInput().isKeyDown(Input.KEY_F7))
		{
			((CommRPG) game).toggleFullScreen();
			updateDelta = 200;
			System.out.println("TOGGLE");
		}
		
		if (container.getInput().isKeyDown(Input.KEY_F8))
		{
			CommRPG.TEST_MODE_ENABLED = false;
			LoadTypeEnum loadType = LoadTypeEnum.TOWN;
			if (persistentStateInfo.getClientProgress().isBattle())
				loadType = LoadTypeEnum.BATTLE;
			start(loadType, persistentStateInfo.getClientProgress().getMapData(), null);
		}
		
		if (container.getInput().isKeyDown(Input.KEY_F9))
		{
			CommRPG.TEST_MODE_ENABLED = true;
			CommRPG.BATTLE_MODE_OPTIMIZE = true;
			if (textSelector.getSelectedResource() != null && 
					entranceSelector.getSelectedResource() != null)
				start(LoadTypeEnum.BATTLE, textSelector.getSelectedResource(), 
						entranceSelector.getSelectedResource());
		}
		
		int x = container.getInput().getMouseX();
		int y = container.getInput().getMouseY();

		loadTownButton.handleUserInput(x, y, false);
		loadCinButton.handleUserInput(x, y, false);
		loadBattleButton.handleUserInput(x, y, false);
		
		if (updateDelta > 0)
			updateDelta = Math.max(0, updateDelta - delta);
		else if (container.getInput().isMouseButtonDown(Input.MOUSE_LEFT_BUTTON))
		{
			this.updateDelta = 200;
			
			if (loadTownButton.handleUserInput(x, y, true))
				start(LoadTypeEnum.TOWN, textSelector.getSelectedResource(), entranceSelector.getSelectedResource());
			if (loadCinButton.handleUserInput(x, y, true))
				start(LoadTypeEnum.CINEMATIC, textSelector.getSelectedResource(), null);
			if (loadBattleButton.handleUserInput(x, y, true)) 
				start(LoadTypeEnum.BATTLE, textSelector.getSelectedResource(), entranceSelector.getSelectedResource());
		}
		
		textSelector.update(container, delta);

		if (entranceSelector != null) {
			entranceSelector.update(container, delta);
			loadTownButton.setEnabled(entranceSelector.getSelectedResource() != null);
			loadBattleButton.setEnabled(entranceSelector.getSelectedResource() != null);
		}

		if (initialized && ps != null)
		{
			ps.update(delta);
		}
	}

	@Override
	public void stateLoaded(FCResourceManager resourceManager) {
		this.initialized = true;
	}

	@Override
	public int getID() {
		return CommRPG.STATE_GAME_MENU_DEVEL;
	}

	@Override
	public boolean resourceSelected(String selectedItem,
			ListUI parentSelector) {
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File("mapdata/" + selectedItem)));
			String firstLine = br.readLine();
			br.close();
			
			if (firstLine.startsWith("<map")) {
				ArrayList<TagArea> tagArea = XMLParser.process(Collections.singletonList(firstLine));
				currentMap = tagArea.get(0).getAttribute("file");
			} else {
				JOptionPane.showMessageDialog(null, "The selected map data has not had a map associated with it yet.\n Load the mapdata in the planner to assign a map");
				return false;
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "The selected file could not be found");
			return false;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "The selected file could not be read or is improperly formatted: " + e.getMessage());
			return false;
		}
		
		
		Map map = new Map();
		ArrayList<String> entrances = new ArrayList<>();
		try {
			MapParser.parseMap("map/" + currentMap, map, new TilesetParser(), null);


			for (MapObject mo : map.getMapObjects())
				if (mo.getKey().equalsIgnoreCase("start"))
					entrances.add(mo.getParam("exit"));

			entranceSelector = new ListUI("Select Entrance", (((PaddedGameContainer) gc).getPaddedWidth() - 150) / 2, entrances);
			loadCinButton.setEnabled(true);
			return true;

		} catch (Throwable t) {
			t.printStackTrace();
			JOptionPane.showMessageDialog(null, "The selected map " + currentMap + " contains errors and may not be loaded: " + t.getMessage());
			entranceSelector = null;
		}
		
		loadCinButton.setEnabled(false);
		return false;
	}
}
